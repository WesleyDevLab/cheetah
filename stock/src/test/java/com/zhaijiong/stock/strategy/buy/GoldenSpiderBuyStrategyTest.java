package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.ThreadPool;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class GoldenSpiderBuyStrategyTest {

    GoldenSpiderBuyStrategy buyStrategy = new GoldenSpiderBuyStrategy();

    @Test
    public void testBuy() throws Exception {

    }

    @Test
    public void testIsBuy() throws Exception {
        ThreadPool.init(16);
        List<String> stockList = Provider.tradingStockList();
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());
        for(String symbol:stockList){
            ThreadPool.execute(() -> {
                try{
                    if(buyStrategy.isBuy(symbol)){
                        System.out.println(symbol);
                    }
                }catch(Exception e){

                }finally{
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }
}