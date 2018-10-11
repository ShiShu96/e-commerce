package com.xy.ecommerce.service;

import com.github.pagehelper.PageInfo;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.entity.Shipping;

public interface ShippingService {
    Response add(Integer userId, Shipping shipping);

    Response delete(Integer userId,Integer shippingId);

    Response update(Integer userId, Shipping shipping);

    Response<Shipping> select(Integer userId,Integer shippingId);

    Response<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
