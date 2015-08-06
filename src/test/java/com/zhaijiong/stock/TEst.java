package com.zhaijiong.stock;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by eryk on 2015/7/5.
 */
public class TEst {
    @Test
    public void test(){
        System.out.println(new BigDecimal(12).add(new BigDecimal(1)).doubleValue());
        BigDecimal k = new BigDecimal(2).divide(new BigDecimal(12).add(new BigDecimal(1)),4, RoundingMode.DOWN);
        System.out.println(k.doubleValue());
    }
}
