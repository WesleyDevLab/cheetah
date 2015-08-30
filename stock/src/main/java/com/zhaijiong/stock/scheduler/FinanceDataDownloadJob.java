package com.zhaijiong.stock.scheduler;

import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.FinanceDataProvider;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-19.
 */
public class FinanceDataDownloadJob extends JobBase {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<String> stockSymbols = getSymbolList();
        DateRange range = DateRange.getRange(365);
        for(String symbol:stockSymbols){
            List<StockData> reports = FinanceDataProvider.get(symbol, range.start(), range.stop());
            stockDB.saveStockData(Constants.TABLE_ARTICLE, reports);
        }
    }
}
