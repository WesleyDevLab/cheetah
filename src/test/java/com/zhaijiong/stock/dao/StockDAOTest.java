package com.zhaijiong.stock.dao;

import com.zhaijiong.stock.Constants;
import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.Pair;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.datasource.NetEaseDailyHistoryStockDataCollecter;
import com.zhaijiong.stock.datasource.StockListFether;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTablePool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class StockDAOTest {
    private Configuration conf;
    String table = "stocks_day";
    Context context;


    @Before
    public void setUp() throws Exception {
        context = new Context();
        conf = new Configuration();
        conf.set(HConstants.ZOOKEEPER_QUORUM, "112.124.60.26:2181");
    }

    @After
    public void tearDown() throws Exception {

    }

    //    @Test
    public void testSet() throws Exception {

        String start = "19901219";
        String stop = "20150804";
        String symbol = "601886";

        StockListFether stockListFether = new StockListFether();
        List<Pair<String, String>> stockList = stockListFether.getStockList();
        for (Pair<String, String> stock : stockList) {
            NetEaseDailyHistoryStockDataCollecter collecter = new NetEaseDailyHistoryStockDataCollecter();
            List<Stock> stocks = collecter.collect(stock.getVal(), start, stop);

            StockDAO stockDAO = new StockDAO(table, context);
            stockDAO.save(stocks);
        }
    }

    @Test
    public void testGet() throws IOException {
        String start = "20150806";
        String stop = "20150807";
        String symbol = "600000";

        StockDAO stockDAO = new StockDAO(table, context);
        List<Stock> stocks = stockDAO.get(symbol, start, stop);
        for(Stock stock:stocks){
            System.out.println(stock);
        }
    }
}