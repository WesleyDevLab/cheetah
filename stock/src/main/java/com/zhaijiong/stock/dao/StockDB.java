package com.zhaijiong.stock.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.*;
import com.zhaijiong.stock.model.*;
import com.zhaijiong.stock.tools.StockList;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zhaijiong.stock.common.Constants.*;
import static com.zhaijiong.stock.common.Utils.getRowkeyWithMD5Prefix;
import static com.zhaijiong.stock.common.Utils.getRowkeyWithMd5PrefixAndDateSuffix;
import static org.apache.hadoop.hbase.util.Bytes.toBytes;

/**
 * Created by eryk on 15-7-25.
 */
@Component
public class StockDB {
    private static final Logger LOG = LoggerFactory.getLogger(StockDB.class);

    @Autowired
    HBase hbase;

    public StockDB(){}

    public HBase getHbase() {
        return hbase;
    }

    public void setHbase(HBase hbase) {
        this.hbase = hbase;
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
            put.add(Constants.TABLE_CF_INFO, Constants.STATUS, Bytes.toBytes(StockList.getStockStatus(pair.getKey())));
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

    /**
     * save all kind of stock data include:5min,15min,30min,60min,daily,week,month
     * @param tableName
     * @param stocks
     */
    public void saveStockData(String tableName, List<StockData> stocks) {
        List<Put> puts = getPuts(stocks);
        hbase.put(tableName, puts);
    }

    /**
     * save 5 min stock data
     *
     * @param stocks
     */
    public void saveStock5MinData(List<StockData> stocks) {
        saveStockData(TABLE_STOCK_5_MINUTES, stocks);
    }

    /**
     * save 15 min stock data
     *
     * @param stocks
     */
    public void saveStock15MinData(List<StockData> stocks) {
        saveStockData(TABLE_STOCK_15_MINUTES, stocks);
    }

    /**
     * save 30 min stock data
     *
     * @param stocks
     */
    public void saveStock30MinData(List<StockData> stocks) {
        saveStockData(TABLE_STOCK_30_MINUTES, stocks);
    }

    /**
     * save 60 min stock data
     *
     * @param stocks
     */
    public void saveStock60MinData(List<StockData> stocks) {
        saveStockData(TABLE_STOCK_60_MINUTES, stocks);
    }

    /**
     * rowkey : md5(symbol,4) + symbol + yyyyMMddHHmm
     *
     * @param stocks
     * @throws java.io.IOException
     */
    public void saveStockDailyData(List<StockData> stocks){
        saveStockData(TABLE_STOCK_DAILY, stocks);
    }

    public void saveStockWeekData(List<StockData> stocks) throws IOException {
        saveStockData(TABLE_STOCK_WEEK, stocks);
    }

    public void saveStockMonthData(List<StockData> stocks) throws IOException {
        saveStockData(TABLE_STOCK_MONTH, stocks);
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

    public StockData getLatestStockData(String symbol){
        DateRange dateRange = DateRange.getRange(1);
        List<StockData> stockDatas = getStockData(TABLE_STOCK_DAILY, symbol,dateRange.start(),dateRange.stop());
        if(stockDatas.size()>=1){
            return stockDatas.get(stockDatas.size()-1);
        }
        return null;
    }

    public List<StockData> getStockDataWeek(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_WEEK, symbol, startDate, stopDate);
    }

    public List<StockData> getStockDataMonth(String symbol, String startDate, String stopDate) {
        return getStockData(TABLE_STOCK_MONTH, symbol, startDate, stopDate);
    }


    public List<StockData> getStockData(String tableName, String symbol, String startDate, String stopDate) {
        List<StockData> stocks = Lists.newLinkedList();
        HTableInterface table = hbase.getTable(tableName);
        Scan scan = new Scan();
        scan.setStartRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate));
        scan.setStopRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate));
        scan.setCaching(2000);
