package com.xy.ecommerce.dao.cache;

import com.xy.ecommerce.dao.ProductMapper;
import com.xy.ecommerce.entity.Product;
import com.xy.ecommerce.vo.ProductDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import redis.clients.jedis.Tuple;

import java.util.*;

public class ProductCacheDao {

    @Autowired
    private ProductMapper mapper;

    private static Logger logger=LoggerFactory.getLogger(ProductCacheDao.class);

    private static JedisPool jedisPool;

    private  static final String PREFIX="product";
    private  static final String VIEWS_LIST="product.views";

    private RuntimeSchema<Product> schema=RuntimeSchema.createFrom(Product.class);

    public ProductCacheDao(){

        jedisPool = new JedisPool();
    }

    public boolean deleteProduct(Integer productId){
        try{
            Jedis jedis=jedisPool.getResource();
            try {
                String key=PREFIX+productId;
                jedis.del(key.getBytes());
                return true;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return false;
    }

    public Product getProduct(Integer productId){
        try {
            Jedis jedis=jedisPool.getResource();
            try {
                String key=PREFIX+productId;
                byte[] productBytes=jedis.get(key.getBytes());
                if (productBytes!=null){
                    Product product=schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(productBytes, product, schema);
                    return product;

                }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

    public void putProduct(Product product){
        try {
            Jedis jedis=jedisPool.getResource();
            try {
                String key=PREFIX+product.getId();
                byte[] productBytes=ProtostuffIOUtil.toByteArray(product, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                int timeout=60*60*24;
                jedis.setex(key.getBytes(), timeout, productBytes);
            } finally {
                jedis.close();
            }
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public void increaseProductViews(Integer productId){
        try {
            Jedis jedis=jedisPool.getResource();
            try {
                jedis.zincrby(VIEWS_LIST, 1, String.valueOf(productId));
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public int productRank(Integer productId){
        try {
            Jedis jedis=jedisPool.getResource();
            try {
                Long rank=jedis.zrevrank(VIEWS_LIST, String.valueOf(productId));
                return rank==null ? -1: rank.intValue()+1;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return -1;
    }

    public List<Map<String, Integer>> productRankList(){
        List<Map<String, Integer>> list=new ArrayList<>();
        try {
            Jedis jedis=jedisPool.getResource();
            try {
                Set<Tuple> tuples=jedis.zrevrangeWithScores(VIEWS_LIST, 0, 4);
                if (tuples!=null){
                    for (Tuple tuple:tuples){
                        Product product=getProduct(Integer.parseInt(tuple.getElement()));
                        Map<String, Integer> map=new HashMap<>();
                        map.put(product.getName(), (int)tuple.getScore());
                        list.add(map);
                    }
                }
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return list;
    }
}
