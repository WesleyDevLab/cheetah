package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-3.
 */
public class StrategyBase {

    public static final String DEFAULT_STRATEGY_NAME = "cheetah";
    protected String name;

    @Autowired
    public StockDB stockDB;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StockDB getStockDB() {
        return stockDB;
    }

    public void setStockDB(StockDB stockDB) {
        this.stockDB = stockDB;
    }

    public List<StockData> getDailyData(String symbol){
        DateRange dateRange = DateRange.getRange(250);
        List<StockData> stockDataList = stockDB.getStockDataDaily(symbol,dateRange.start(),dateRange.stop());
        if(stockDataList.size()==0 || stockDataList.size()<120){
            stockDataList = Provider.dailyData(symbol, 500, true);
            stockDB.saveStockData(Constants.TABLE_STOCK_DAILY,stockDataList);
        }
        return stockDataList;
    }
}
