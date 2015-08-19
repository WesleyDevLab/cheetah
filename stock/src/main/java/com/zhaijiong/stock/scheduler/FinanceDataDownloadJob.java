package com.zhaijiong.stock.scheduler;

import com.zhaijiong.stock.collect.FinanceDataCollecter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.convert.FinanceDataConverter;
import org.apache.hadoop.hbase.client.Put;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-19.
 */
public class FinanceDataDownloadJob extends JobBase {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<String> stockSymbols = stockDB.getStockSymbols();
        FinanceDataCollecter collecter= new FinanceDataCollecter();
        for(String symbol:stockSymbols){
            Map<String, Map<String, String>> reports = collecter.collect(symbol);
            FinanceDataConverter converter = new FinanceDataConverter(symbol);
            List<Put> puts = converter.toPut(reports);
            stockDB.save(Constants.TABLE_ARTICLE,puts);
        }
    }
}
