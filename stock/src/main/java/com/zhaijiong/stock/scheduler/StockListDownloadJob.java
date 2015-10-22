package com.zhaijiong.stock.scheduler;

import com.zhaijiong.stock.tools.StockList;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-8.
 */
public class StockListDownloadJob extends JobBase {
    private static final Logger LOG = LoggerFactory.getLogger(StockListDownloadJob.class);

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        try {
            Map<String, String> stockMap = StockList.getMap();
            stockDB.saveStockSymbols(stockMap);
        } catch (IOException e) {
            LOG.error("failed to download stock list");
        }finally {
            context.close();
        }
    }
}
