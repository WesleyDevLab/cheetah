package com.zhaijiong.stock.provider;

import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockBlock;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Tick;
import com.zhaijiong.stock.provider.*;
import com.zhaijiong.stock.tools.StockCategory;
import com.zhaijiong.stock.tools.StockList;

import java.util.List;
import java.util.Map;

/**
 * 成交量,单位：手
 * 成交金额,单位：万
 * 总市值,单位:亿
 * 流通市值,单位:亿
 * 外盘,单位:手
 * 内盘,单位:手
 *
 * author: xuqi.xq
 * mail: xuqi.xq@gmail.com
 * date: 15-8-27.
 */
public class Provider {

    /**
     * 获取日线级别最近120天历史数据
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
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return
     */
    public static List<StockData> dailyData(String symbol,String startDate,String stopDate){
        List<StockData> collect = DailyDataProvider.get(symbol, startDate, stopDate);
        return collect;
    }

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
     * 获取最新一笔股票分钟级别数据
     * @param symbol
     * @param type  参数值：5,15,30,60
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

    /**
     * 获取指定时间段内历史分钟级别数据，受数据源限制
     * @param symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @param type
     * @return
     */
    public static List<StockData> minuteData(String symbol,String startDate,String stopDate,String type){
        List<StockData> stockList = MinuteDataProvider.get(symbol, startDate, stopDate, type);
        return stockList;
    }

    /**
     * 获取个股当日资金流数据
     * @param symbol
     * @return
     */
    public static StockData moneyFlowData(String symbol){
        StockData stockData = MoneyFlowDataProvider.get(symbol);
        return stockData;
    }

    /**
     * 获取个股指定时间段内资金流数据，受数据源限制
     * @param symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return
     */
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
     * @param type 输入值：1,5,10
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
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return
     */
    public static List<StockData> financeData(String symbol, String startDate, String stopDate){
        List<StockData> stockDataList = FinanceDataProvider.get(symbol, startDate, stopDate);
        return stockDataList;
    }

    /**
     * 最新一期财报数据
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

    /**
     * 股票年报数据
     * @param symbol
     * @return
     */
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

    /**
     * 获取股票版块数据
     * map key: 股票的版块分类名称，包含三项：概念，地区，行业
     * value: list是版块分类下的版块，每个版块包含一个股票列表
     * @return
     */
    public static Map<String,List<StockBlock>> stockBlock(){
        return StockCategory.getCategory();
    }

    /**
     * 获取股票列表
     * @return
     */
    public static List<String> stockList(){
        return StockList.getList();
    }

    /**
     * 获取交易中的股票列表，剔除了退市和停牌的股票
     * @return
     */
    public static List<String> tradingStockList(){
        return StockList.getTradingStockList();
    }

}
