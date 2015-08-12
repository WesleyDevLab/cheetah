package com.zhaijiong.stock.indicators;

import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.datasource.Collecter;
import com.zhaijiong.stock.datasource.DailyStockDataCollecter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TDXFunctionTest {
    TDXFunction function = new TDXFunction();

    @Test
    public void testHhv() throws Exception {
        String start = "20150312";
        String stop = "20150813";
        Collecter collecter= new DailyStockDataCollecter(start,stop);
        List<Stock> stocks = collecter.collect("601886");

        double[] prices = new double[stocks.size()];
        int size = stocks.size();
        for(int i =0;i<size;i++){
            Stock stock = stocks.get(i);
            prices[size-1 - i] = stock.close;
//            System.out.println(stock.close);
        }

        double[] hhv = function.hhv(prices, 30);
        for(int i=0;i<prices.length;i++){
            System.out.println("hhv:"+hhv[i]+",close:"+prices[i]);
        }

    }
}