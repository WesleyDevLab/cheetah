package com.zhaijiong.stock.scheduler;

import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.collect.RealtimeDataCollecter;
import com.zhaijiong.stock.convert.RealTimeDataConverter;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.tools.StockMap;
import org.apache.hadoop.hbase.client.Put;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.zhaijiong.stock.common.Constants.*;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-6.
 */
public class DailyDataDownloadJob implements Job{
    private static final Logger LOG = LoggerFactory.getLogger(DailyDataDownloadJob.class);

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
            Context context = new Context();
            StockDB stockDB = new StockDB(context);
            Map<String, String> stockMap = new StockMap().getMap();
            for (Map.Entry<String, String> stock : stockMap.entrySet()) {
                RealtimeDataCollecter collecter = new RealtimeDataCollecter();
                Map<String, List<String>> data = collecter.collect(stock.getKey());
                RealTimeDataConverter converter = new RealTimeDataConverter();
                List<Put> puts = converter.toPut(data);
                if(puts!=null){
                    stockDB.save(TABLE_STOCK_DAILY,puts);
                }else{
                    LOG.error("fail to get realtime data with " + stock.getKey());
                }
            }
            context.close();
    }
}
