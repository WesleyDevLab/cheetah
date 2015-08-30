package com.zhaijiong.stock;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.collect.FinanceDataCollecter;
import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.collect.MoneyFlowDataCollecter;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.convert.MinuteDataConverter;
import com.zhaijiong.stock.convert.MoneyFlowDataConverter;
import com.zhaijiong.stock.provider.*;
import com.zhaijiong.stock.model.StockData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 成交量 单位：手
 * 成交金额 单位：元
 *
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-27.
 */
public class s {
    /**
     * 获取实时数据股票数据
     * @param symbol
     * @return
     */
    public static StockData realtimeData(String symbol){
        StockData stockData = RealTimeDataProvider.get(symbol);
        return stockData;
    }

    /**
     * 获取日线级别历史数据
     * @param symbol
     * @return
     */
    public static List<StockData> dailyData(String symbol){
        DateRange range = DateRange.getRange(120);
        List<StockData> collect = DailyDataProvider.get(symbol, range.start(), range.stop());
        return collect;
    }

    public static List<StockData> dailyData(String symbol,String startDate,String stopDate){
        List<StockData> collect = DailyDataProvider.get(symbol, startDate, stopDate);
        return collect;
    }

    public static StockData minuteData(String symbol,String type){
        DateRange range = DateRange.getRange(10);
        List<StockData> stockDataList = minuteData(symbol,range.start(),range.stop(),type);
        if(stockDataList.size()>=1){
            return stockDataList.get(stockDataList.size()-1);
        }else{
            return new StockData(symbol);
        }
    }

    public static List<StockData> minuteData(String symbol,String startDate,String stopDate,String type){
        List<StockData> stockList = MinuteDataProvider.get(symbol, startDate, stopDate, type);
        return stockList;
    }

    public static StockData moneyFlowData(String symbol){
        StockData stockData = MoneyFlowDataProvider.get(symbol);
        return stockData;
    }

    public static List<StockData> moneyFlowData(String symbol,String startDate,String stopDate){
        List<StockData> stockDataList = MoneyFlowDataProvider.get(symbol,startDate,stopDate);
        return stockDataList;
    }

    /**
     * 历史财报
     * @param symbol
     * @return
     */
    public static List<StockData> financeData(String symbol, String startDate, String stopDate){
        List<StockData> stockDataList = FinanceDataProvider.get(symbol,startDate,stopDate);
        return stockDataList;
    }

    /**
     * 最新一期财报
     * @param symbol
     * @return
     */
    public static StockData financeData(String symbol){
        DateRange range = DateRange.getRange(365);
        List<StockData> stockDataList = FinanceDataProvider.get(symbol,range.start(),range.stop());
        if(stockDataList.size()>=1){
            return stockDataList.get(stockDataList.size()-1);
        }else{
            return new StockData(symbol);
        }
    }

    public static List<StockData> financeYearData(String symbol){
        List<StockData> stockDataList = FinanceDataProvider.getYear(symbol);
        return stockDataList;
    }

}
