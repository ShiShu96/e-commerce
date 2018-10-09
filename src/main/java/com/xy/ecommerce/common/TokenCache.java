package com.xy.ecommerce.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {

    public static final String TOKEN_PREFIX="token_";
    private static Logger logger=LoggerFactory.getLogger(TokenCache.class);
    private static LoadingCache<String, String> cache=CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(2,TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return null;
                }
            });

    public static void setKey(String key, String value){
        cache.put(key, value);
    }

    public static String getKey(String key){
        String value=null;
        try {
            value = cache.get(key);
        } catch (Exception e){
            logger.error("cache error");
        }
        return value;
    }
}
