package com.zhaijiong.stock.dao;

import com.google.common.base.Stopwatch;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.DailyDataProvider;
import com.zhaijiong.stock.tools.StockList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"classpath:applicationContext.xml"}) //加载配置文件
public class StockDBTest {

    @Autowired
    StockDB stockDB;

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

        DailyDataProvider collecter = new DailyDataProvider();
        List<StockData> stocks = collecter.getFQ(symbol, start, stop);

        StockDB stockDB = new StockDB();
        stockDB.saveStockDailyData(stocks);
    }

    @Test
    public void testSaveSingleStockMinData(){
        String start = "19901219";
        String stop = "20150809";
//        MinuteDataCollecter collecter =new MinuteDataCollecter(start,stop,"5");
//        List<StockData> stocks = collecter.collect("601886");
//        StockDB stockDB = new StockDB(context);
//        stockDB.saveStock5MinData(stocks);
    }

    @Test
    public void testGet() throws IOException {
        String start = DateRange.getRange(3).start();
        String stop = DateRange.getRange(3).stop();
        String symbol = "600376";

        StockDB stockDB = new StockDB();
        List<StockData> stocks = stockDB.getStockDataDaily(symbol, start, stop);
        for (StockData stock : stocks) {
            System.out.println(stock.date);
            for(Map.Entry<String,Double> entry:stock.entrySet()){
                System.out.println(entry.getKey()+":"+entry.getValue());
            }
        }
    }

    @Test
    public void testGetMinuteData() throws IOException {
        String start = "20150824";
        String stop = "20150826";
        String symbol = "600376";

        StockDB stockDB = new StockDB();
        List<StockData> stocks = stockDB.getStockData15Min(symbol, start, stop);
        for (StockData stock : stocks) {
            System.out.println(stock.date + stock.toString());
        }
    }

    @Test
    public void testSaveFinanceData(){
//        Context context = new Context();
//        StockDB stockDB = new StockDB(context);
//        List<String> stockSymbols = stockDB.getStockSymbols();
//        List<String> stockSymbols = Lists.newArrayList("600376");
//        FinanceDataCollecter collecter= new FinanceDataCollecter();
//        for(String symbol:stockSymbols){
//            Map<String, Map<String, String>> reports = collecter.collect(symbol);
//            System.out.println("report:"+reports.size());
//            FinanceDataConverter converter = new FinanceDataConverter(symbol);
//            List<Put> puts = converter.toPut(reports);
//            stockDB.save(Constants.TABLE_ARTICLE,puts);
//        }
    }

    @Test
    public void testGetLatestStockData(){
        StockData stockData = stockDB.getLatestStockData("600133");
        for(Map.Entry<String,Double> entry:stockData.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
}