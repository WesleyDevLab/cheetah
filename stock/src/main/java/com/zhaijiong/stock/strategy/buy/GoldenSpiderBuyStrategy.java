package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.StrategyUtils;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-22.
 */
public class GoldenSpiderBuyStrategy implements BuyStrategy {

    public GoldenSpiderBuyStrategy(){}

    @Override
    public double buy(String symbol) {
        List<StockData> stockDataList = Provider.dailyData(symbol,false);
        return buy(stockDataList);
    }

    @Override
    public double buy(List<StockData> stockDataList) {
        stockDataList = StrategyUtils.goldenSpider(stockDataList);
        if(stockDataList.size()>1){
            double status = stockDataList.get(stockDataList.size() - 1).get(StockConstants.GOLDEN_SPIDER);
            if(status==1){
                System.out.println("buy:"+stockDataList.get(stockDataList.size() - 1));
                return stockDataList.get(stockDataList.size() - 1).get(StockConstants.CLOSE);
            }
        }
        return -1;
    }

    @Override
    public boolean isBuy(String symbol) {
        List<StockData> stockDataList = Provider.dailyData(symbol,false);
        return isBuy(stockDataList);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        stockDataList = StrategyUtils.goldenSpider(stockDataList);
        if(stockDataList.size()>1){
            Double status = stockDataList.get(stockDataList.size() - 1).get(StockConstants.GOLDEN_SPIDER);
            if(status !=null &&status==1){
                return true;
            }
        }
        return false;
    }

}
