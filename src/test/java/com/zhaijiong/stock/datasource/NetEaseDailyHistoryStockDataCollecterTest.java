package com.zhaijiong.stock.datasource;

import com.zhaijiong.stock.Pair;
import com.zhaijiong.stock.Stock;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NetEaseDailyHistoryStockDataCollecterTest {

    @Test
    public void testCollect() throws Exception {
        String start = "19901219";
        String stop = "20150805";
        String symbol = "0601886";


            NetEaseDailyHistoryStockDataCollecter collecter = new NetEaseDailyHistoryStockDataCollecter();
            List<Stock> collect = collecter.collect(symbol, start, stop);
            for(Stock stock:collect){
                System.out.println(stock);
            }

    }
}