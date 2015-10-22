package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.model.StockData;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-28.
 */
public interface BuyStrategy {
    /**
     * 买入价格
     * @param stockDataList
     * @return
     */
    Map<String,Double> buy(String symbol, List<StockData> stockDataList);
}
