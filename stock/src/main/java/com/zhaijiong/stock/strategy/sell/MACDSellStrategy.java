package com.zhaijiong.stock.strategy.sell;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.StrategyUtils;
import com.zhaijiong.stock.tools.StockList;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-9.
 */
public class MACDSellStrategy implements SellStrategy{

    private int timeRange = 5;
    private PeriodType type;

    public MACDSellStrategy(int timeRange, PeriodType type){
        this.timeRange = timeRange;
        this.type = type;
    }

    @Override
    public double sell(String symbol) {
        List<StockData> stockDataList = getStockDataByType(symbol);
        return sell(stockDataList);
    }

    @Override
    public double sell(List<StockData> stockDataList) {
        int count = stockDataList.size();
        stockDataList = Provider.computeMACD(stockDataList);
        for (int i = count - 1; i > 0; i--) {
            StockData stockData = stockDataList.get(i);
            Double cross = stockData.get(StockConstants.MACD_CROSS);
            if (cross != null && count - i <= timeRange && cross == 0)
                return stockData.get("close");
        }
        return -1;
    }

    @Override
    public boolean isSell(String symbol) {
        List<StockData> stockDataList = getStockDataByType(symbol);
        return isSell(stockDataList);
    }

    @Override
    public boolean isSell(List<StockData> stockDataList) {
        stockDataList = Provider.computeMACD(stockDataList);
        if (StrategyUtils.isMACDDiedCrossIn(stockDataList, timeRange)) {
            return true;
        }
        return false;
    }

    private List<StockData> getStockDataByType(String symbol) {
        List<StockData> stockDataList;
        switch (type){
            case FIVE_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "5"));
                break;
            case FIFTEEN_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "15"));
                break;
            case THIRTY_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "30"));
                break;
            case SIXTY_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "60"));
                break;
            case DAY:
                stockDataList = Lists.newArrayList(Provider.dailyData(symbol,false));
                break;
            default:
                stockDataList = Lists.newArrayList(Provider.dailyData(symbol,false));
        }
        return stockDataList;
    }

    public static void main(String[] args) {
        MACDSellStrategy sellStrategy = new MACDSellStrategy(3,PeriodType.DAY);
        List<String> symbols = StockList.getMarginTradingStockList();
        for(String symbol:symbols){
            if(sellStrategy.isSell(symbol)){
                System.out.println("for:"+symbol);
                System.out.println(sellStrategy.sell(symbol));
            }
        }
    }
}
