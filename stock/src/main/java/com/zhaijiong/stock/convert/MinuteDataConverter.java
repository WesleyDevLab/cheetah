package com.zhaijiong.stock.convert;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.collect.Collecter;
import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.common.Constants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;
import java.util.Map;

import static com.zhaijiong.stock.common.Constants.TABLE_CF_DATA;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-16.
 */
public class MinuteDataConverter implements Converter<Map<String, Map<String,String>>>{
    @Override
    public List<Put> toPut(Map<String, Map<String,String>> map) {
        List<Put> puts = Lists.newLinkedList();
        for(Map.Entry<String,Map<String,String>> entry:map.entrySet()){
            Put put = new Put(Bytes.toBytes(entry.getKey()));
            put.add(TABLE_CF_DATA, Constants.OPEN,Bytes.toBytes(Double.parseDouble(entry.getValue().get("open"))));
            put.add(TABLE_CF_DATA, Constants.HIGH,Bytes.toBytes(Double.parseDouble(entry.getValue().get("high"))));
            put.add(TABLE_CF_DATA, Constants.LOW,Bytes.toBytes(Double.parseDouble(entry.getValue().get("low"))));
            put.add(TABLE_CF_DATA, Constants.CLOSE, Bytes.toBytes(Double.parseDouble(entry.getValue().get("close"))));
            put.add(TABLE_CF_DATA, Constants.VOLUME,Bytes.toBytes(Integer.parseInt(entry.getValue().get("volume"))/100d));
            puts.add(put);
        }
        return puts;
    }

    public static void main(String[] args) {
        String startDate = "20150814";
        String stopDate = "20150815";
        Collecter collect = new MinuteDataCollecter(startDate, stopDate, "5");
        Map<String, Map<String,String>> stocks = collect.collect("600376");
        MinuteDataConverter converter = new MinuteDataConverter();
        List<Put> puts = converter.toPut(stocks);
        for(Put put :puts){
            Map<byte[], List<KeyValue>> familyMap = put.getFamilyMap();
            List<KeyValue> keyValues = familyMap.get(TABLE_CF_DATA);
            for(KeyValue kv:keyValues){
                System.out.println(Bytes.toString(kv.getRow()));
                System.out.println(Bytes.toString(kv.getQualifier())+":"+Bytes.toDouble(kv.getValue()));
            }
        }
    }
}
