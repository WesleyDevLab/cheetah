package com.zhaijiong.stock.scheduler;

import com.google.common.base.Strings;
import com.zhaijiong.stock.common.Pair;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.datasource.CostStatisticsFetcher;
import com.zhaijiong.stock.datasource.StockListFetcher;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

import static com.zhaijiong.stock.common.Constants.IFENG_DATE_STYLE;
import static com.zhaijiong.stock.common.Constants.ROWKEY_DATA_FORMAT;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-11.
 */
public class StockAvgCostSaveJob extends JobBase {

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        String starttime = jobContext.getJobDetail().getJobDataMap().getString("starttime");
        String stoptime = jobContext.getJobDetail().getJobDataMap().getString("stoptime");
        if (Strings.isNullOrEmpty(starttime)) {
            starttime = Utils.getYesterday(IFENG_DATE_STYLE);
        }
        if (Strings.isNullOrEmpty(stoptime)) {
            starttime = Utils.getYesterday(IFENG_DATE_STYLE);
            stoptime = Utils.getTomorrow(IFENG_DATE_STYLE);
        }
        StockListFetcher fetcher = new StockListFetcher();

        List<Pair<String, String>> stockList = fetcher.getStockList();
        for (Pair<String, String> stock : stockList) {
            double avgCost = CostStatisticsFetcher.getAvgCost(stock.getVal(), starttime, stoptime);
            //TODO 任务如果是第二天凌晨跑，时间就会不准确
            stockDB.saveStockAvgCost(stock.getVal(), Utils.getNow(ROWKEY_DATA_FORMAT), avgCost);
        }

    }
}
