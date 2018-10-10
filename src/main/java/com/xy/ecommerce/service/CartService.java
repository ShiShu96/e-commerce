package com.xy.ecommerce.service;

import com.xy.ecommerce.common.Response;

public interface CartService {

    Response add(Integer userId, Integer productId, int count)
}
