package com.xy.ecommerce.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.dao.CategoryMapper;
import com.xy.ecommerce.dao.ProductMapper;
import com.xy.ecommerce.dao.cache.ProductCacheDao;
import com.xy.ecommerce.entity.Category;
import com.xy.ecommerce.entity.Product;
import com.xy.ecommerce.service.ProductService;
import com.xy.ecommerce.util.DatetimeUtil;
import com.xy.ecommerce.vo.ProductDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private static Logger logger=LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private ProductCacheDao productCacheDao=new ProductCacheDao();

    @Override
    public Response saveOrUpdateProduct(Product product){
        if(product!=null){
            // update
            if (product.getId()!=null){
                // double deletion + delay to guarantee database and cache consistence
                boolean result=updateProduct(product);
                if (result)
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
        boolean result=updateProduct(product);
        if (result)
            return Response.createBySuccess();
        return Response.createByError();
    }

    private boolean updateProduct(Product product){
        productCacheDao.deleteProduct(product.getId());
        int count=productMapper.updateByPrimaryKeySelective(product);
        if (count>0){
            try {
                Thread.sleep(300);
                productCacheDao.putProduct(product);
            } catch (InterruptedException e){
                logger.error(e.getMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    public Response<ProductDetailVo> getProductDetail(Integer productId, boolean isAdminOpt){
        if (productId==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        // read from redis
        Product product=productCacheDao.getProduct(productId);
        if (product==null){
            // not found in redis, read from database
            product=productMapper.selectByPrimaryKey(productId);
            if (product==null){
                return Response.createByError(ResponseCode.PRODUCT_NOT_FOUND);
            } else {
                // put in redis
                productCacheDao.putProduct(product);
            }
        }
        if (!isAdminOpt)
            productCacheDao.increaseProductViews(productId);

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

    public Response<List<Map<String, Integer>>> getRankList(){
        List<Map<String, Integer>> rankList=productCacheDao.productRankList();
        return Response.createBySuccess(rankList);
    }

    public Response<Integer> getRank(Integer productId){
        if (productId==null){
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        int rank=productCacheDao.productRank(productId);
        return Response.createBySuccess(rank);
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
