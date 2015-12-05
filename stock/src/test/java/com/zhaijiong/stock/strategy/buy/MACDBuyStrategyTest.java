package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.StockPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"classpath:applicationContext.xml"}) //加载配置文件
public class MACDBuyStrategyTest {

    @Autowired
    StockPool stockPool;

    @Autowired
    private MACDBuyStrategy minute60BuyStrategy;


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

    @Test
    public void testIsBuy(){
        String symbol = "002096";
        if(minute60BuyStrategy.isBuy(symbol)){
            System.out.println(symbol);
        }
    }

    @Test
    public void testIsBuy1(){
        List<String> stockList = stockPool.tradingStock();
        for(String symbol:stockList){
            if(minute60BuyStrategy.isBuy(symbol)){
                System.out.println(symbol);
            }
        }
    }
}