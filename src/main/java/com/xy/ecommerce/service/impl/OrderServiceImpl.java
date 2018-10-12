package com.xy.ecommerce.service.impl;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.dao.*;
import com.xy.ecommerce.entity.*;
import com.xy.ecommerce.service.OrderService;
import com.xy.ecommerce.util.BigDecimalUtil;
import com.xy.ecommerce.util.DatetimeUtil;
import com.xy.ecommerce.util.PropertiesUtil;
import com.xy.ecommerce.vo.OrderItemVo;
import com.xy.ecommerce.vo.OrderVo;
import com.xy.ecommerce.vo.ShippingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public Response<OrderVo> createOrder(Integer userId, Integer shippingId) {
        List<Cart> cartList=cartMapper.selectCheckedCartByUserId(userId);
        // calculate total price
        Response<List<OrderItem>> response=getCartOrderItem(userId, cartList);
        if(!response.isSuccess()){
            return Response.createByError(ResponseCode.getResponseCode(response.getStatus()));
        }
        List<OrderItem> orderItemList=response.getData();
        BigDecimal payment=getOrderTotalPrice(orderItemList);

        // create order
        Order order=assembleOrder(userId, shippingId, payment);

        // insert into the database
        int count=orderMapper.insert(order);
        if(count<=0){
            return Response.createByError();
        }
        for (OrderItem item:orderItemList){
            item.setOrderNo(order.getOrderNo());
        }

        // bunch insert
        orderItemMapper.batchInsert(orderItemList);

        // reduce stock
        reduceProductStock(orderItemList);

        // clear cart
        cleanCart(cartList);

        OrderVo orderVo=assembleOrderVo(order, orderItemList);

        return Response.createBySuccess(orderVo);
    }

    @Override
    public Response cancelOrder(Integer userId, Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order==null || order.getStatus()==Const.ORDER_CANCELLED){
            return Response.createByError(ResponseCode.ORDER_NOT_EXISTS);
        }
        Order upddateOrder=new Order();
        upddateOrder.setId(order.getId());
        upddateOrder.setStatus(Const.ORDER_CANCELLED);

        List<OrderItem> orderItemList=orderItemMapper.selectByOrderNo(orderNo);
        for (OrderItem item:orderItemList){
            Product product=productMapper.selectByPrimaryKey(item.getProductId());
            product.setStock(product.getStock()+item.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }

        int count=orderMapper.updateByPrimaryKeySelective(upddateOrder);
        if (count>0){
            return Response.createBySuccess();
        }
        return Response.createByError();
    }

    @Override
    public Response<Order> getOrderDetail(Integer userId, Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order==null){
            return Response.createByError(ResponseCode.ORDER_NOT_EXISTS);
        }
        return Response.createBySuccess(order);
    }

    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.ONLINE_PAY_MSG);

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.ORDER_NOT_PAY_MSG);

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DatetimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DatetimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DatetimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DatetimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DatetimeUtil.dateToStr(order.getCloseTime()));


        List<OrderItemVo> orderItemVoList = new ArrayList<>();

        for(OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }


    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DatetimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }



    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    private void cleanCart(List<Cart> cartList){
        for(Cart cart : cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for(OrderItem orderItem : orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment){
        Order order=new Order();

        order.setOrderNo(generateOrderNo());
        order.setStatus(Const.ORDER_NOT_PAY);
        order.setPostage(0);
        order.setPaymentType(Const.ONLINE_PAY);
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);

        return order;
    }

    private long generateOrderNo(){
        long curTime=System.currentTimeMillis();
        return curTime+new Random().nextInt(100);
    }


    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment=new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            payment=BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private Response<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList){
        List<OrderItem> orderItemList=new ArrayList<>();
        if (CollectionUtils.isEmpty(cartList)){
            return Response.createByError(ResponseCode.EMPTY_CART);
        }
        for (Cart cart:cartList){
            OrderItem orderItem=new OrderItem();
            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus()!=Const.PRODUCT_STATUS_SELLING){
                return Response.createByError(ResponseCode.PRODUCT_SOLD_OUT_OR_DELETED);
            }
            if(cart.getQuantity()>product.getStock()){
                return Response.createByError(ResponseCode.PRODUCT_STOCK_NOT_ENOUGH);
            }
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity()));
            orderItemList.add(orderItem);
        }
        return Response.createBySuccess(orderItemList);
    }
}
