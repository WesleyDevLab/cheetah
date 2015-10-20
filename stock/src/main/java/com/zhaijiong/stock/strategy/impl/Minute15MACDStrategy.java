package com.zhaijiong.stock.strategy.impl;

import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.BaseStrategy;
import com.zhaijiong.stock.strategy.RiskStrategy;
import com.zhaijiong.stock.strategy.Strategy;
import com.zhaijiong.stock.strategy.risk.DefaultRiskStrategy;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-10-10.
 */
public class Minute15MACDStrategy extends BaseStrategy implements Strategy {

    private RiskStrategy riskStrategy = new DefaultRiskStrategy();

    public Minute15MACDStrategy(DataCenter dataCenter) {
        super(dataCenter);
    }

    @Override
    public Map<String, Double> buy(String stock) {
        return null;
    }

    @Override
    public boolean pick(String symbol,List<StockData> stockDataList) {
        List<StockData> macdStockDataList = Provider.computeMACD(stockDataList);
        for(int i=0;i<macdStockDataList.size();i++){

        }
        return false;
    }

    @Override
    public Map<String, Double> risk(String stock) {
        return riskStrategy.risk(stock);
    }

    @Override
    public Map<String, Double> sell(String stock) {
        return null;
    }
}
