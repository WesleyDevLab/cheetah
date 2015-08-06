package com.zhaijiong.stock.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.Pair;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.Utils;
import com.zhaijiong.stock.dao.StockDAO;
import com.zhaijiong.stock.datasource.NetEaseDailyHistoryStockDataCollecter;
import com.zhaijiong.stock.datasource.StockListFether;
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
            String tablename = jobContext.getJobDetail().getJobDataMap().getString("tablename");
            String starttime = jobContext.getJobDetail().getJobDataMap().getString("starttime");
            String stoptime = jobContext.getJobDetail().getJobDataMap().getString("stoptime");
            Preconditions.checkNotNull(tablename,"tablename must be set");
            if(Strings.isNullOrEmpty(starttime)){
                starttime = Utils.getYesterday();
            }
            if(Strings.isNullOrEmpty(stoptime)){
                starttime = Utils.getYesterday();
                stoptime = Utils.getTomorrow();
            }

            Context context = new Context();
            StockListFether stockListFether = new StockListFether();
            List<Pair<String, String>> stockList = stockListFether.getStockList();
            for (Pair<String, String> stock : stockList) {
                NetEaseDailyHistoryStockDataCollecter collecter = new NetEaseDailyHistoryStockDataCollecter();
                List<Stock> stocks = collecter.collect(stock.getVal(), starttime, stoptime);
                StockDAO stockDAO = new StockDAO(tablename, context);
                stockDAO.save(stocks);
            }
            context.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
