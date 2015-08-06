//package com.zhaijiong.stock.datasource;
//
//import com.zhaijiong.stock.Stock;
//
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class YahooDailyHistoryStockDataCollectorTest {
//
//    @org.junit.Test
//    public void testCollect() throws Exception {
//        YahooDailyHistoryStockDataCollector collector = new YahooDailyHistoryStockDataCollector();
//        List<Stock> history = collector.collect("601886");
//        for(Stock stock :history){
//            System.out.println(stock);
//        }
//    }
//}