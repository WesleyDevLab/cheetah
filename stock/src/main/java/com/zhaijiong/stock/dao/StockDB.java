package com.zhaijiong.stock.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.model.*;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Utils;
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
import java.util.Map;

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

    public void save(String tableName,List<Put> puts){
        hbase.put(tableName,puts);
    }

    /**
     * 存储stock列表,key为symbol，value为股票名称
     * rowkey : md5(symbol,4) + symbol
     *
     * @param stockMap
     * @throws java.io.IOException
     */
    public void saveStockSymbols(Map<String, String> stockMap) throws IOException {
        List<Put> puts = Lists.newLinkedList();
        for (Map.Entry<String, String> pair : stockMap.entrySet()) {
            Put put = new Put(getRowkeyWithMD5Prefix(pair.getKey()));
            put.add(Constants.TABLE_CF_INFO, Constants.NAME, Bytes.toBytes(pair.getValue()));
            puts.add(put);
        }
        hbase.put(TABLE_STOCK_INFO,puts);
        LOG.info("total stock count:" + stockMap.size());
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

    /**
     * save all kind of stock data include:5min,15min,30min,60min,daily,week,month
     * @param tableName
     * @param stocks
     */
    public void saveStockDate(String tableName, List<StockData> stocks) {
        List<Put> puts = getPuts(stocks);
        hbase.put(tableName, puts);
    }

    /**
     * save 5 min stock data
     *
     * @param stocks
     */
    public void saveStock5MinData(List<StockData> stocks) {
        saveStockDate(TABLE_STOCK_5_MINUTES, stocks);
    }

    /**
     * save 15 min stock data
     *
     * @param stocks
     */
    public void saveStock15MinData(List<StockData> stocks) {
        saveStockDate(TABLE_STOCK_15_MINUTES, stocks);
    }

    /**
     * save 30 min stock data
     *
     * @param stocks
     */
    public void saveStock30MinData(List<StockData> stocks) {
        saveStockDate(TABLE_STOCK_30_MINUTES, stocks);
    }

    /**
     * save 60 min stock data
     *
     * @param stocks
     */
    public void saveStock60MinData(List<StockData> stocks) {
        saveStockDate(TABLE_STOCK_60_MINUTES, stocks);
    }

    /**
     * rowkey : md5(symbol,4) + symbol + yyyyMMddHHmm
     *
     * @param stocks
     * @throws java.io.IOException
     */
    public void saveStockDailyData(List<StockData> stocks){
        saveStockDate(TABLE_STOCK_DAILY, stocks);
    }

    public void saveStockWeekData(List<StockData> stocks) throws IOException {
        saveStockDate(TABLE_STOCK_WEEK, stocks);
    }

    public void saveStockMonthData(List<StockData> stocks) throws IOException {
        saveStockDate(TABLE_STOCK_MONTH, stocks);
    }

    public List<StockData> getStockData5Min(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_5_MINUTES, symbol, startDate, stopDate);
    }

    public List<StockData> getStockData15Min(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_15_MINUTES, symbol, startDate, stopDate);
    }

    public List<StockData> getStockData30Min(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_30_MINUTES, symbol, startDate, stopDate);
    }

    public List<StockData> getStockData60Min(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_60_MINUTES, symbol, startDate, stopDate);
    }

    public List<StockData> getStockDataDaily(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_DAILY, symbol, startDate, stopDate);
    }

    public List<StockData> getStockDataWeek(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_WEEK, symbol, startDate, stopDate);
    }

    public List<StockData> getStockDataMonth(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_MONTH, symbol, startDate, stopDate);
    }


    public List<StockData> getStockData(String tableName, String symbol, String startDate, String stopDate) {
        List<StockData> stocks = Lists.newLinkedList();
        HTableInterface table = context.getTable(tableName);
        Scan scan = new Scan();
        scan.setStartRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate));
        scan.setStopRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate));
        scan.setCaching(2000);
        LOG.info("startRow:" + Bytes.toString(getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate)));
        LOG.info("stopRow:" + Bytes.toString(getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate)));
        try {
            ResultScanner scanner = table.getScanner(scan);
            for(Result result :scanner){
                stocks.add(toStock(result));
            }
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

    public StockData toStock(Result result) {
        String symbol = Utils.getStockSymbol(result.getRow());
        Date date = Utils.getStockDate(result.getRow());
        StockData stockData = new StockData();
        stockData.date = date;
        stockData.symbol= symbol;
        stockData.boardType = BoardType.getType(symbol);
        stockData.stockMarketType = StockMarketType.getType(symbol);
        for(KeyValue kv:result.list()){
            stockData.put(Bytes.toString(kv.getQualifier()),Bytes.toDouble(kv.getValue()));
        }
        return stockData;
    }


    public StockSlice getStockSliceDaily(String symbol, String startDate, String stopDate) throws IOException {
        List<StockData> stockHistory = getStockDataDaily(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    public StockSlice getStockSlice5Min(String symbol, String startDate, String stopDate) throws IOException {
        List<StockData> stockHistory = getStockData5Min(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    public StockSlice getStockSlice15Min(String symbol, String startDate, String stopDate) throws IOException {
        List<StockData> stockHistory = getStockData15Min(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    public StockSlice getStockSlice30Min(String symbol, String startDate, String stopDate) throws IOException {
        List<StockData> stockHistory = getStockData30Min(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    public StockSlice getStockSlice60Min(String symbol, String startDate, String stopDate) throws IOException {
        List<StockData> stockHistory = getStockData60Min(symbol, startDate, stopDate);
        return StockSlice.getSlice(symbol, stockHistory, startDate, stopDate);
    }

    private List<Put> getPuts(List<StockData> stocks) {
        List<Put> puts = Lists.newLinkedList();
        for (StockData stock : stocks) {
            byte[] rowkey = Utils.getRowkeyWithMd5PrefixAndDaySuffix(stock);
            Put put = new Put(rowkey);
            for(Map.Entry<String,Double> entry:stock.entrySet()){
                put.add(TABLE_CF_DATA, Bytes.toBytes(entry.getKey()), toBytes(entry.getValue()));
            }
            puts.add(put);
        }
        return puts;
    }

}
