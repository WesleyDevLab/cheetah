package com.zhaijiong.datacenter;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Tick;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.Sleeper;
import com.zhaijiong.stock.tools.StockPool;
import com.zhaijiong.stock.tools.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.zhaijiong.stock.common.Constants.*;

/**
* author: eryk
* mail: xuqi86@gmail.com
* date: 15-11-30.
*/
@Component
public class StockDataDownload {
    protected static final Logger LOG = LoggerFactory.getLogger(StockDataDownload.class);

    public volatile static boolean rebuild = false;

    @Autowired
    @Qualifier("context")
    protected Context context;
    @Autowired
    protected StockDB stockDB;
    @Autowired
    @Qualifier("stockPool")
    protected StockPool stockPool;

    @Scheduled(cron = "0 0 8 * * MON-FRI") //0 0 8 * * MON-FRI
    public void downloadDailyData() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> stockList = stockPool.stockList();
        LOG.info("start to download daily data,stockList=" + stockList.size());
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());
        for (String symbol : stockList) {
            ThreadPool.execute(() -> {
                LOG.info(String.format("start download daily data with symbol [%s]", symbol));
                List<StockData> stockDataList = Lists.newLinkedList();
                int retry = 0;
                while(retry <3 && (stockDataList==null || stockDataList.size()==0)){
                    try{
                        stockDataList = Provider.dailyData(symbol, 240, true);
                        if (stockDataList != null && stockDataList.size() != 0) {
                            save(TABLE_STOCK_DAILY,stockDataList);
                        }
                    }catch(Exception e){
                        Sleeper.sleep(500);
//                        LOG.error(String.format("retry=%s,fail to download daily data with symbol [%s]",retry, symbol));
                    }finally {
                        retry++;
                    }
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await(3, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info(String.format("daily data download elapsed time=%ss", stopwatch.elapsed(TimeUnit.SECONDS)));
    }

    @Scheduled(fixedRate = 180000)
    public void downloadRealTimeData() {
        if(!rebuild){
            if (!Utils.isTradingTime()) {
                LOG.info("not working time.");
                return;
            }
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> stockList = stockPool.tradingStock();
        LOG.info("start to download realtime data,stockList=" + stockList.size());
        List<StockData> stockDataList = Collections.synchronizedList(Lists.newArrayListWithCapacity(stockList.size()));
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());
        for (String symbol : stockList) {
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        StockData stockData = Provider.realtimeData(symbol);
                        int retry = 0;
                        while(retry <3 && stockData==null){
                            try{
                                stockData = Provider.realtimeData(symbol);
                            }catch(Exception e){
                                Sleeper.sleep(500);
//                                LOG.error(String.format("retry=%s,fail to download realtime data with symbol [%s]",retry, symbol));
                            }finally {
                                retry++;
                            }

                        }
                        if (stockData != null && !Strings.isNullOrEmpty(stockData.symbol)) {
                            stockData.date = Utils.str2Date(Utils.formatDate(stockData.date, "yyyyMMdd"), "yyyyMMdd");
                            stockDataList.add(stockData);
                        } else {
                            LOG.error(String.format("fail to download realtime data with symbol [%s]", symbol));
                        }
                    } catch (Exception e) {
                        LOG.error(String.format("fail to get symbol [%s] data",symbol),e);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        save(TABLE_STOCK_DAILY,stockDataList);
        LOG.info(String.format("realtime data download elapsed time=%ss,save count=%s", stopwatch.elapsed(TimeUnit.SECONDS), stockDataList.size()));
    }

//    @Scheduled(fixedRate = 180000)
    public void downloadTickData() {
        if(!rebuild){
            if (!Utils.isTradingTime()) {
                LOG.info("not working time.");
                return;
            }
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> stockList = stockPool.tradingStock();
        LOG.info("start to download tick data,stockList=" + stockList.size());
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());
        for (String symbol : stockList) {
            ThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    List<Tick> tickData = Provider.tickData(symbol);
                    int retry = 0;
                    while(retry <3 && tickData==null){
                        try{
                            tickData = Provider.tickData(symbol);
                        }catch(Exception e){
                            Sleeper.sleep(500);
                        }finally {
                            retry++;
                        }
                    }
                    if (tickData != null ) {
                        stockDB.saveTicksData(symbol,tickData);
                    } else {
                        LOG.error(String.format("fail to download realtime data with symbol [%s]", symbol));
                    }
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LOG.info(String.format("tick data download elapsed time=%ss,save count=%s", stopwatch.elapsed(TimeUnit.SECONDS), stockList.size()));

    }

    public void save(String tableName,List<StockData> stockDataList) {
        stockDB.saveStockData(tableName, stockDataList);
    }

}
