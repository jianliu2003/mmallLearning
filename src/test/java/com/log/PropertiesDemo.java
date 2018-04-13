package com.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by jianl on 2018/3/30.
 */
public class PropertiesDemo {
    private static Logger logger = LoggerFactory.getLogger(PropertiesDemo.class);
    private static Properties props = new Properties();

    static {
        readConfig();
    }

    private static void readConfig() {
        InputStream inputStream = PropertiesDemo.class.getClassLoader().getResourceAsStream("mmall.properties");
        try {
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            props.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getProperty(String key) {
        String property = props.getProperty(key.trim());
        return property.trim();
    }

    public static void main(String[] args) {
        logger.error("error...");
        logger.warn("warn...");
        logger.info("info...");
        logger.debug("debug");
        logger.trace("trace...");
        System.out.println("-------------------------------");
        String property = getProperty("ftp.server.ip");
        System.out.println(property);
    }


    @Test
    public void run() {
        String str = "woshiLiuJian";
//        String lowerStr = str.toLowerCase();
//        String upperStr = str.toUpperCase();
//        String concatStr = str.concat("haha");
//        System.out.println("str:" + str);
//        System.out.println("lowerStr:" + lowerStr);
//        System.out.println("upperStr:" + upperStr);
//        System.out.println(concatStr);
//        //第一种遍历
//        for (int i = 0; i < str.length(); i++) {
//            char ch = str.charAt(i);
//            System.out.print(ch);
//        }
//        System.out.println();
//
//        //第二种遍历
//        char[] chs = str.toCharArray();
//        for(char ch : chs){
//            System.out.print(ch);
//        }
//        System.out.println();
//
//        //第二种遍历改版
//        for (int i=0;i<chs.length;i++){
//            System.out.print(chs[i]);
//        }
//        System.out.println();

        List<String> list = new ArrayList<String>();
        list.add("hello");
        list.add("world");
        list.add("java");
        System.out.println(list.size());
    }

}
