package com.xy.ecommerce.service.impl;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.dao.CartMapper;
import com.xy.ecommerce.dao.ProductMapper;
import com.xy.ecommerce.entity.Cart;
import com.xy.ecommerce.entity.Product;
import com.xy.ecommerce.service.CartService;
import com.xy.ecommerce.util.BigDecimalUtil;
import com.xy.ecommerce.util.PropertiesUtil;
import com.xy.ecommerce.vo.CartProductVo;
import com.xy.ecommerce.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Response<CartVo> add(Integer userId, Integer productId, Integer count){
        if (productId==null || count==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart==null){
            //add a new cart
            Cart newCart=new Cart();
            newCart.setQuantity(count);
            newCart.setChecked(Const.CART_CHECKED);
            newCart.setProductId(productId);
            newCart.setUserId(userId);

            cartMapper.insert(newCart);
        } else {
            // already in cart
            count+=cart.getQuantity();
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public Response<CartVo> update(Integer userId, Integer productId, Integer count){
        if (productId==null || count==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId, productId);
        if(cart!=null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }
        return list(userId);
    }

    @Override
    public Response<CartVo> selectUnSelect(Integer userId, Integer checked, Integer productId){
        cartMapper.checkUncheckProduct(userId, checked, productId);
        return list(userId);
    }

    @Override
    public Response<CartVo> list(Integer userId){
        CartVo cartVo=getCartVo(userId);
        return Response.createBySuccess(cartVo);
    }

    @Override
    public Response<CartVo> delete(Integer userId, String productStr){
        String[] productIds=productStr.split(",");
        if (productIds.length==0){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        cartMapper.deleteByUserIdProductIds(userId, productIds);
        return list(userId);
    }

    private CartVo getCartVo(Integer userId){
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList=new ArrayList<>();

        CartVo cartVo=new CartVo();

        BigDecimal cartTotalPrice=new BigDecimal("0");

        if (!CollectionUtils.isEmpty(cartList)){
            for (Cart cart : cartList){
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());

                Product product=productMapper.selectByPrimaryKey(cart.getId());
                if (product!=null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    int limitCount=0;
                    if (cart.getQuantity()<=product.getStock()){
                        // enough stock
                        limitCount=cart.getQuantity();
                    } else {
                        limitCount=product.getStock();
                        Cart limitQuantityCart=new Cart();
                        limitQuantityCart.setId(cart.getId());
                        limitQuantityCart.setQuantity(limitCount);
                        cartMapper.updateByPrimaryKeySelective(limitQuantityCart);
                    }
                    cartProductVo.setQuantity(limitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                if(cart.getChecked() == Const.CART_CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.prefix"));

        return null;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if (userId==null) return false;
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

}
