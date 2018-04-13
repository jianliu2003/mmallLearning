package com.bigdecimal;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by jianl on 2018/4/13.
 */
public class BigDecimalDemo {
    public static void main(String[] args) {
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.01);
        BigDecimal result = b1.add(b2);
        System.out.println("b1:"+b1);
        System.out.println("b2:"+b2);
        System.out.println("b1+b2:"+result);
    }

    @Test
    public void run(){
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        BigDecimal result = b1.add(b2);
        System.out.println("b1:"+b1);
        System.out.println("b2:"+b2);
        System.out.println("b1+b2:"+result);
    }


    @Test
    public void run2(){
        BigDecimal result = BigDecimalUtil.add(new BigDecimal("0.02").doubleValue(), 2);
        BigDecimal result2 = BigDecimalUtil.mul(new BigDecimal("0.03").doubleValue(), new Integer(2).doubleValue());
        System.out.println(result);
        System.out.println(result2);
    }

}
