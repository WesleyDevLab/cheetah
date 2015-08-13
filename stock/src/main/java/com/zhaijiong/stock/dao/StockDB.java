package com.zhaijiong.stock.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.Point;
import com.zhaijiong.stock.StockSlice;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Pair;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.BoardType;
import com.zhaijiong.stock.model.Stock;
import com.zhaijiong.stock.model.StockMarketType;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.zhaijiong.stock.common.Constants.*;
import static com.zhaijiong.stock.common.Utils.*;
import static org.apache.hadoop.hbase.util.Bytes.toBytes;
import static org.apache.hadoop.hbase.util.Bytes.toDouble;

/**
 * Created by eryk on 15-7-25.
 */
public class StockDB {
    private static final Logger LOG = LoggerFactory.getLogger(StockDB.class);

    Context context;
    HBase hbase;

    public StockDB(Context context) {
        this.context = context;
        this.hbase = new HBase(context);
    }

    /**
     * 存储stock列表,key为symbol，value为股票名称
     * rowkey : md5(symbol,4) + symbol
     *
     * @param stockList
     * @throws java.io.IOException
     */
    public void saveStockSymbols(List<Pair<String, String>> stockList) throws IOException {
        List<Put> puts = Lists.newLinkedList();
        for (Pair<String, String> pair : stockList) {
            Put put = new Put(getRowkeyWithMD5Prefix(pair.getVal()));
            put.add(Constants.TABLE_CF_INFO, Constants.NAME, Bytes.toBytes(pair.getKey()));
            puts.add(put);
        }
        hbase.put(TABLE_STOCK_INFO,puts);
        LOG.info("total stock count:" + stockList.size());
    }

    public List<String> getStockSymbols(){
        Scan scan = new Scan();
        scan.setFilter(new KeyOnlyFilter());
        scan.setCaching(5000);
        List<Result> resultList = hbase.scan(TABLE_STOCK_INFO, scan);
        List<String> symbols = Lists.newLinkedList();
        for(Result result :resultList){
            symbols.add(Bytes.toString(Bytes.tail(result.getRow(),6)));
        }
        return symbols;
    }

