package com.zhaijiong.stock.convert;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.collect.MoneyFlowDataCollecter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Utils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zhaijiong.stock.common.Constants.TABLE_CF_DATA;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-19.
 */
public class MoneyFlowDataConverter implements Converter<Map<String,String>>{
    private static final Logger LOG = LoggerFactory.getLogger(MoneyFlowDataConverter.class);

    private String symbol;

    private List<String> columnNames = Lists.newArrayList(
            "今日主力净流入",      //0    今日主力净流入
            "主力净比",         //1 主力净比
            "今日超大单净流入",     //2 今日超大单净流入
            "超大单净比",        //3 超大单净比
            "今日大单净流入",      //4 今日大单净流入
            "大单净比",         //5 大单净比
            "今日中单净流入",      //6 今日中单净流入
            "中单净比",         //7 中单净比
            "今日小单净流入",      //8 今日小单净流入
            "小单净比",         //9 小单净比
            "未知1",           //10    未知
            "未知2",           //11    未知
            "超大单:流入",       //12    超大单:流入
            "超大单:流出",       //13    超大单:流出
            "大单:流入",       //14    大单:流入
            "大单:流出",       //15    大单:流出
            "中单:流入",       //16    中单:流入
            "中单:流出",       //17    中单:流出
            "小单:流入",       //17    小单:流入
            "小单:流出",       //18    小单:流出
            "未知3",           //19    未知
            "未知4"           //20    未知

    );

    public MoneyFlowDataConverter(String symbol){
        this.symbol =symbol;
    }

    @Override
    public List<Put> toPut(Map<String, String> dataMap) {
        List<Put> puts = Lists.newLinkedList();
        //时间设置到15:00:00以方便后期获取日线级别数据,即每天一条记录
        String dateStr = Utils.formatDate(new Date(), "yyyyMMdd") + "1500";
        byte[] rowkey = Utils.getRowkeyWithMd5PrefixAndDateSuffix(symbol, dateStr);
        Put put = new Put(rowkey);
        String data = dataMap.get("data");
        if(Strings.isNullOrEmpty(data)){
            LOG.error("fail to get money flow data from "+symbol);
            return puts;
        }
        String[] values = data.split(",");
        for(int i=0;i<values.length;i++){
            put.add(TABLE_CF_DATA, Bytes.toBytes(columnNames.get(i)), Bytes.toBytes(Double.parseDouble(values[i])));
        }
        puts.add(put);
        return puts;
    }

    public static void main(String[] args) {
        MoneyFlowDataCollecter collecter= new MoneyFlowDataCollecter();
        Map<String, String> data = collecter.collect("300282");
        MoneyFlowDataConverter converter = new MoneyFlowDataConverter("600376");
        converter.toPut(data);
    }
}
