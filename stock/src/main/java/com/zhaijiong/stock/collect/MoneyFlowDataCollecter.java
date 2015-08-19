package com.zhaijiong.stock.collect;

import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-18.
 */
public class MoneyFlowDataCollecter implements Collecter<String,Double>{
    //第二个为6位随机数
    private String moneyFlowURL = "http://hqchart.eastmoney.com/hq20/js/%s.js?%s";

    @Override
    public Map<String, Double> collect(String symbol) {
        return null;
    }

    @Override
    public String getPath(String symbol) {
        return null;
    }
}
