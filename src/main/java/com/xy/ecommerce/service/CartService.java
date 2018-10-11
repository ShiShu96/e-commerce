package com.xy.ecommerce.service;

import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.vo.CartVo;

public interface CartService {

    Response<CartVo> add(Integer userId, Integer productId, Integer count);

    Response<CartVo> update(Integer userId, Integer productId, Integer count);

    Response<CartVo> delete(Integer userId, String productStr);

    Response<CartVo> list(Integer userId);

    Response<CartVo> selectUnSelect(Integer userId, Integer checked, Integer productId);
}
