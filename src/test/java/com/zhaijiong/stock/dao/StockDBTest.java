package com.zhaijiong.stock.dao;

import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.Pair;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.datasource.NetEaseDailyHistoryStockDataCollecter;
import com.zhaijiong.stock.datasource.StockListFether;
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
    public void testSet() throws Exception {

        String start = "19901219";
        String stop = "20150804";
        String symbol = "601886";

        StockListFether stockListFether = new StockListFether();
        List<Pair<String, String>> stockList = stockListFether.getStockList();
        for (Pair<String, String> stock : stockList) {
            NetEaseDailyHistoryStockDataCollecter collecter = new NetEaseDailyHistoryStockDataCollecter();
            List<Stock> stocks = collecter.collect(stock.getVal(), start, stop);

            StockDB stockDB = new StockDB(table, context);
            stockDB.save(stocks);
        }
    }

    @Test
    public void testGet() throws IOException {
        String start = "20150806";
        String stop = "20150807";
        String symbol = "600000";

        StockDB stockDB = new StockDB(table, context);
        List<Stock> stocks = stockDB.get(symbol, start, stop);
        for(Stock stock:stocks){
            System.out.println(stock);
        }
    }
}