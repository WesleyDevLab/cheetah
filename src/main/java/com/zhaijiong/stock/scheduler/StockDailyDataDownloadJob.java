package com.zhaijiong.stock.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.common.Pair;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.datasource.DailyStockDataCollecter;
import com.zhaijiong.stock.datasource.StockListFetcher;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.List;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-6.
 */
public class StockDailyDataDownloadJob implements Job{

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        try {
            String starttime = jobContext.getJobDetail().getJobDataMap().getString("starttime");
            String stoptime = jobContext.getJobDetail().getJobDataMap().getString("stoptime");
            if(Strings.isNullOrEmpty(starttime)){
                starttime = Utils.getYesterday();
            }
            if(Strings.isNullOrEmpty(stoptime)){
                starttime = Utils.getYesterday();
                stoptime = Utils.getTomorrow();
            }
            Context context = new Context();
            StockListFetcher stockListFetcher = new StockListFetcher();
            List<Pair<String, String>> stockList = stockListFetcher.getStockList();
            for (Pair<String, String> stock : stockList) {
                DailyStockDataCollecter collecter = new DailyStockDataCollecter(starttime, stoptime);
                List<Stock> stocks = collecter.collect(stock.getVal());
                StockDB stockDB = new StockDB(context);
                stockDB.saveStockDailyData(stocks);
            }
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
