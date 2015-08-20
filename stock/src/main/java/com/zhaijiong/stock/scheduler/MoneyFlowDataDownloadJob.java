package com.zhaijiong.stock.scheduler;

import com.zhaijiong.stock.collect.MoneyFlowDataCollecter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.convert.MoneyFlowDataConverter;
import org.apache.hadoop.hbase.client.Put;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-20.
 */
public class MoneyFlowDataDownloadJob extends JobBase{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<String> symbolList = getSymbolList();
        MoneyFlowDataCollecter collecter = new MoneyFlowDataCollecter();
        for(String symbol :symbolList){
            Map<String, String> data = collecter.collect(symbol);
            MoneyFlowDataConverter converter = new MoneyFlowDataConverter(symbol);
            List<Put> puts = converter.toPut(data);
            stockDB.save(Constants.TABLE_STOCK_DAILY,puts);
        }
    }
}
