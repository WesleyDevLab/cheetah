package com.zhaijiong.stock.dao;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.Utils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.zhaijiong.stock.Constants.TABLE_CF_BASE;

/**
 * Created by eryk on 15-7-25.
 */
public class StockDAO {
    private String tableName;

    Context context;

    public StockDAO(String tableName,Context context){
        this.context = context;
        this.tableName = tableName;
    }

    public void save(List<Stock> stocks) throws IOException {
        HTableInterface table = context.getTable(tableName);
        List<Put> puts = Lists.newLinkedList();
        for(Stock stock:stocks){
            byte[] rowkey = getRowkey(stock);
            Put put = new Put(rowkey);
            addColumn(stock, put);
            puts.add(put);
        }
        table.put(puts);
        context.closeTable(table);
    }


    public void save(Stock stock) throws IOException {
        HTableInterface table = context.getTable(tableName);
        Put put = new Put(getRowkey(stock));
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
        System.out.println("startRow:"+Bytes.toString(Bytes.add(symbol.getBytes(), startDate.getBytes())));
        System.out.println("stopRow:"+Bytes.toString(Bytes.add(symbol.getBytes(), stopDate.getBytes())));

        ResultScanner scanner = table.getScanner(scan);
        for(Result result : scanner){
            Stock stock = new Stock();
            List<KeyValue> list = result.list();
            for(KeyValue kv : list){
                if(Bytes.toString(kv.getQualifier()).equals("close")){
                    stock.close = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("high")){
                    stock.high = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("low")){
                    stock.low = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("open")){
                    stock.open = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("lastClose")){
                    stock.lastClose = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("changeAmount")){
                    stock.changeAmount = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("change")){
                    stock.change = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("turnoverRate")){
                    stock.turnoverRate = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("volume")){
                    stock.volume = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("amount")){
                    stock.amount = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("totalValue")){
                    stock.totalValue = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("marketValue")){
                    stock.marketValue = Bytes.toDouble(kv.getValue());
                }
                if(Bytes.toString(kv.getQualifier()).equals("amplitude")){
                    stock.amplitude = Bytes.toDouble(kv.getValue());
                }
                stock.date = bytes2Date(Bytes.tail(kv.getRow(),14));
                stock.symbol = symbol;
            }
            stocks.add(stock);
        }
        context.closeTable(table);
        return stocks;
    }

    private void addColumn(Stock stock, Put put) {
        put.add(TABLE_CF_BASE, Bytes.toBytes("close"),Bytes.toBytes(stock.close));
        put.add(TABLE_CF_BASE,Bytes.toBytes("high"),Bytes.toBytes(stock.high));
        put.add(TABLE_CF_BASE,Bytes.toBytes("low"),Bytes.toBytes(stock.low));
        put.add(TABLE_CF_BASE,Bytes.toBytes("open"),Bytes.toBytes(stock.open));
        put.add(TABLE_CF_BASE,Bytes.toBytes("lastClose"),Bytes.toBytes(stock.lastClose));
        put.add(TABLE_CF_BASE,Bytes.toBytes("changeAmount"),Bytes.toBytes(stock.changeAmount));
        put.add(TABLE_CF_BASE,Bytes.toBytes("change"),Bytes.toBytes(stock.change));
        put.add(TABLE_CF_BASE,Bytes.toBytes("turnoverRate"),Bytes.toBytes(stock.turnoverRate));
        put.add(TABLE_CF_BASE,Bytes.toBytes("volume"),Bytes.toBytes(stock.volume));
        put.add(TABLE_CF_BASE,Bytes.toBytes("amount"),Bytes.toBytes(stock.amount));
        put.add(TABLE_CF_BASE,Bytes.toBytes("totalValue"),Bytes.toBytes(stock.totalValue));
        put.add(TABLE_CF_BASE,Bytes.toBytes("marketValue"),Bytes.toBytes(stock.marketValue));
        put.add(TABLE_CF_BASE,Bytes.toBytes("amplitude"),Bytes.toBytes(stock.amplitude));
    }

    private byte[] getRowkey(Stock stock) {
        return Bytes.add(stock.symbol.getBytes(),
                Bytes.toBytes(Utils.formatDate(stock.date, "yyyyMMddhhmmss")));
    }

    private Date bytes2Date(byte[] bytes){
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMddhhmmss");
        DateTime dateTime = DateTime.parse(Bytes.toString(bytes),format);
        return dateTime.toDate();
    }
}
