package com.zhaijiong.stock.strategy.impl;

import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.BaseStrategy;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-10-10.
 */
public class Minute15MACDStrategy extends BaseStrategy{

    public Minute15MACDStrategy(DataCenter dataCenter) {
        super(dataCenter);
    }

    @Override
    public Map<String, Double> buy(String stock) {
        return null;
    }

    @Override
    public boolean pick(String stock) {
        List<StockData> stockDataList = dataCenter.getDailyData(stock);
        return false;
    }

    @Override
    public Map<String, Double> risk(String stock) {
        return null;
    }

    @Override
    public Map<String, Double> sell(String stock) {
        return null;
    }
}
