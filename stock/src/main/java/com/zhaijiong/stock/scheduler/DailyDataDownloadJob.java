package com.zhaijiong.stock.scheduler;

import com.zhaijiong.stock.collect.RealtimeDataCollecter;
import com.zhaijiong.stock.convert.RealTimeDataCollecter;
import org.apache.hadoop.hbase.client.Put;
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
public class DailyDataDownloadJob extends JobBase{
    private static final Logger LOG = LoggerFactory.getLogger(DailyDataDownloadJob.class);

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
            List<String> symbols = getSymbolList();
            for (String symbol : symbols) {
                RealtimeDataCollecter collecter = new RealtimeDataCollecter();
                Map<String, List<String>> data = collecter.collect(symbol);
                RealTimeDataCollecter converter = new RealTimeDataCollecter();
                List<Put> puts = converter.toPut(data);
                if(puts!=null){
                    stockDB.save(TABLE_STOCK_DAILY,puts);
                }else{
                    LOG.error("fail to get realtime data with " + symbol);
                }
            }
            context.close();
    }
}
