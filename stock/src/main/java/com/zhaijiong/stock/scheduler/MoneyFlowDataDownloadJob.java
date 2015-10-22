package com.zhaijiong.stock.scheduler;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.MoneyFlowDataProvider;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-20.
 */
public class MoneyFlowDataDownloadJob extends JobBase{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<String> symbolList = getSymbolList();
        for(String symbol :symbolList){
            StockData data = MoneyFlowDataProvider.get(symbol);
            stockDB.saveStockData(Constants.TABLE_STOCK_DAILY, Lists.newArrayList(data));
        }
    }
}
