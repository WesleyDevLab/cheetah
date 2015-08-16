package com.zhaijiong.stock.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.convert.MinuteDataConverter;
import com.zhaijiong.stock.tools.StockMap;
import org.apache.hadoop.hbase.client.Put;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

import static com.zhaijiong.stock.common.Constants.*;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-12.
 */
public class StockMinuteDataDownloadJob extends JobBase {

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        String type = jobContext.getJobDetail().getJobDataMap().getString("type");
        Preconditions.checkNotNull(type);

        String starttime = jobContext.getJobDetail().getJobDataMap().getString("starttime");
        String stoptime = jobContext.getJobDetail().getJobDataMap().getString("stoptime");
        if (Strings.isNullOrEmpty(starttime)) {
            starttime = Utils.getNow(BISNESS_DATA_FORMAT);
        }
        if (Strings.isNullOrEmpty(stoptime)) {
            starttime = Utils.getYesterday(BISNESS_DATA_FORMAT);
            stoptime = Utils.getTomorrow(BISNESS_DATA_FORMAT);
        }

        List<String> stockList = StockMap.getList();
        for (String symbol : stockList) {
            MinuteDataCollecter collecter = new MinuteDataCollecter(starttime, stoptime, type);
            Map<String, Map<String, String>> data = collecter.collect(symbol);
            MinuteDataConverter converter = new MinuteDataConverter();
            List<Put> stocks = converter.toPut(data);
            if ("5".equals(type)) {
                stockDB.save(TABLE_STOCK_5_MINUTES,stocks);
            } else if ("15".equals(type)) {
                stockDB.save(TABLE_STOCK_15_MINUTES,stocks);
            } else if ("30".equals(type)) {
                stockDB.save(TABLE_STOCK_30_MINUTES,stocks);
            } else if ("60".equals(type)) {
                stockDB.save(TABLE_STOCK_60_MINUTES,stocks);
            }
        }
        context.close();
    }
}
