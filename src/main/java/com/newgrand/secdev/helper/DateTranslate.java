package com.newgrand.secdev.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author ChenXiangLu
 * @Date 2020/11/28 18:01
 * @Version 1.0
 */
public class DateTranslate {
    public static String getDateAsString(LocalDateTime localDateTime, String format)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

    /**
     * 判断时间格式是否正确
     * @param str
     * @return
     */
    public static boolean isValidDate(String str,String format) {
        DateFormat formatter = new SimpleDateFormat(format); //这里的时间格式根据自己需求更改（注意：格式区分大小写、格式区分大小写、格式区分大小写）
        try{
            Date date = (Date)formatter.parse(str);
            return str.equals(formatter.format(date));
        }catch(Exception e){
            return false;
        }
    }
}
