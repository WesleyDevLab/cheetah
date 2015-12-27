package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.StrategyBase;
import com.zhaijiong.stock.strategy.StrategyUtils;

import java.util.List;

import static com.zhaijiong.stock.common.StockConstants.CLOSE;
import static com.zhaijiong.stock.common.StockConstants.GOLDEN_SPIDER;
import static com.zhaijiong.stock.common.StockConstants.GOLDEN_SPIDER_RANGE;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-22.
 */
public class GoldenSpiderBuyStrategy extends StrategyBase implements BuyStrategy {

    private int crossCount = 3;
    private static final String NAME = "goldenSpiderBuy";

    public GoldenSpiderBuyStrategy(){
        this.name = NAME;
    }

    public GoldenSpiderBuyStrategy(int crossCount){
        this.crossCount = crossCount;
        this.name = NAME;
    }

    @Override
    public double buy(String symbol) {
        List<StockData> stockDataList = getDailyData(symbol);
        return buy(stockDataList);
    }

    @Override
    public double buy(List<StockData> stockDataList) {
        stockDataList = StrategyUtils.goldenSpider(stockDataList);
        if(stockDataList.size()>1){
            double status = stockDataList.get(stockDataList.size() - 1).get(GOLDEN_SPIDER);
            if(status>=crossCount  || stockDataList.get(stockDataList.size() - 1).get(GOLDEN_SPIDER_RANGE) > 0){
                System.out.println("buy:"+stockDataList.get(stockDataList.size() - 1));
                return stockDataList.get(stockDataList.size() - 1).get(CLOSE);
            }
        }
        return -1;
    }

    @Override
    public boolean isBuy(String symbol) {
        if(blackList.contains(symbol)){
            return false;
        }
        List<StockData> stockDataList = getDailyData(symbol);
        return isBuy(stockDataList);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        int size = stockDataList.size();
        if(size==0 || size < 60){
            return false;
        }
        stockDataList = StrategyUtils.goldenSpider(stockDataList);
        if(size>2){
            double status = stockDataList.get(size - 1).get(GOLDEN_SPIDER);
            if(status>=crossCount || stockDataList.get(size - 1).get(GOLDEN_SPIDER_RANGE) > 0){
                return true;
            }
        }
        return false;
    }

}
