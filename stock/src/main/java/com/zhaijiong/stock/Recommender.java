package com.zhaijiong.stock;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import com.zhaijiong.stock.tools.Sleeper;
import com.zhaijiong.stock.tools.StockCategory;
import com.zhaijiong.stock.tools.StockPool;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 股票推荐，针对多个股票池的个性化策略周期调度系统
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-19.
 */
public class Recommender {
    public static void main(String[] args) throws InterruptedException {
//        Map<String, List<String>> stockCategory = StockCategory.getStockCategory();
        Map<String, Set<String>> stockCategory = StockCategory.getStockCategory("概念");

        MACDBuyStrategy dayMacdStrategy = new MACDBuyStrategy(1, PeriodType.DAY);
        MACDBuyStrategy minute15MacdStrategy = new MACDBuyStrategy(3,PeriodType.FIFTEEN_MIN);
        MACDBuyStrategy minute5MacdStrategy = new MACDBuyStrategy(3,PeriodType.FIVE_MIN);

        Conditions conditions = new Conditions();
        conditions.addCondition("close", Conditions.Operation.LT,30d);
        conditions.addCondition("PE",Conditions.Operation.LT,200d);
        conditions.addCondition("marketValue",Conditions.Operation.LT,100d);
        List<String> stockList = StockPool.listByConditions(conditions);
        System.out.println("stockList="+stockList.size());
        ExecutorService pool = Executors.newFixedThreadPool(32);
        while(true){
            if(Utils.isTradingTime()){
                Stopwatch stopwatch = Stopwatch.createStarted();
                System.out.println(Utils.formatDate(new Date(),"yyyyMMdd HH:mm:ss"));
                CountDownLatch countDownLatch = new CountDownLatch(stockList.size());

                for (String symbol : stockList) {
                    pool.execute(() -> {
                        if (dayMacdStrategy.isBuy(symbol)) {
                            if (minute15MacdStrategy.isBuy(symbol)) {
//                                if(minute5MacdStrategy.isBuy(symbol)){
                                StockData stockData = Provider.realtimeData(symbol);
                            String record = Joiner.on("\t").join(
                                    stockData.name,stockData.symbol,
                                    stockData.get("close"),
                                    stockData.get("PE"));
                                String category = "";
                                if(stockCategory.get(symbol)!=null){
                                    category = Joiner.on(",").join(stockCategory.get(symbol));
                                }

                                System.out.println(record + "\t" + category);
//                                }
                            }
                        }
                        countDownLatch.countDown();
                    });
                }
                countDownLatch.await();
                System.out.println("cost:"+stopwatch.elapsed(TimeUnit.SECONDS)+"s");
            }
            Sleeper.sleep(120 * 1000);
        }

//        Utils.closeThreadPool(pool);

    }
}
