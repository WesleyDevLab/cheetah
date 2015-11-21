package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class StrategyUtilsTest {

    @Test
    public void testAverageBond() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015,10,13,0,0);
        long time = calendar.getTime().getTime();
        ExecutorService executorService = Executors.newFixedThreadPool(16);

        List<String> stockList = Provider.tradingStockList();
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());
        for(String stock :stockList){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    List<StockData> stockDataList = Provider.dailyData(stock,500,false);
                    if(stockDataList.size()>120){
                        stockDataList = Provider.computeMA(stockDataList,"close");
                        stockDataList = StrategyUtils.averageBond(stockDataList,0.005);
                        for(StockData stockData:stockDataList){
                            if(stockData.date.getTime()>time &&
                                    stockData.get(StockConstants.AVERAGE_BOND)>3 &&
                                    stockData.get(StockConstants.CLOSE)>stockData.get(StockConstants.CLOSE_MA10)){
                                System.out.println(stockData);
                                break;
                            }
                        }
                    }
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }
}