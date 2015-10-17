package com.zhaijiong.stock.strategy.impl;

import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.strategy.RiskStrategy;

import java.util.Map;

/**
 * 每次下注满仓买入卖出
 */
public class DefaultRiskStrategy implements RiskStrategy{
    @Override
    public Map<String, Double> risk(String stock) {
        Map<String,Double> risk = Maps.newHashMap();
        risk.put(StockConstants.POSITION,1d);
        return null;
    }
}
