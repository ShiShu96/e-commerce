package com.xy.ecommerce.service;

import com.github.pagehelper.PageInfo;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.entity.Product;
import com.xy.ecommerce.vo.ProductDetailVo;

import java.util.List;

public interface ProductService {
    Response<String> saveOrUpdateProduct(Product product);

    Response<String> setSaleStatus(Integer productId, Integer status);

    Response<ProductDetailVo> getProductDetail(Integer productId);

    Response<PageInfo> getProductList(int pageNum, int pageSize);

    Response<List<ProductDetailVo>> searchProduct(String productName, Integer productId);
}
