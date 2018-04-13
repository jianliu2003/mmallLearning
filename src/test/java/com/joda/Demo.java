package com.joda;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by jianl on 2018/3/29.
 */
public class Demo {
    public static void main(String[] args) {
        Date date = strToDate("2022-05-21 13:12:12", "yyyy-MM-dd HH:mm:ss");
        System.out.println(date);
        System.out.println("------------------------------------------------");
        String dateStr = dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(dateStr);

    }

    public static Date strToDate(String dateStr,String format){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateStr);
        Date date = dateTime.toDate();
        return  date;
    }

    public static String dateToStr(Date date,String format){
        DateTime dateTime = new DateTime(date);
        String dateStr = dateTime.toString(format);
        return dateStr;
    }


}