    public List<String> getStockSymbols(final StockMarketType type){
        List<String> symbols = getStockSymbols();
        Collection<String> filter = Collections2.filter(symbols, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return type.isMatchType(input);
            }
        });
        return Lists.newArrayList(filter);
    }

    public List<String> getStockSymbols(final BoardType type){
        List<String> symbols = getStockSymbols();
        Collection<String> filter = Collections2.filter(symbols, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return type.isMatchType(input);
            }
        });
        return Lists.newArrayList(filter);
    }

    public void saveStockAvgCost(String symbol,String date,double price){
        List<Put> puts = Lists.newLinkedList();
        Put put = new Put(getRowkeyWithMd5PrefixAndDateSuffix(symbol,date));
        put.add(TABLE_CF_DATA,AVG_COST,Bytes.toBytes(price));
        puts.add(put);
        hbase.put(TABLE_STOCK_DAILY,puts);
    }

    /**
     * save all kind of stock data include:5min,15min,30min,60min,daily,week,month
     * @param tableName
     * @param stocks
     */
    public void saveStockDate(String tableName, List<Stock> stocks) {
        List<Put> puts = getPuts(stocks);
        hbase.put(tableName, puts);
    }

    /**
     * save 5 min stock data
     *
     * @param stocks
     */
    public void saveStock5MinData(List<Stock> stocks) {
        saveStockDate(TABLE_STOCK_5_MINUTES, stocks);
    }

    /**
     * save 15 min stock data
     *
     * @param stocks
     */
    public void saveStock15MinData(List<Stock> stocks) {
        saveStockDate(TABLE_STOCK_15_MINUTES, stocks);
    }

    /**
     * save 30 min stock data
     *
     * @param stocks
     */
    public void saveStock30MinData(List<Stock> stocks) {
        saveStockDate(TABLE_STOCK_30_MINUTES, stocks);
    }

    /**
     * save 60 min stock data
     *
     * @param stocks
     */
    public void saveStock60MinData(List<Stock> stocks) {
        saveStockDate(TABLE_STOCK_60_MINUTES, stocks);
    }

    /**
     * rowkey : md5(symbol,4) + symbol + yyyyMMddHHmm
     *
     * @param stocks
     * @throws java.io.IOException
     */
    public void saveStockDailyData(List<Stock> stocks) throws IOException {
        saveStockDate(TABLE_STOCK_DAILY, stocks);
    }

    public void saveStockWeekData(List<Stock> stocks) throws IOException {
        saveStockDate(TABLE_STOCK_WEEK, stocks);
    }

    public void saveStockMonthData(List<Stock> stocks) throws IOException {
        saveStockDate(TABLE_STOCK_MONTH, stocks);
    }

    public List<Stock> getStockData5Min(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_5_MINUTES, symbol, startDate, stopDate);
    }

    public List<Stock> getStockData15Min(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_15_MINUTES, symbol, startDate, stopDate);
    }

    public List<Stock> getStockData30Min(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_30_MINUTES, symbol, startDate, stopDate);
    }

    public List<Stock> getStockData60Min(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_60_MINUTES, symbol, startDate, stopDate);
    }

    public List<Stock> getStockDataDaily(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_DAILY, symbol, startDate, stopDate);
    }

    public List<Stock> getStockDataWeek(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_WEEK, symbol, startDate, stopDate);
    }

    public List<Stock> getStockDataMonth(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_MONTH, symbol, startDate, stopDate);
    }

    public List<Stock> getStockData(String tableName, String symbol, String startDate, String stopDate) {
        List<Stock> stocks = Lists.newLinkedList();
        HTableInterface table = context.getTable(tableName);
        Scan scan = new Scan();
        scan.setStartRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate));
        scan.setStopRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate));
        scan.setCaching(2000);
        if (LOG.isDebugEnabled()) {
            LOG.debug("startRow:" + getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate));
            LOG.debug("stopRow:" + getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate));
        }
        try {
            ResultScanner scanner = table.getScanner(scan);
            fillStockList(stocks, scanner);
            scanner.close();
        } catch (IOException e) {
            LOG.error(String.format("fail to get %s stock history data from %s,start:%s - stop:%s",
                    symbol,
                    tableName,
                    startDate,
                    stopDate));
        }
        context.closeTable(table);
        return stocks;
    }

    private void fillStockList(List<Stock> stocks, ResultScanner scanner) {
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
    }

    public StockSlice getStockSliceDaily(String symbol, String startDate, String stopDate) throws IOException {
        List<Stock> stockHistory = getStockDataDaily(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    public StockSlice getStockSlice5Min(String symbol, String startDate, String stopDate) throws IOException {
        List<Stock> stockHistory = getStockData5Min(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    public StockSlice getStockSlice15Min(String symbol, String startDate, String stopDate) throws IOException {
        List<Stock> stockHistory = getStockData15Min(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    public StockSlice getStockSlice30Min(String symbol, String startDate, String stopDate) throws IOException {
        List<Stock> stockHistory = getStockData30Min(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    public StockSlice getStockSlice60Min(String symbol, String startDate, String stopDate) throws IOException {
        List<Stock> stockHistory = getStockData60Min(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
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
        scan.addColumn(TABLE_CF_DATA, toBytes(metric));
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            KeyValue keyValue = result.getColumnLatest(TABLE_CF_DATA, toBytes(metric));
            byte[] key = keyValue.getKey();
            Date date = Utils.getStockDate(key);
            double val = toDouble(keyValue.getValue());

        }
        return points;
    }

    private List<Put> getPuts(List<Stock> stocks) {
        List<Put> puts = Lists.newLinkedList();
        for (Stock stock : stocks) {
            byte[] rowkey = Utils.getRowkeyWithMd5PrefixAndDaySuffix(stock);
            Put put = new Put(rowkey);
            addColumn(stock, put);
            puts.add(put);
        }
        return puts;
    }

    private void addColumn(Stock stock, Put put) {
        if (isNotNullorZero(stock.close)) {
            put.add(TABLE_CF_DATA, CLOSE, toBytes(stock.close));
        }
        if (isNotNullorZero(stock.high)) {
            put.add(TABLE_CF_DATA, HIGH, toBytes(stock.high));
        }
        if (isNotNullorZero(stock.low)) {
            put.add(TABLE_CF_DATA, LOW, toBytes(stock.low));
        }
        if (isNotNullorZero(stock.open)) {
            put.add(TABLE_CF_DATA, OPEN, toBytes(stock.open));
        }
        if (isNotNullorZero(stock.lastClose)) {
            put.add(TABLE_CF_DATA, LAST_CLOSE, toBytes(stock.lastClose));
        }
        if (isNotNullorZero(stock.changeAmount)) {
            put.add(TABLE_CF_DATA, CHANGE_AMOUNT, toBytes(stock.changeAmount));
        }
        if (isNotNullorZero(stock.change)) {
            put.add(TABLE_CF_DATA, CHANGE, toBytes(stock.change));
        }
        if (isNotNullorZero(stock.turnoverRate)) {
            put.add(TABLE_CF_DATA, TURNOVER_RATE, toBytes(stock.turnoverRate));
        }
        if (isNotNullorZero(stock.volume)) {
            put.add(TABLE_CF_DATA, VOLUME, toBytes(stock.volume));
        }
        if (isNotNullorZero(stock.amount)) {
            put.add(TABLE_CF_DATA, AMOUNT, toBytes(stock.amount));
        }
        if (isNotNullorZero(stock.totalValue)) {
            put.add(TABLE_CF_DATA, TOTAL_VALUE, toBytes(stock.totalValue));
        }
        if (isNotNullorZero(stock.marketValue)) {
            put.add(TABLE_CF_DATA, MARKET_VALUE, toBytes(stock.marketValue));
        }
        if (isNotNullorZero(stock.amplitude)) {
            put.add(TABLE_CF_DATA, AMPLITUDE, toBytes(stock.amplitude));
        }
    }

}
