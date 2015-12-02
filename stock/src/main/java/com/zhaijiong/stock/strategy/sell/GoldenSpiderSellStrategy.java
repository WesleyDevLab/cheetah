package com.zhaijiong.stock.strategy.sell;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.StrategyUtils;

import java.util.List;

import static com.zhaijiong.stock.common.StockConstants.*;

/**
 * author: xuqi86
 * mail: xuqi86@gmail.com
 * date: 15-11-23.
 */
public class GoldenSpiderSellStrategy implements SellStrategy{

    private int crossCount = 3;
    private SellStrategy sellStrategy = new MASellStrategy(10,3);

    public GoldenSpiderSellStrategy(){}

    public GoldenSpiderSellStrategy(int crossCount){
        this.crossCount = crossCount;
    }

    @Override
    public double sell(String symbol) {
        List<StockData> stockDataList = Provider.dailyData(symbol, true);
        return sell(stockDataList);
    }

    @Override
    public double sell(List<StockData> stockDataList) {
        return sellStrategy.sell(stockDataList);
    }

    @Override
    public boolean isSell(String symbol) {
        List<StockData> stockDataList = Provider.dailyData(symbol, true);
        return isSell(stockDataList);
    }

    @Override
    public boolean isSell(List<StockData> stockDataList) {
        int size = stockDataList.size();
        stockDataList = StrategyUtils.goldenSpider(stockDataList);
        for(int i=size-1;i>0;i--){
            if(stockDataList.get(i).get(GOLDEN_SPIDER)>=crossCount){
                if(i < size-crossCount && stockDataList.get(i).get("close")>stockDataList.get(size-1).get("close")){
                    return true;
                }
                break;
            }
        }
        return sellStrategy.isSell(stockDataList);
    }
}
