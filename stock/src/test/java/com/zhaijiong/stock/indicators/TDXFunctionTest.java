package com.zhaijiong.stock.indicators;

import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.DailyDataProvider;
import org.junit.Test;

import java.util.List;

public class TDXFunctionTest {
    TDXFunction function = new TDXFunction();
    Indicators indicators = new Indicators();

    @Test
    public void testHhv() throws Exception {
        String start = "20150312";
        String stop = "20150821";
        Context context = new Context();
        StockDB stockDB = new StockDB(context);
        List<StockData> stocks = stockDB.getStockDataDaily("600376",start,stop);

        double[] prices = new double[stocks.size()];
        int size = stocks.size();
        for(int i =0;i<size;i++){
            StockData stock = stocks.get(i);
            prices[i] = stock.get("close");
        }

        double[] hhv = function.hhv(prices, 5);
        for(int i=0;i<prices.length;i++){
            System.out.println("hhv:"+hhv[i]+",close:"+prices[i]);
        }
    }

    @Test
    public void testllv(){
        DateRange range = DateRange.getRange(9);
        List<StockData> stocks = DailyDataProvider.get("600376", range.start(), range.stop());

        double[] prices = new double[stocks.size()];
        int size = stocks.size();
        for(int i =0;i<size;i++){
            StockData stock = stocks.get(i);
            prices[i] = stock.get("close");
        }

        double[] llv = function.llv(prices, 5);
        for(int i=0;i<prices.length;i++){
            System.out.println("llv:"+llv[i]+",close:"+prices[i]);
        }

    }

    @Test
    public void testCross(){
        String start = "20150701";
        String stop = "20150821";
        Context context = new Context();
        StockDB stockDB = new StockDB(context);
        List<StockData> stocks = stockDB.getStockDataDaily("600376",start,stop);

        double[] prices = new double[stocks.size()];
        int size = stocks.size();
        for(int i =0;i<size;i++){
            StockData stock = stocks.get(i);
            prices[i] = stock.get("close");
        }

        double[] ma5 = indicators.sma(prices, 5);
        double[] ma10 = indicators.sma(prices, 10);

        double[] cross = function.cross(ma5, ma10);
        for(int i =0;i<cross.length;i++){
            System.out.println(stocks.get(i).date+":"+stocks.get(i).get("close"));
            System.out.println("ma5:"+ Utils.formatDouble(ma5[i])+",ma10:"+Utils.formatDouble(ma10[i])+",cross:"+Utils.formatDouble(cross[i]));
        }
    }
}