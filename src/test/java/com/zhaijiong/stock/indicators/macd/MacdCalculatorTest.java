package com.zhaijiong.stock.indicators.macd;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.datasource.Collecter;
import com.zhaijiong.stock.datasource.DailyStockDataCollecter;
import com.zhaijiong.stock.indicators.Indicators;
import com.zhaijiong.stock.indicators.TALIBWraper;
import org.junit.Test;

import java.util.List;

public class MacdCalculatorTest {
    Core lib = new Core();
    String start = "19901219";
    String stop = "20150807";


    private MInteger outBegIdx = new MInteger();
    private MInteger outNbElement= new MInteger();

//    @Test
    public void test_MACD()
    {
//        StockDataCollecter collecter= new YahooDailyHistoryStockDataCollector();
//        List<Stock> stocks = collecter.collect("601886");
//        List<Double> closes = Lists.newLinkedList();
//        for(Stock stock : stocks){
//            System.out.println(Utils.formatDate(stock.getStockByDate()) +":"+stock.getAdjClose() );
//            closes.add(stock.getAdjClose());
//        }
//        Collections.reverse(closes);


//        double macd[]   = new double[close.length];
//        double signal[] = new double[close.length];
//        double hist[]   = new double[close.length];
//        int lookback = lib.macdLookback(12,26,9);
//        RetCode retCode = lib.macd(0,close.length-1,close,12,26,9,outBegIdx,outNbElement,macd,signal,hist);
//
//        double ema15[] = new double[close.length];
//        lookback = lib.emaLookback(12);
//        retCode = lib.ema(0,close.length-1,close,12,outBegIdx,outNbElement,ema15);
//
//        double ema26[] = new double[close.length];
//        lookback = lib.emaLookback(26);
//        retCode = lib.ema(0,close.length-1,close,26,outBegIdx,outNbElement,ema26);
//
//        System.out.println(macd[close.length-1]);
//        System.out.println(macd[0]);
    }

//    @Test
//    public void testGetMACD() throws Exception {
//        StockDataCollecter collecter= new YahooDailyHistoryStockDataCollector();
//        List<Stock> stocks = collecter.collect("601616");
//        List<Double> prices = Lists.newLinkedList();
//        for(Stock stock : stocks){
//            System.out.println(Utils.formatDate(stock.getStockByDate()) +":"+stock.getAdjClose() );
////            prices.add(new BigDecimal(stock.getAdjClose()));
//            prices.add(stock.getAdjClose());
//        }
//        Collections.reverse(prices);
//        HashMap<String, Double> macd = MacdCalculator.getMACD(prices, 12, 26, 9);
//
//
//        System.out.println(macd.get("DIF"));
//        System.out.println(macd.get("DEA"));
//        System.out.println(macd.get("MACD"));

//        -0.7644851870721314
//                -0.12844094954045596
//                -1.2720884750633508
//    }

    @Test
    public void test_Macd(){
        start = "20150312";
        stop = "20150812";
        Collecter collecter= new DailyStockDataCollecter(start,stop);
        List<Stock> stocks = collecter.collect("601886");
        for(Stock stock:stocks){
            System.out.println(stock);
        }
        double[] prices = new double[stocks.size()];
        int size = stocks.size();
        for(int i =0;i<size;i++){
            Stock stock = stocks.get(i);
            System.out.println(Utils.formatDate(stock.date) +":"+stock.close );
            prices[size-1 - i] = stock.close;
        }

        TALIBWraper talib = new TALIBWraper();
        double[][] macd = talib.getMacdExt(prices, 12, 26, 9);

        double dif = macd[0][stocks.size() - 1];
        double dea = macd[1][stocks.size() - 1];
        double macdRtn = (dif - dea) * 2;
        System.out.println("DIF=" + dif);
        System.out.println("DEA=" + dea);
        System.out.println("MACD=" + macdRtn);
    }

    @Test
    public void test_ma(){
        Collecter collecter= new DailyStockDataCollecter(start,stop);
        List<Stock> stocks = collecter.collect("601886");
        double[] prices = new double[stocks.size()];
        int size = stocks.size();
        System.out.println(size);
        for(int i =0;i<size;i++){
            Stock stock = stocks.get(i);
            prices[size-1 - i] = stock.close;
        }
        Indicators indicators = new Indicators();
        double[] ma5 = indicators.sma(prices,5 );
        double[] ma10 = indicators.sma(prices,10);
        double[] ma20 = indicators.sma(prices,20);
        double[] ma30 = indicators.sma(prices,30);
        double[] ma60 = indicators.sma(prices,60);
        double[] ma120 = indicators.sma(prices,120);

        for(int i=0;i<ma5.length;i++){
            System.out.println(Utils.formatDate(stocks.get(stocks.size()-1-i).date));
            System.out.println(ma5[i]);
            System.out.println(ma10[i]);
            System.out.println(ma20[i]);
            System.out.println(ma30[i]);
            System.out.println(ma60[i]);
            System.out.println(ma120[i]);
        }

    }

    @Test
    public void test_boll() {
        Collecter collecter = new DailyStockDataCollecter(start, stop);
        List<Stock> stocks = collecter.collect("601886");
        double[] prices = new double[stocks.size()];
        int size = stocks.size();
        System.out.println(size);
        for (int i = 0; i < size; i++) {
            Stock stock = stocks.get(i);
            prices[size - 1 - i] = stock.close;
        }
        System.out.println("prices:"+prices.length);
        Indicators indicators = new Indicators();

        double[][] bbands = indicators.bbands(prices);
        System.out.println("boll length:"+bbands.length);
        System.out.println("boll[0]:"+bbands[0].length);
        System.out.println(bbands[0][bbands.length-1]);
        System.out.println(bbands[1][bbands.length-1]);
        System.out.println(bbands[2][bbands.length-1]);
    }
}