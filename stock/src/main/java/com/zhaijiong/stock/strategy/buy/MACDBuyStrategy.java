package com.zhaijiong.stock.strategy.buy;

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
 * date: 15-10-18.
 *
 * 策略判断步骤说明:
 * 1.判断日线级别macd最近n天是否处于金叉状态,并且红柱持续放大
 * 2.判断最近n根15分钟数据是否处于金叉状态
 */
public class MACDBuyStrategy extends StrategyBase implements BuyStrategy {

    private int timeRange = 5;
    private PeriodType type;
    private static final String NAME = "macdBuy";

    public MACDBuyStrategy(){
        this.name = NAME;
    }

    public MACDBuyStrategy(int timeRange, PeriodType type){
        this.timeRange = timeRange;
        this.type = type;
        this.name = NAME;
    }

    @Override
    public double buy(String symbol){
        List<StockData> stockDataList = getStockDataByType(type,symbol);
        return buy(stockDataList);
    }

    @Override
    public double buy(List<StockData> stockDataList){
        int count = stockDataList.size();
        stockDataList = Provider.computeMACD(stockDataList);
        for (int i = count - 1; i > 0; i--) {
            StockData stockData = stockDataList.get(i);
            double cross = stockData.get(MACD_CROSS);
            if (count - i <= timeRange && cross == 1)
                return stockData.get(CLOSE);
        }
        return -1;
    }


    @Override
    public boolean isBuy(String symbol) {
        List<StockData> stockDataList = getStockDataByType(type,symbol);
        return isBuy(stockDataList);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList){
        stockDataList = Provider.computeMACD(stockDataList);
        if (StrategyUtils.isMACDGoldenCrossIn(stockDataList, timeRange)) {
            return true;
        }
        return false;
    }

}
