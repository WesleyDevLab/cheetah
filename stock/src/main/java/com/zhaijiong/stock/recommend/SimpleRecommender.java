package com.zhaijiong.stock.recommend;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.buy.BuyStrategy;
import com.zhaijiong.stock.strategy.sell.SellStrategy;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-28.
 */
public class SimpleRecommender extends Recommender{

    public BuyStrategy buyStrategy;

    public SellStrategy sellStrategy;

    @Override
    public boolean isBuy(String symbol) {
        return buyStrategy.isBuy(symbol);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        return buyStrategy.isBuy(stockDataList);
    }

    @Override
    public boolean isSell(String symbol) {
        return sellStrategy.isSell(symbol);
    }

    @Override
    public boolean isSell(List<StockData> stockDataList) {
        return sellStrategy.isSell(stockDataList);
    }

    public BuyStrategy getBuyStrategy() {
        return buyStrategy;
    }

    public void setBuyStrategy(BuyStrategy buyStrategy) {
        this.buyStrategy = buyStrategy;
    }
}
