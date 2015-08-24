package com.zhaijiong.stock.dao;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.collect.FinanceDataCollecter;
import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.convert.FinanceDataConverter;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.tools.HistoryDailyDataInit;
import com.zhaijiong.stock.tools.StockMap;
import org.apache.hadoop.hbase.client.Put;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StockDBTest {
    String table = "stocks_day";
    Context context;


    @Before
    public void setUp() throws Exception {
        context = new Context();
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void testSetAll() throws Exception {

        String start = "19901219";
        String stop = "20150809";

//        StockListFetcher stockListFetcher = new StockListFetcher();
//        List<Pair<String, String>> stockList = stockListFetcher.getStockList();
//        for (Pair<String, String> stock : stockList) {
//            DailyStockDataCollecter collecter = new DailyStockDataCollecter(start, stop);
//            List<StockData> stocks = collecter.collect(stock.getVal());
//
//            StockDB stockDB = new StockDB(context);
//            stockDB.saveStockDailyData(stocks);
//        }
    }

    @Test
    public void testSaveSingleStock() throws IOException {
        String start = Constants.MARKET_START_DATE;
//        String start = "20150810";
        String stop = "20150813";
        String symbol = "600376";

        HistoryDailyDataInit collecter = new HistoryDailyDataInit();
        List<StockData> stocks = collecter.collect(symbol,start, stop);

        StockDB stockDB = new StockDB(context);
        stockDB.saveStockDailyData(stocks);
    }

    @Test
    public void testSaveSingleStockMinData(){
        String start = "19901219";
        String stop = "20150809";
        MinuteDataCollecter collecter =new MinuteDataCollecter(start,stop,"5");
//        List<StockData> stocks = collecter.collect("601886");
//        StockDB stockDB = new StockDB(context);
//        stockDB.saveStock5MinData(stocks);
    }

    @Test
    public void testGet() throws IOException {
        String start = DateRange.getRange(3).start();
        String stop = DateRange.getRange(3).stop();
        String symbol = "600376";

        StockDB stockDB = new StockDB(context);
        List<StockData> stocks = stockDB.getStockDataDaily(symbol, start, stop);
        for (StockData stock : stocks) {
            for(Map.Entry<String,Double> entry:stock.entrySet()){
                System.out.println(entry.getKey()+":"+entry.getValue());
            }
        }
    }

    @Test
    public void testGetMinuteData() throws IOException {
        String start = "20150824";
        String stop = "20150825";
        String symbol = "600376";

        StockDB stockDB = new StockDB(context);
        List<StockData> stocks = stockDB.getStockData15Min(symbol, start, stop);
        for (StockData stock : stocks) {
            System.out.println(stock.date + stock.toString());
        }
    }

    @Test
    public void testSaveStockList() throws IOException {
        StockDB stockDB = new StockDB(context);
        stockDB.saveStockSymbols(StockMap.getMap());
        context.close();
    }

    @Test
    public void testGetStockList(){
        Stopwatch stopwatch = Stopwatch.createStarted();
        StockDB stockDB = new StockDB(context);
//        List<String> stockList = stockDB.getStockSymbols();
//        List<String> stockList = stockDB.getStockSymbols(StockMarketType.SZ);
        List<String> stockList = stockDB.getTradingStockSymbols();
        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        for(String symbol:stockList){
            System.out.println(symbol);
        }
        System.out.println("size="+stockList.size());
    }

    @Test
    public void testSaveFinanceData(){
        Context context = new Context();
        StockDB stockDB = new StockDB(context);
//        List<String> stockSymbols = stockDB.getStockSymbols();
        List<String> stockSymbols = Lists.newArrayList("600376");
        FinanceDataCollecter collecter= new FinanceDataCollecter();
        for(String symbol:stockSymbols){
            Map<String, Map<String, String>> reports = collecter.collect(symbol);
            System.out.println("report:"+reports.size());
            FinanceDataConverter converter = new FinanceDataConverter(symbol);
            List<Put> puts = converter.toPut(reports);
            stockDB.save(Constants.TABLE_ARTICLE,puts);
        }
    }
}