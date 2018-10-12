package com.xy.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.dao.ShippingMapper;
import com.xy.ecommerce.entity.Shipping;
import com.xy.ecommerce.service.ShippingService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public Response add(Integer userId, Shipping shipping){
        if (shipping==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            return Response.createBySuccess();
        }
        return Response.createByError();
    }

    @Override
    public Response delete(Integer userId,Integer shippingId){
        if (shippingId==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        int resultCount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if(resultCount > 0){
            return Response.createBySuccess();
        }
        return Response.createByError();
    }

    @Override
    public Response update(Integer userId, Shipping shipping){
        if (shipping==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount > 0){
            return Response.createBySuccess();
        }
        return Response.createByError();
    }

    @Override
    public Response<Shipping> select(Integer userId, Integer shippingId){
        if (shippingId==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if(shipping!=null){
            return Response.createBySuccess(shipping);
        }
        return Response.createByError();
    }

    @Override
    public Response<PageInfo> list(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return Response.createBySuccess(pageInfo);
    }
}
