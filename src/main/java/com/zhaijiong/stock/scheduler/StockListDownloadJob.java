package com.zhaijiong.stock.scheduler;

import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.common.Pair;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.datasource.StockListFetcher;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-8.
 */
public class StockListDownloadJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(StockListDownloadJob.class);

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        StockListFetcher fetcher = new StockListFetcher();
        try {
            Context context = new Context();
            StockDB stockDB = new StockDB(context);
            List<Pair<String, String>> stockList = fetcher.getStockList();
            stockDB.saveStockList(stockList);
            context.close();
        } catch (IOException e) {
            LOG.error("failed to download stock list");
        }
    }
}
