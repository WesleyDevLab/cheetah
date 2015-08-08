package com.zhaijiong.stock.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zhaijiong.stock.*;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.zhaijiong.stock.Constants.*;
import static com.zhaijiong.stock.Constants.TABLE_CF_BASE;
import static org.apache.hadoop.hbase.util.Bytes.toBytes;
import static org.apache.hadoop.hbase.util.Bytes.toDouble;

/**
 * Created by eryk on 15-7-25.
 */
public class StockDB {
    private static final Logger LOG = LoggerFactory.getLogger(StockDB.class);


    private String tableName;

    Context context;

    public StockDB(String tableName, Context context){
        this.context = context;
        this.tableName = tableName;
    }

    public void save(List<Stock> stocks) throws IOException {
        HTableInterface table = context.getTable(tableName);
        List<Put> puts = Lists.newLinkedList();
        for(Stock stock:stocks){
            byte[] rowkey = Utils.getRowkeyWithMD5Prefix(stock);
            Put put = new Put(rowkey);
            addColumn(stock, put);
            puts.add(put);
        }
        table.put(puts);
        context.closeTable(table);
    }


    public void save(Stock stock) throws IOException {
        HTableInterface table = context.getTable(tableName);
        Put put = new Put(Utils.getRowkeyWithMD5Prefix(stock));
        addColumn(stock, put);
        table.put(put);
        context.close();
    }

    public List<Stock> get(String symbol,String startDate,String stopDate) throws IOException {
        List<Stock> stocks = Lists.newLinkedList();
        HTableInterface table = context.getTable(tableName);
        Scan scan = new Scan();
        scan.setStartRow(Bytes.add(symbol.getBytes(), startDate.getBytes()));
        scan.setStopRow(Bytes.add(symbol.getBytes(), stopDate.getBytes()));
        scan.setCaching(200);
        if(LOG.isDebugEnabled()){
            LOG.debug("startRow:" + Bytes.toString(Bytes.add(symbol.getBytes(), startDate.getBytes())));
            LOG.debug("stopRow:" + Bytes.toString(Bytes.add(symbol.getBytes(), stopDate.getBytes())));
        }
        ResultScanner scanner = table.getScanner(scan);
        for(Result result : scanner){
            Stock stock = new Stock();
            List<KeyValue> list = result.list();
            for(KeyValue kv : list){
                if(Bytes.toString(kv.getQualifier()).equals("close")){
                    stock.close = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("high")){
                    stock.high = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("low")){
                    stock.low = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("open")){
                    stock.open = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("lastClose")){
                    stock.lastClose = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("changeAmount")){
                    stock.changeAmount = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("change")){
                    stock.change = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("turnoverRate")){
                    stock.turnoverRate = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("volume")){
                    stock.volume = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("amount")){
                    stock.amount = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("totalValue")){
                    stock.totalValue = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("marketValue")){
                    stock.marketValue = toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("amplitude")){
                    stock.amplitude = toDouble(kv.getValue());
                }
                stock.date = Utils.getStockDate(kv.getRow());
                stock.symbol = Utils.getStockSymbol(kv.getRow());
            }
            stocks.add(stock);
        }
        context.closeTable(table);
        return stocks;
    }

    /**
     * 根据股票代码和指标获取一段时间内的值
     * @param symbol
     * @param metric
     * @param startDate
     * @param stopDate
     * @return
     */
    public List<Point> getDate(String symbol,String metric,String startDate,String stopDate) throws IOException {
        List<Point> points = Lists.newLinkedList();
        HTableInterface table = context.getTable(tableName);
        Scan scan = new Scan();
        scan.setStartRow(Bytes.add(symbol.getBytes(), startDate.getBytes()));
        scan.setStopRow(Bytes.add(symbol.getBytes(), stopDate.getBytes()));
        scan.addColumn(TABLE_CF_BASE, toBytes(metric));
        ResultScanner scanner = table.getScanner(scan);
        for(Result result : scanner){
            KeyValue keyValue = result.getColumnLatest(TABLE_CF_BASE, toBytes(metric));
            byte[] key = keyValue.getKey();
            Date date = Utils.getStockDate(key);
            double val = toDouble(keyValue.getValue());

        }
        return points;
    }

    private void addColumn(Stock stock, Put put) {
        put.add(TABLE_CF_BASE, CLOSE, toBytes(stock.close));
        put.add(TABLE_CF_BASE, HIGH, toBytes(stock.high));
        put.add(TABLE_CF_BASE, LOW, toBytes(stock.low));
        put.add(TABLE_CF_BASE, OPEN, toBytes(stock.open));
        put.add(TABLE_CF_BASE, LAST_CLOSE, toBytes(stock.lastClose));
        put.add(TABLE_CF_BASE, CHANGE_AMOUNT, toBytes(stock.changeAmount));
        put.add(TABLE_CF_BASE, CHANGE, toBytes(stock.change));
        put.add(TABLE_CF_BASE, TURNOVER_RATE, toBytes(stock.turnoverRate));
        put.add(TABLE_CF_BASE, VOLUME, toBytes(stock.volume));
        put.add(TABLE_CF_BASE, AMOUNT, toBytes(stock.amount));
        put.add(TABLE_CF_BASE, TOTAL_VALUE, toBytes(stock.totalValue));
        put.add(TABLE_CF_BASE, MARKET_VALUE, toBytes(stock.marketValue));
        put.add(TABLE_CF_BASE, AMPLITUDE, toBytes(stock.amplitude));
    }

}
