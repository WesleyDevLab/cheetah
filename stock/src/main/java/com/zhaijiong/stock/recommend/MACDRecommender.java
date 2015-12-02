package com.zhaijiong.stock.recommend;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import com.zhaijiong.stock.strategy.sell.MACDSellStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-28.
 */
@Component
public class MACDRecommender extends Recommender{

    @Autowired
    private MACDBuyStrategy dayMACDBuyStrategy;

    @Autowired
    private MACDBuyStrategy minute15BuyStrategy;

    @Autowired
    private MACDSellStrategy minute15SellStratgy;

    @Override
    public boolean isBuy(String symbol) {
        if(dayMACDBuyStrategy.isBuy(symbol) && minute15BuyStrategy.isBuy(symbol)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        if(dayMACDBuyStrategy.isBuy(stockDataList) && minute15BuyStrategy.isBuy(stockDataList)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isSell(String symbol) {
        if(minute15SellStratgy.isSell(symbol)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isSell(List<StockData> stockDataList) {
        if(minute15SellStratgy.isSell(stockDataList)){
            return true;
        }
        return false;
    }

    public MACDBuyStrategy getDayMACDBuyStrategy() {
        return dayMACDBuyStrategy;
    }

    public void setDayMACDBuyStrategy(MACDBuyStrategy dayMACDBuyStrategy) {
        this.dayMACDBuyStrategy = dayMACDBuyStrategy;
    }

    public MACDBuyStrategy getMinute15BuyStrategy() {
        return minute15BuyStrategy;
    }

    public void setMinute15BuyStrategy(MACDBuyStrategy minute15BuyStrategy) {
        this.minute15BuyStrategy = minute15BuyStrategy;
    }
}
