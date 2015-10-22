package com.zhaijiong.stock.scheduler;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.RealTimeDataProvider;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zhaijiong.stock.common.Constants.TABLE_STOCK_DAILY;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-6.
 */
public class DailyDataDownloadJob extends JobBase {
    private static final Logger LOG = LoggerFactory.getLogger(DailyDataDownloadJob.class);

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        List<String> symbols = getSymbolList();
        for (String symbol : symbols) {
            StockData stockData = RealTimeDataProvider.get(symbol);
            stockDB.saveStockData(TABLE_STOCK_DAILY, Lists.newArrayList(stockData));
        }
        context.close();
    }
}