//        LOG.info("startRow:" + Bytes.toString(getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate)));
//        LOG.info("stopRow:" + Bytes.toString(getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate)));
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
        hbase.closeTable(table);
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

    public void deleteDailyData(List<String> symbols,String date){
        Date _date = Utils.str2Date(date,"yyyyMMdd");
        List<StockData> stockDataList = Lists.newLinkedList();
        for(String symbol:symbols){
            StockData stockData = new StockData(symbol);
            stockData.date = _date;
            stockDataList.add(stockData);
        }
        deleteDailyData(stockDataList);
    }

    public void deleteDailyData(List<StockData> stockDataList){
        List<Delete> deletes = getDeletes(stockDataList);
        hbase.delete(TABLE_STOCK_DAILY,deletes);
    }

    public List<Delete> getDeletes(List<StockData> stockDataList){
        List<Delete> deletes = Lists.newLinkedList();
        for (StockData stock : stockDataList) {
            byte[] rowkey = Utils.getRowkeyWithMd5PrefixAndDaySuffix(stock);
            Delete delete = new Delete(rowkey);
            deletes.add(delete);
        }
        return deletes;
    }

    public void saveTicksData(String symbol,List<Tick> ticks){
        List<Put> puts = getPuts(symbol,ticks);
        hbase.put(TABLE_STOCK_TICK, puts);
    }

    public List<Tick> getTicksData(String symbol,String startDate,String stopDate){
        List<Tick> ticks = Lists.newLinkedList();
        HTableInterface table = hbase.getTable(TABLE_STOCK_TICK);
        Scan scan = new Scan();
        scan.setStartRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate));
        scan.setStopRow(getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate));
        scan.setCaching(2000);
//        LOG.info("startRow:" + Bytes.toString(getRowkeyWithMd5PrefixAndDateSuffix(symbol, startDate)));
//        LOG.info("stopRow:" + Bytes.toString(getRowkeyWithMd5PrefixAndDateSuffix(symbol, stopDate)));
        try {
            ResultScanner scanner = table.getScanner(scan);
            for(Result result :scanner){
                ticks.add(toTick(result));
            }
            scanner.close();
        } catch (IOException e) {
            LOG.error(String.format("fail to get %s stock tick data from %s,start:%s - stop:%s",
                    symbol,
                    TABLE_STOCK_TICK,
                    startDate,
                    stopDate));
        }
        hbase.closeTable(table);
        return ticks;
    }

    public Tick toTick(Result result){
        Date date = Utils.getTickDate(result.getRow());
        Tick tick = new Tick();
        tick.date = date;
        for(KeyValue kv:result.list()){
            if(Bytes.toString(kv.getQualifier()).equals(StockConstants.AMOUNT)){
                tick.amount = Bytes.toDouble(kv.getValue());
            }
            if(Bytes.toString(kv.getQualifier()).equals(StockConstants.CLOSE)){
                tick.price = Bytes.toDouble(kv.getValue());
            }
            if(Bytes.toString(kv.getQualifier()).equals(StockConstants.VOLUME)){
                tick.volume = Bytes.toInt(kv.getValue());
            }
            if(Bytes.toString(kv.getQualifier()).equals(StockConstants.TYPE)){
                if(Bytes.toInt(kv.getValue())>0){
                    tick.type = Tick.Type.BUY;
                }else if(Bytes.toInt(kv.getValue())<0){
                    tick.type = Tick.Type.SELL;
                }else {
                    tick.type = Tick.Type.MID;
                }
            }
        }
        return tick;
    }

    public List<Put> getPuts(String symbol,List<Tick> ticks){
        List<Put> puts = Lists.newLinkedList();
        for (Tick tick : ticks) {
            byte[] rowkey = Utils.getTickRowkey(symbol, tick);
            Put put = new Put(rowkey);
            put.add(TABLE_CF_DATA, Bytes.toBytes(StockConstants.AMOUNT), toBytes(tick.amount));
            put.add(TABLE_CF_DATA, Bytes.toBytes(StockConstants.CLOSE), toBytes(tick.price));
            put.add(TABLE_CF_DATA, Bytes.toBytes(StockConstants.VOLUME), toBytes(tick.volume));
            put.add(TABLE_CF_DATA, Bytes.toBytes(StockConstants.TYPE), toBytes(tick.type.getType()));
            puts.add(put);
        }
        return puts;
    }
}
