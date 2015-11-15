package com.zhaijiong.stock;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Tick;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.provider.RealTimeDataProvider;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-27.
 */
public class XuleiTest {
    private static Logger LOG = LoggerFactory.getLogger(XuleiTest.class);

    Map<String,List<StockData>> dailyDataMap;
    DateRange range;

    @Before
    public void setup(){
        dailyDataMap = Maps.newHashMap();
        range = DateRange.getRange(15);
    }

    @Test
    public void test(){
        Stopwatch stopwatch = Stopwatch.createStarted();
        ExecutorService service = Executors.newFixedThreadPool(10);

        //获取最近10天之内有过涨停板
        //涨停板当天出现过一笔买盘成交手数是当天平均手数的30倍以上
        //并且随后7天内股价跌破涨停板当天开盘价
//        List<String> stocks = Lists.newArrayList(Collections2.filter(Lists.newArrayList("600868"), stock -> {
        List<String> tradingStockList = Provider.tradingStockList();
        CountDownLatch countDownLatch = new CountDownLatch(tradingStockList.size());

        List<String> stocks = Lists.newLinkedList();
        for(String stock:tradingStockList){
            service.execute(() -> {
                if(check(stock)){
                    stocks.add(stock);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Utils.closeThreadPool(service);

        stocks.forEach(stock -> System.out.println(stock));
        System.out.println("cost:"+stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    private boolean check(String stock) {
        List<StockData> stockDataList = Provider.dailyData(stock, range.start(), range.stop());
        dailyDataMap.put(stock,stockDataList);
        double latestOpen = Double.MAX_VALUE;
        int count = 0;
        for (StockData stockData : stockDataList) {
            if (stockData.date.getTime() > range.startDate().getTime()) {
                if(count>0 && count <= 7){
                    count++;
                }else{
                    count = 0;
                }
                if (stockData.get("change") > 9.9 && stockData.get("close").doubleValue() == stockData.get("high").doubleValue()) {
                    List<Tick> ticks = Provider.tickData(stock, Utils.formatDate(stockData.date, "yyyyMMdd"));
                    //其中一笔买盘成交手数是当天平均手数的10倍以上
                    double avgVolume = 0;
                    for (Tick tick : ticks) {
                        avgVolume += tick.volume;
                    }
                    avgVolume = avgVolume / ticks.size();

                    int tickCount = 0;
                    for (int i = 1; i < ticks.size(); i++) {
                        Tick tick = ticks.get(i);

                        if (tick.type == Tick.Type.BUY && tick.volume > avgVolume * 50 && !tick.date.contains("15:00:") && !tick.date.contains("09:30:")) {
//                                System.out.println(ticks.get(i));
                            tickCount++;
                        }
                    }
                    if (tickCount > 1) {
                        System.out.println(stockData.symbol + "\t" +Utils.formatDate(stockData.date,"yyyyMMdd") + "平均每笔成交量:" + Utils.formatDouble(avgVolume));
                        //如果出现大买单，计算随后七天内是否被跌破过
                        latestOpen = stockData.get("open");
                        count = 1;
                    }
                }else if(latestOpen < Double.MAX_VALUE && latestOpen > stockData.get("close").doubleValue() && count > 0
                        && stockDataList.get(stockDataList.size()-1).get("close") < latestOpen){
                    System.out.println(stockData.symbol + "\t" + Utils.formatDate(stockData.date,"yyyyMMdd") + "\t" +latestOpen + ">" + stockDataList.get(stockDataList.size()-1).get("close"));
                    return true;
                }
            }
        }
        return false;
    }
}
