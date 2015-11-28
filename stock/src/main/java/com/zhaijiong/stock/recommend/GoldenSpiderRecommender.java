package com.zhaijiong.stock.recommend;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.buy.GoldenSpiderBuyStrategy;
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

    @Override
    public boolean isBuy(String symbol) {
        return goldenSpiderBuyStrategy.isBuy(symbol);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        return goldenSpiderBuyStrategy.isBuy(stockDataList);
    }
}
