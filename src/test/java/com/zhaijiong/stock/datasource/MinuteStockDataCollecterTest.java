package com.zhaijiong.stock.datasource;

import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.common.Pair;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.indicators.Indicators;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MinuteStockDataCollecterTest {

    MinuteStockDataCollecter collecter;

    @Before
    public void setUp() throws Exception {
        String startDate = "20150808";
        String stopDate = "20150811";
        collecter = new MinuteStockDataCollecter(startDate,stopDate,"15");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCollect() throws Exception {
        Context context =new Context();
        StockDB stockDB = new StockDB(context);
        Indicators indicators = new Indicators();
        StockListFetcher stockListFetcher = new StockListFetcher();
        List<Pair<String, String>> stockList = stockListFetcher.getStockList();
        for(Pair<String,String> pair:stockList){
            List<Stock> stocks = collecter.collect(pair.getVal());

        }

    }
}