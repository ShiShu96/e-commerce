package com.xy.ecommerce.service.impl;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.dao.CartMapper;
import com.xy.ecommerce.entity.Cart;
import com.xy.ecommerce.service.CartService;
import com.xy.ecommerce.vo.CartProductVo;
import com.xy.ecommerce.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public Response add(Integer userId, Integer productId, int count){
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

        return null;
    }

    private CartVo getCartVo(Integer userId){
        List<Cart> cart=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList=new ArrayList<>();

        CartVo cartVo=new CartVo();

        return null;
    }
}
