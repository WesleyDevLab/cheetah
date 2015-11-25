package com.zhaijiong.stock;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.buy.BuyStrategy;
import com.zhaijiong.stock.strategy.buy.GoldenSpiderBuyStrategy;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import com.zhaijiong.stock.strategy.sell.SellStrategy;
import com.zhaijiong.stock.tools.Sleeper;
import com.zhaijiong.stock.tools.StockCategory;
import com.zhaijiong.stock.tools.StockPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected static final Logger LOG = LoggerFactory.getLogger(Recommender.class);

    protected Set<String> holdingStocks = Sets.newConcurrentHashSet();

    protected static Map<String, Set<String>> stockCategory = StockCategory.getStockCategory("概念");

    /**
     * 获取股票所属概念版块名称列表
     * @param symbol    6位股票代码
     * @return
     */
    public static String getStockCategory(String symbol){
        if(stockCategory.get(symbol)!=null){
            return Joiner.on(",").join(stockCategory.get(symbol));
        }
        return "";
    }

    public void process(List<String> symbols){

    }

    public void recommender(String symbol){

    }

    public static void main(String[] args) throws InterruptedException {
        MACDBuyStrategy dayMacdStrategy = new MACDBuyStrategy(1, PeriodType.DAY);
        MACDBuyStrategy minute15MacdStrategy = new MACDBuyStrategy(3,PeriodType.FIFTEEN_MIN);
        MACDBuyStrategy minute5MacdStrategy = new MACDBuyStrategy(3,PeriodType.FIVE_MIN);
        GoldenSpiderBuyStrategy goldenSpiderBuyStrategy = new GoldenSpiderBuyStrategy();

        Conditions conditions = new Conditions();
        conditions.addCondition("close", Conditions.Operation.LT,20d);
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

                                System.out.println(record + "\t" + getStockCategory(symbol));
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
    }
}
