package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.StockCategory;
import com.zhaijiong.stock.tools.StockPool;
import com.zhaijiong.stock.tools.ThreadPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"classpath:applicationContext.xml"}) //加载配置文件
public class GoldenSpiderBuyStrategyTest {

    @Autowired
    StockPool stockPool;
    @Autowired
    GoldenSpiderBuyStrategy buyStrategy;

    @Test
    public void testBuy() throws Exception {
        String symbol = "300233";
        if(buyStrategy.isBuy(symbol)){
            System.out.println(symbol);
        }
    }

    @Test
    public void testIsBuy() throws Exception {
        ThreadPool.init(16);
//        Map<String, Set<String>> stockCategory = StockCategory.getStockCategory("概念");
        List<String> stockList = stockPool.get("small_medium");
        if(stockList==null||stockList.size()==0){
            Conditions conditions = new Conditions();
            conditions.addCondition("close", Conditions.Operation.LT,30d);
            conditions.addCondition("PE",Conditions.Operation.LT,200d);
            conditions.addCondition("marketValue",Conditions.Operation.LT,200d);
            stockList = Provider.tradingStockList(conditions);
            stockPool.add("small_medium",stockList,86400);
        }

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