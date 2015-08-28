package com.zhaijiong.stock;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.collect.FinanceDataCollecter;
import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.collect.MoneyFlowDataCollecter;
import com.zhaijiong.stock.collect.RealtimeDataCollecter;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.convert.MinuteDataConverter;
import com.zhaijiong.stock.convert.MoneyFlowDataConverter;
import com.zhaijiong.stock.convert.RealTimeDataConverter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-27.
 */
public class s {
    public static Map<String,Double> realtimeData(String symbol){
        RealtimeDataCollecter realtimeDataCollecter = new RealtimeDataCollecter();
        Map<String, List<String>> collect = realtimeDataCollecter.collect(symbol);
        return RealTimeDataConverter.toMap(collect);
    }

    //TODO 历史资金流向
    public static Map<String,Double> moneyFlow(String symbol){
        MoneyFlowDataCollecter moneyFlowDataCollecter = new MoneyFlowDataCollecter();
        Map<String, String> collect1 = moneyFlowDataCollecter.collect(symbol);
        MoneyFlowDataConverter moneyFlowDataConverter = new MoneyFlowDataConverter(symbol);
        return moneyFlowDataConverter.toMap(collect1);
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

    public static Map<String, Map<String, Double>> minuteDate(String symbol,String type){
        DateRange range = DateRange.getRange(10);
        MinuteDataCollecter minuteDataCollecter = new MinuteDataCollecter(range.start(),range.stop(),type);
        Map<String, Map<String, String>> collect = minuteDataCollecter.collect(symbol);
        MinuteDataConverter minuteDataConverter = new MinuteDataConverter();
        Map<String, Map<String, Double>> stringMapMap = minuteDataConverter.toMap(collect);
        return stringMapMap;
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
        values.put("date",String.valueOf(Utils.parseDate(date,"yyyy-MM-dd").getTime()));
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

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String,Double> values = s.realtimeData("600376");
        Utils.printMap(values);


        Map<String, Double> moneyFlow = s.moneyFlow("600376");
        Utils.printMap(moneyFlow);

//        Map<String, Double> stringMapMap = s.financialStatementLatest("600376");
//        for(Map.Entry<String, Double> entry:stringMapMap.entrySet()){
//            System.out.println(entry.getKey()+":"+entry.getValue());
//        }

        Map<String, Map<String, Double>> stringMapMap1 = s.minuteDate("600376", "15");
        for(Map.Entry<String,Map<String, Double>> entry:stringMapMap1.entrySet()){
            System.out.println(entry.getKey());
            Utils.printMap(entry.getValue());
        }

        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}
