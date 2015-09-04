package com.zhaijiong.stock;

import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Tick;
import com.zhaijiong.stock.provider.*;

import java.util.List;

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

    /**
     * 获取指定时间段内的日线股票数据
     * @param symbol
     * @param startDate
     * @param stopDate
     * @return
     */
    public static List<StockData> dailyData(String symbol,String startDate,String stopDate){
        List<StockData> collect = DailyDataProvider.get(symbol, startDate, stopDate);
        return collect;
    }

    /**
     * 获取最新一笔股票分钟级别数据
     * @param symbol
     * @param type
     * @return
     */
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
     * 大盘资金流向历史数据
     * @return
     */
    public static List<StockData> moneyFlowDapanData(){
        List<StockData> stockDataList = MoneyFlowDataProvider.getDapan();
        return stockDataList;
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     * @param type 1,5,10
     * @return
     */
    public static List<StockData> moneyFlowIndustryData(String type){
        List<StockData> stockDataList = MoneyFlowDataProvider.getIndustry(type);
        return stockDataList;
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     * @param type 1,5,10
     * @return
     */
    public static List<StockData> moneyFlowConceptData(String type){
        List<StockData> stockDataList = MoneyFlowDataProvider.getConcept(type);
        return stockDataList;
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     * @param type 1,5,10
     * @return
     */
    public static List<StockData> moneyFlowRegionData(String type){
        List<StockData> stockDataList = MoneyFlowDataProvider.getRegion(type);
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

    /**
     * 获取最新一天股票逐笔数据
     * @param symbol
     * @return
     */
    public static List<Tick> tickData(String symbol){
        List<Tick> ticks = TickDataProvider.get(symbol);
        return ticks;
    }

    /**
     * 获取指定日期逐笔股票数据
     * @param symbol
     * @param date  格式: yyyyMMdd
     * @return
     */
    public static List<Tick> tickData(String symbol,String date){
        String _date = Utils.formatDate(Utils.str2Date(date,"yyyyMMdd"),"yyyy-MM-dd");
        List<Tick> ticks = TickDataProvider.get(symbol,_date);
        return ticks;
    }
}
