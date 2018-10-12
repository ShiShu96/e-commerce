package com.xy.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.dao.CategoryMapper;
import com.xy.ecommerce.dao.ProductMapper;
import com.xy.ecommerce.entity.Category;
import com.xy.ecommerce.entity.Product;
import com.xy.ecommerce.service.ProductService;
import com.xy.ecommerce.util.DatetimeUtil;
import com.xy.ecommerce.util.PropertiesUtil;
import com.xy.ecommerce.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Response saveOrUpdateProduct(Product product){
        if(product!=null){
            // update
            if (product.getId()!=null){
                int count=productMapper.updateByPrimaryKey(product);
                if (count>0)
                    return Response.createBySuccess();
                return Response.createByError();
            } else {
                // add a new product
              int count=productMapper.insert(product);
              if(count>0)
                  return Response.createBySuccess();
              return Response.createByError();
            }
        } else {
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
    }

    @Override
    public Response setSaleStatus(Integer productId, Integer status) {
        if (productId==null || status==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int count=productMapper.updateByPrimaryKeySelective(product);
        if (count>0){
            return Response.createBySuccess();
        }

        return Response.createByError();
    }

    @Override
    public Response<ProductDetailVo> getProductDetail(Integer productId){
        if (productId==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return Response.createByError(ResponseCode.PRODUCT_NOT_FOUND);
        }
        if (product.getStatus() != Const.PRODUCT_STATUS_SELLING){
            return Response.createByError(ResponseCode.PRODUCT_SOLD_OUT_OR_DELETED);
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return Response.createBySuccess(productDetailVo);
    }

    @Override
    public Response<PageInfo> getProductList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList=productMapper.selectList();

        List<ProductDetailVo> list=new ArrayList<>();
        for (Product product : productList) {
            list.add(assembleProductDetailVo(product));
        }
        PageInfo pageInfo=new PageInfo();
        pageInfo.setList(list);
        return Response.createBySuccess(pageInfo);
    }

    @Override
    public Response<List<ProductDetailVo>> searchProduct(String productName, Integer productId){
        if (!StringUtils.isEmpty(productName)){
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList=productMapper.selectByNameAndId(productName, productId);

        List<ProductDetailVo> list=new ArrayList<>();
        for (Product product : productList) {
            list.add(assembleProductDetailVo(product));
        }

        return Response.createBySuccess(list);
    }


    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());


        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DatetimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DatetimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

}
