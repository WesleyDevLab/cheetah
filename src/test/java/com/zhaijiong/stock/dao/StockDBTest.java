package com.zhaijiong.stock.dao;

import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.common.Pair;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.datasource.DailyStockDataCollecter;
import com.zhaijiong.stock.datasource.MinuteStockDataCollecter;
import com.zhaijiong.stock.datasource.StockListFetcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

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

        StockListFetcher stockListFetcher = new StockListFetcher();
        List<Pair<String, String>> stockList = stockListFetcher.getStockList();
        for (Pair<String, String> stock : stockList) {
            DailyStockDataCollecter collecter = new DailyStockDataCollecter(start, stop);
            List<Stock> stocks = collecter.collect(stock.getVal());

            StockDB stockDB = new StockDB(context);
            stockDB.saveStockDailyData(stocks);
        }
    }

    @Test
    public void testSaveSingleStock() throws IOException {
        String start = "20150811";
        String stop = "20150813";
        String symbol = "601886";

        DailyStockDataCollecter collecter = new DailyStockDataCollecter(start, stop);
        List<Stock> stocks = collecter.collect(symbol);

        StockDB stockDB = new StockDB(context);
        stockDB.saveStockDailyData(stocks);
    }

    @Test
    public void testSaveSingleStockMinData(){
        String start = "19901219";
        String stop = "20150809";
        MinuteStockDataCollecter collecter =new MinuteStockDataCollecter(start,stop,"5");
        List<Stock> stocks = collecter.collect("601886");
        StockDB stockDB = new StockDB(context);
        stockDB.saveStock5MinData(stocks);
    }

    @Test
    public void testGet() throws IOException {
        String start = "19901219";
        String stop = "20150809";
        String symbol = "601886";

        StockDB stockDB = new StockDB(context);
        List<Stock> stocks = stockDB.getStockDataDaily(symbol, start, stop);
        for (Stock stock : stocks) {
            System.out.println(stock);
        }
    }

    @Test
    public void testSaveStockList() throws IOException {
        StockDB stockDB = new StockDB(context);
        StockListFetcher stockListFetcher = new StockListFetcher();
        List<Pair<String, String>> stockList = stockListFetcher.getStockList();
        stockDB.saveStockList(stockList);
        context.close();
    }
}