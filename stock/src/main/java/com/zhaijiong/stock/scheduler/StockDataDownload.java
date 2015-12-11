package com.zhaijiong.stock.scheduler;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.StockPool;
import com.zhaijiong.stock.tools.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
* author: eryk
* mail: xuqi86@gmail.com
* date: 15-11-30.
*/
@Component
public class StockDataDownload {
    protected static final Logger LOG = LoggerFactory.getLogger(StockDataDownload.class);

    @Autowired
    @Qualifier("context")
    protected Context context;
    @Autowired
    protected StockDB stockDB;
    @Autowired
    @Qualifier("stockPool")
    protected StockPool stockPool;

    @Scheduled(cron = "0 0 10 * * MON-FRI")
    public void downloadDailyData() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> stockList = stockPool.stockList();
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());
        for (String symbol : stockList) {
            ThreadPool.execute(() -> {
                LOG.info(String.format("start download daily data with symbol [%s]", symbol));
                //TODO retry when fail
                List<StockData> stockDataList = Provider.dailyData(symbol, 240, true);
                if (stockDataList != null && stockDataList.size() != 0) {
                    save(stockDataList);
                } else {
                    LOG.error(String.format("fail to download daily data with symbol [%s]", symbol));
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await(3, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info(String.format("download elapsed time=%ss", stopwatch.elapsed(TimeUnit.SECONDS)));
    }

    @Scheduled(fixedRate = 120000)
    public void downloadRealTimeData() {
        if (!Utils.isTradingTime()) {
            return;
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
        save(stockDataList);
        LOG.info(String.format("download elapsed time=%ss,save count=%s", stopwatch.elapsed(TimeUnit.SECONDS), stockDataList.size()));
    }

    public void save(List<StockData> stockDataList) {
        stockDB.saveStockData(Constants.TABLE_STOCK_DAILY, stockDataList);
    }

}
