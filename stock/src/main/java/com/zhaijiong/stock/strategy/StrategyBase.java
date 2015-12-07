package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.indicators.TDXFunction;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
    @Autowired
    @Qualifier("function")
    public TDXFunction function;

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

    public TDXFunction getFunction() {
        return function;
    }

    public void setFunction(TDXFunction function) {
        this.function = function;
    }

    protected List<StockData> getStockDataByType(PeriodType type,String symbol) {
        List<StockData> stockDataList;
        switch (type){
            case FIVE_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "5"));
                break;
            case FIFTEEN_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "15"));
                break;
            case THIRTY_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "30"));
                break;
            case SIXTY_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "60"));
                break;
            case DAY:
                stockDataList = getDailyData(symbol);
                break;
            default:
                stockDataList = getDailyData(symbol);
        }
        return stockDataList;
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
