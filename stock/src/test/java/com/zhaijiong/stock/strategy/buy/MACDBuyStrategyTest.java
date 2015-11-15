package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.provider.Provider;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MACDBuyStrategyTest {

    @Test
    public void testPick() throws Exception {
        MACDBuyStrategy dayMacdStrategy = new MACDBuyStrategy(5, PeriodType.DAY);
        MACDBuyStrategy minute15MacdStrategy = new MACDBuyStrategy(3,PeriodType.FIFTEEN_MIN);
        List<String> stockList = Provider.tradingStockList();
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());

        for (String symbol : stockList) {
            pool.execute(() -> {
                if (dayMacdStrategy.isBuy(symbol)) {
                    if (minute15MacdStrategy.isBuy(symbol)) {
                        System.out.println(symbol);
                    }
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        Utils.closeThreadPool(pool);
    }
}