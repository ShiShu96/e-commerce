package com.xy.ecommerce.service;

import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.entity.Order;
import com.xy.ecommerce.vo.OrderVo;

public interface OrderService {

    Response<OrderVo> createOrder(Integer userId, Integer shippingId);

    Response cancelOrder(Integer userId, Long orderNo);

    Response<Order> getOrderDetail(Integer userId, Long orderNo);
}
