package com.zhaijiong.stock.dao;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.*;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Pair;
import com.zhaijiong.stock.common.Utils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.zhaijiong.stock.common.Constants.*;
import static com.zhaijiong.stock.common.Constants.TABLE_CF_BASE;
import static com.zhaijiong.stock.common.Utils.getRowkeyWithMd5PrefixAndDateSuffix;
import static com.zhaijiong.stock.common.Utils.getRowkeyWithMD5Prefix;
import static org.apache.hadoop.hbase.util.Bytes.toBytes;
import static org.apache.hadoop.hbase.util.Bytes.toDouble;

/**
 * Created by eryk on 15-7-25.
 */
public class StockDB {
    private static final Logger LOG = LoggerFactory.getLogger(StockDB.class);

    Context context;

    public StockDB(Context context){
        this.context = context;
    }

    /**
     * rowkey : md5(symbol,4) + symbol + yyyyMMddhhmmss
     * @param stocks
     * @throws IOException
     */
    public void saveStockDailyData(List<Stock> stocks) throws IOException {
        HTableInterface table = context.getTable(Constants.TABLE_STOCK_DAILY);
        List<Put> puts = Lists.newLinkedList();
        for (Stock stock : stocks) {
            byte[] rowkey = getRowkeyWithMd5PrefixAndDateSuffix(stock);
            Put put = new Put(rowkey);
            addColumn(stock, put);
            puts.add(put);
        }
        table.put(puts);
        context.closeTable(table);
    }

    /**
     * 存储stock列表,key为symbol，value为股票名称
     * rowkey : md5(symbol,4) + symbol
     * @param stockList
     * @throws IOException
     */
    public void saveStockList(List<Pair<String, String>> stockList) throws IOException {
        HTableInterface table = context.getTable(Constants.TABLE_STOCK_INFO);
        List<Put> puts = Lists.newLinkedList();
        for (Pair<String, String> pair : stockList) {
            Put put = new Put(getRowkeyWithMD5Prefix(pair.getVal()));
            put.add(Constants.TABLE_CF_INFO,Constants.NAME,Bytes.toBytes(pair.getKey()));
            puts.add(put);
        }
        table.put(puts);
        LOG.info("total stock count:"+stockList.size());
        context.closeTable(table);
    }

    /**
     * rowkey : md5(symbol,4) + symbol + yyyyMMddhhmmss
     * @param stock
     * @throws IOException
     */
    public void saveStock(Stock stock) throws IOException {
        HTableInterface table = context.getTable(TABLE_STOCK_DAILY);
        Put put = new Put(getRowkeyWithMd5PrefixAndDateSuffix(stock));
        addColumn(stock, put);
        table.put(put);
        context.close();
    }

    public List<Stock> getStockHistory(String symbol, String startDate, String stopDate) throws IOException {
        List<Stock> stocks = Lists.newLinkedList();
        HTableInterface table = context.getTable(TABLE_STOCK_DAILY);
        Scan scan = new Scan();
        scan.setStartRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol,startDate));
        scan.setStopRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol,stopDate));
        scan.setCaching(200);
        if (LOG.isDebugEnabled()) {
            LOG.debug("startRow:" + getRowkeyWithMd5PrefixAndDateSuffix(symbol,startDate));
            LOG.debug("stopRow:" + getRowkeyWithMd5PrefixAndDateSuffix(symbol,stopDate));
        }
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            Stock stock = new Stock();
            List<KeyValue> list = result.list();
            for (KeyValue kv : list) {
                if (Bytes.toString(kv.getQualifier()).equals("close")) {
                    stock.close = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("high")) {
                    stock.high = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("low")) {
                    stock.low = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("open")) {
                    stock.open = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("lastClose")) {
                    stock.lastClose = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("changeAmount")) {
                    stock.changeAmount = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("change")) {
                    stock.change = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("turnoverRate")) {
                    stock.turnoverRate = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("volume")) {
                    stock.volume = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("amount")) {
                    stock.amount = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("totalValue")) {
                    stock.totalValue = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("marketValue")) {
                    stock.marketValue = toDouble(kv.getValue());
                }
                if (Bytes.toString(kv.getQualifier()).equals("amplitude")) {
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
     *
     * @param symbol
     * @param metric
     * @param startDate
     * @param stopDate
     * @return
     */
    public List<Point> getStockByDate(String symbol, String metric, String startDate, String stopDate) throws IOException {
        List<Point> points = Lists.newLinkedList();
        HTableInterface table = context.getTable(TABLE_STOCK_DAILY);
        Scan scan = new Scan();
        scan.setStartRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate));
        scan.setStopRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate));
        scan.addColumn(TABLE_CF_BASE, toBytes(metric));
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
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
