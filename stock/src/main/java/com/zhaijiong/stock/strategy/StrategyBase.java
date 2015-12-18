package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.indicators.Indicators;
import com.zhaijiong.stock.indicators.TDXFunction;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Set;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-3.
 */
public class StrategyBase {
    protected static final Logger LOG = LoggerFactory.getLogger(StrategyBase.class);

    public static final String DEFAULT_STRATEGY_NAME = "cheetah";
    protected String name;

    /**
     * 如果股票为新股，stockdata list数量小于60，则不再重复爬去
     */
    protected Set<String> blackList = Sets.newConcurrentHashSet();

    @Autowired
    public StockDB stockDB;
    @Autowired
    @Qualifier("function")
    public TDXFunction function;
    @Autowired
    @Qualifier("indicators")
    public Indicators indicators;

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
        DateRange dateRange = DateRange.getRange(240);
        List<StockData> stockDataList = stockDB.getStockDataDaily(symbol,dateRange.start(),dateRange.stop());
        if(stockDataList.size()<60){
            stockDataList = Provider.dailyData(symbol, 500, true);
            if(stockDataList.size()<60){
                blackList.add(symbol);  //如果获取的股票数据依然小于60天，则不再抓取
                return stockDataList;
            }
            stockDB.saveStockData(Constants.TABLE_STOCK_DAILY,stockDataList);
        }
        return stockDataList;
    }
}
