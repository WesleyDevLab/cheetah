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
import com.zhaijiong.stock.provider.MinuteDataProvider;
import com.zhaijiong.stock.provider.MoneyFlowDataProvider;
import com.zhaijiong.stock.provider.RealTimeDataProvider;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.DailyDataProvider;

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
        return Lists.reverse(collect);
    }

    public static StockData minuteData(String symbol,String type){
        DateRange range = DateRange.getRange(10);
        List<StockData> stockList = minuteData(symbol,range.start(),range.stop(),type);
        if(stockList.size()>=1){
            return stockList.get(stockList.size()-1);
        }else{
            return null;
        }
    }

    public static List<StockData> minuteData(String symbol,String startDate,String stopDate,String type){
        List<StockData> stockList = MinuteDataProvider.get(symbol,startDate,stopDate,type);
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
    public static Map<String, Map<String, String>> financialStatementHistory(String symbol){
        FinanceDataCollecter collecter = new FinanceDataCollecter();
        Map<String, Map<String, String>> collect = collecter.collect(symbol);
        return collect;
    }

    /**
     * 最新一期财报
     * @param symbol
     * @return
     */
    public static Map<String, Double> financialStatementLatest(String symbol){
        FinanceDataCollecter collecter = new FinanceDataCollecter();
        Map<String, Map<String, String>> collect = collecter.collect(symbol);
        String date = Lists.newArrayList(collect.keySet()).get(collect.keySet().size() - 1);
        Map<String, String> values = collect.get(date);
        values.put("date",String.valueOf(Utils.str2Date(date, "yyyy-MM-dd").getTime()));
        Map<String, Double> stringObjectMap = Maps.transformEntries(values, new Maps.EntryTransformer<String, String, Double>() {
            @Override
            public Double transformEntry(String key, String value) {
                if (Utils.isDouble(value)) {
                    return Double.parseDouble(value);
                }
                return 0d;
            }
        });
        return stringObjectMap;
    }

}
