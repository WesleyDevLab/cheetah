package com.zhaijiong.stock.tools;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.tools.SharpeRatio;
import org.junit.Test;

import java.util.List;

public class SharpeRatioTest {

    @Test
    public void testValue() throws Exception {
        List<Double> inputs = Lists.newArrayList(4.41,5.34,-5.68,5.92,7.32,-9.32,1.65,-1.39,9.65,1.07,
                1.82,1.82,-2.82,4.91,-3.19,4.96,3.4,2.92,-4.77,4.36);
        System.out.println(SharpeRatio.value(inputs));
        System.out.println(SharpeRatio.value(inputs,3.0));
    }
}