package com.xy.ecommerce.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
    private static Logger logger=LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;

    static {
        String filename="ecommerce.properties";

        properties=new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(filename),"UTF-8"));

        } catch (Exception e){
            logger.error(e.toString());
        }
    }

    public static String getProperty(String key){
        return getProperty(key, null);
    }

    public static String getProperty(String key,String defaultValue){

        String value = properties.getProperty(key.trim()).trim();
        if(StringUtils.isEmpty(value)){
            value = defaultValue;
        }
        return value.trim();
    }

}
