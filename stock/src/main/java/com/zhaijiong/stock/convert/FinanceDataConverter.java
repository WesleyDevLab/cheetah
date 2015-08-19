package com.zhaijiong.stock.convert;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.ArticleType;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-19.
 */
public class FinanceDataConverter implements Converter<Map<String,Map<String,String>>>{

    private String symbol;

    public FinanceDataConverter(String symbol){
        this.symbol = symbol;
    }

    @Override
    public List<Put> toPut(Map<String, Map<String, String>> reports) {
        List<Put> puts = Lists.newLinkedList();
        for(Map.Entry<String,Map<String,String>> entry:reports.entrySet()){
            String date = Utils.formatDate(Utils.parseDate(entry.getKey(),"yyyy-MM-dd"),"yyyyMMddHHmm");
            byte[] rowkey = Bytes.add(Utils.getRowkeyWithMD5Prefix(symbol), ArticleType.FINANCIAL_STATEMENTS.getType(),Bytes.toBytes(date));
            Put put = new Put(rowkey);
            for(Map.Entry<String,String> kv:entry.getValue().entrySet()){
                put.add(Constants.TABLE_CF_ARTICLE,Bytes.toBytes(kv.getKey()),Bytes.toBytes(kv.getValue()));
            }
            puts.add(put);
        }
        return puts;
    }
}
