package com.zhaijiong.stock.strategy.sell;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zhaijiong.stock.common.StockConstants.*;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-22.
 */
public class MASellStrategy implements SellStrategy{
    private static final Logger LOG = LoggerFactory.getLogger(MASellStrategy.class);

    private static final String COLUMN_NAME_PROFIX = "close_ma";
    private int timeRange = 10; //计算的ma周期
    private String columnName = COLUMN_NAME_PROFIX + timeRange;
    private int count = 3;  //连续跌破ma周期线多少天以后卖出

    public MASellStrategy(int timeRange,int count){
        this.timeRange = timeRange;
        this.columnName = COLUMN_NAME_PROFIX + timeRange;
        this.count = count;
    }

    public MASellStrategy(){}

    @Override
    public double sell(String symbol) {
        List<StockData> stockDataList = Provider.dailyData(symbol,500,false);
        return sell(stockDataList);
    }

    @Override
    public double sell(List<StockData> stockDataList) {
        return stockDataList.get(stockDataList.size()-1).get(CLOSE);
    }

    @Override
    public boolean isSell(String symbol) {
        List<StockData> stockDataList = Provider.dailyData(symbol,500,false);
        return isSell(stockDataList);
    }

    @Override
    public boolean isSell(List<StockData> stockDataList) {
        int size = stockDataList.size();
        stockDataList = Provider.computeMA(stockDataList, CLOSE);
        int tmpCount = 0;
        if(size>3){
            for(int i =size-1;i>size-4;i--){
                double ma = stockDataList.get(i).get(columnName);
                if(stockDataList.get(i).get(CLOSE) < ma){
                    tmpCount++;
                }
            }
        }
        if(tmpCount>=3){
            System.out.println(stockDataList.get(size-1) + ":" + tmpCount);
            return true;
        }
        return false;
    }
}
