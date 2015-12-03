package com.zhaijiong.stock.strategy.sell;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.StrategyBase;
import com.zhaijiong.stock.strategy.StrategyUtils;

import java.util.List;

import static com.zhaijiong.stock.common.StockConstants.CLOSE;
import static com.zhaijiong.stock.common.StockConstants.MACD_CROSS;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-9.
 */
public class MACDSellStrategy extends StrategyBase implements SellStrategy{

    private int timeRange = 5;
    private PeriodType type;
    private static final String NAME = "macdSell";

    public MACDSellStrategy(){
        this.name = NAME;
    }

    public MACDSellStrategy(int timeRange, PeriodType type){
        this.timeRange = timeRange;
        this.type = type;
        this.name = NAME;
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
            Double cross = stockData.get(MACD_CROSS);
            if (cross != null && count - i <= timeRange && cross == 0)
                return stockData.get(CLOSE);
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
                stockDataList = getDailyData(symbol);
                break;
            default:
                stockDataList = getDailyData(symbol);
        }
        return stockDataList;
    }

}
