package com.xy.ecommerce.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatetimeUtil {

    private static Logger logger=LoggerFactory.getLogger(DatetimeUtil.class);

    private static final String pattern="yyyy-MM-dd HH:mm:ss";
    // Str -> Date
    public static Date strToDate(String str){
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        Date date=null;
        try {
            date=format.parse(str);
        } catch (ParseException e) {
            logger.error(e.toString());
        }

        return date;
    }

    // Date -> Str
    public static String dateToStr(Date date){
        if (date==null) return "";
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
