package com.zhaijiong.stock.recommend;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.buy.GoldenSpiderBuyStrategy;
import com.zhaijiong.stock.strategy.sell.GoldenSpiderSellStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-28.
 */
@Component
public class GoldenSpiderRecommender extends Recommender{

    @Autowired
    public GoldenSpiderBuyStrategy goldenSpiderBuyStrategy;

    @Autowired
    public GoldenSpiderSellStrategy goldenSpiderSellStrategy;

    @Override
    public boolean isBuy(String symbol) {
        return goldenSpiderBuyStrategy.isBuy(symbol);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        return goldenSpiderBuyStrategy.isBuy(stockDataList);
    }

    @Override
    public boolean isSell(String symbol) {
        if(goldenSpiderSellStrategy.isSell(symbol)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isSell(List<StockData> stockDataList) {
        if(goldenSpiderSellStrategy.isSell(stockDataList)){
            return true;
        }
        return false;
    }
}
