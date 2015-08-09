package com.zhaijiong.stock.indicators;

import com.zhaijiong.stock.Context;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.StockSlice;
import com.zhaijiong.stock.dao.StockDB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IndicatorsTest {
    Indicators indicators;
    Context context;
    StockDB stockDB;


    @Before
    public void setUp() throws Exception {
        indicators = new Indicators();
        context =new Context();
        stockDB = new StockDB(context);
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void testSma() throws Exception {
        String startDate = "20150101";
        String stopDate = "20150808";
        String symbol = "601886";

        List<Stock> stockHistory = stockDB.getStockHistory(symbol, startDate, stopDate);
        System.out.println("stockHis:"+stockHistory.size());
        StockSlice stockSlice = StockSlice.getSlice(stockHistory,startDate,stopDate);
        double[] closes = stockSlice.getValues("close");
        indicators.sma(closes,5);

        double[] ma5 = indicators.sma(closes,5 );
        double[] ma10 = indicators.sma(closes,10);
        double[] ma20 = indicators.sma(closes,20);
        double[] ma30 = indicators.sma(closes,30);
        double[] ma60 = indicators.sma(closes,60);
        double[] ma120 = indicators.sma(closes,120);

        System.out.println("ma5:"+ma5[closes.length-1]);
        System.out.println("ma10:"+ma10[closes.length-1]);
        System.out.println("ma20:"+ma20[closes.length-1]);
        System.out.println("ma30:"+ma30[closes.length-1]);
        System.out.println("ma60:"+ma60[closes.length-1]);
        System.out.println("ma120:"+ma120[closes.length-1]);
    }

    @Test
    public void testMacd() throws Exception {

    }

    @Test
    public void testBbands() throws Exception {

    }
}