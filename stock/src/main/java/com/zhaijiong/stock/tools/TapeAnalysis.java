package com.zhaijiong.stock.tools;

import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.collect.RealtimeDataCollecter;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.convert.MinuteDataConverter;
import com.zhaijiong.stock.convert.RealTimeDataConverter;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-26.
 */
public class TapeAnalysis {

    public static void analyze(){
        DateRange dateRange = DateRange.getRange(0);
        MinuteDataCollecter minuteDataCollecter = new MinuteDataCollecter(dateRange.start(),dateRange.stop(),"15");
        Map<String, Map<String, String>> collect = minuteDataCollecter.collect("000001");
        for(Map.Entry<String, Map<String, String>> entry:collect.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
    }

    public static void main(String[] args) {
        TapeAnalysis.analyze();
    }
}
