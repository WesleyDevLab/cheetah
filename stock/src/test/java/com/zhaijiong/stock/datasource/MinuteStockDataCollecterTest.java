package com.zhaijiong.stock.datasource;

import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.indicators.Indicators;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MinuteStockDataCollecterTest {

    MinuteDataCollecter collecter;

    @Before
    public void setUp() throws Exception {
        String startDate = "20150808";
        String stopDate = "20150811";
        collecter = new MinuteDataCollecter(startDate,stopDate,"15");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCollect() throws Exception {
        Context context =new Context();
        StockDB stockDB = new StockDB(context);
        Indicators indicators = new Indicators();
//        StockListFetcher stockListFetcher = new StockListFetcher();
//        List<Pair<String, String>> stockList = stockListFetcher.getStockList();
//        for(Pair<String,String> pair:stockList){
//            List<StockData> stocks = collecter.collect(pair.getVal());
//        }

    }
}