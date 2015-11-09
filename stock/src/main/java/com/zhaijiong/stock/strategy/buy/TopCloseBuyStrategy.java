package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.BuyStrategy;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-6.
 */
public class TopCloseBuyStrategy implements BuyStrategy {
    @Override
    public double buy(String symbol) {
        return 0;
    }

    @Override
    public double buy(List<StockData> stockDataList) {
        return 0;
    }

    @Override
    public boolean isBuy(String symbol) {
        return false;
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        return false;
    }
}
