package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.model.StockData;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-29.
 */
public interface RiskStrategy {

    Map<String,Double> risk(String symbol);
}
