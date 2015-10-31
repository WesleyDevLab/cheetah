package com.zhaijiong.stock.indicators;

import com.google.common.base.Stopwatch;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.StockSlice;
import com.zhaijiong.stock.provider.Provider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IndicatorsTest {
    Indicators indicators;
    Context context;
    StockDB stockDB;


    @Before
    public void setUp() throws Exception {
        indicators = new Indicators();
        context = new Context();
        stockDB = new StockDB(context);
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void testSma() throws Exception {
        List<StockData> stocks = Provider.dailyData("300217",40);
        for(StockData stock:stocks){
            System.out.println(stock.date+":"+stock);
        }
        double[] closes = new double[stocks.size()];
        for (int i = 0; i < stocks.size(); i++) {
            closes[i] = stocks.get(i).get("close");
            System.out.println(Utils.formatDate(stocks.get(i).date, "yyyyMMdd") + ":" + closes[i]);
        }

        double[] ma5 = indicators.sma(closes, 5);
        double[] ma10 = indicators.sma(closes, 10);
        double[] ma20 = indicators.sma(closes, 20);
        double[] ma30 = indicators.sma(closes, 30);
//        double[] ma60 = indicators.sma(closes, 60);
//        double[] ma120 = indicators.sma(closes, 120);

        System.out.println("ma5:" + ma5[closes.length - 1]);
        System.out.println("ma10:" + ma10[closes.length - 1]);
        System.out.println("ma20:" + ma20[closes.length - 1]);
        System.out.println("ma30:" + ma30[closes.length - 1]);
//        System.out.println("ma60:" + ma60[closes.length - 1]);
//        System.out.println("ma120:" + ma120[closes.length - 1]);
    }

    @Test
    public void testVolumeMA() throws IOException {
        String startDate = "20150201";
        String stopDate = "20150812";
        String symbol = "601886";

        StockSlice stockSlice = stockDB.getStockSliceDaily(symbol, startDate, stopDate);
        double[] volumes = stockSlice.getValues("volume");
        indicators.sma(volumes, 5);

        double[] ma5 = indicators.sma(volumes, 5);
        double[] ma10 = indicators.sma(volumes, 10);
        double[] ma20 = indicators.sma(volumes, 20);
        double[] ma30 = indicators.sma(volumes, 30);
        double[] ma60 = indicators.sma(volumes, 60);
        double[] ma120 = indicators.sma(volumes, 120);

        System.out.println("ma5:" + ma5[volumes.length - 1]);
        System.out.println("ma10:" + ma10[volumes.length - 1]);
        System.out.println("ma20:" + ma20[volumes.length - 1]);
        System.out.println("ma30:" + ma30[volumes.length - 1]);
        System.out.println("ma60:" + ma60[volumes.length - 1]);
        System.out.println("ma120:" + ma120[volumes.length - 1]);
    }

    @Test
    public void testMacd() throws Exception {

        List<StockData> stocks = Provider.dailyData("300217",40);
        for(StockData stock:stocks){
            System.out.println(stock.date+":"+stock);
        }
        double[] closes = new double[stocks.size()];
        for (int i = 0; i < stocks.size(); i++) {
            closes[i] = stocks.get(i).get("close");
            System.out.println(Utils.formatDate(stocks.get(i).date, "yyyyMMdd") + ":" + closes[i]);
        }

        for (double close : closes) {
            System.out.println(close);
        }
        double[][] macd = indicators.macd(closes);
        double dif = macd[0][closes.length - 1];
        double dea = macd[1][closes.length - 1];
        double macdRtn = (dif - dea) * 2;
        System.out.println("DIF=" + dif);
        System.out.println("DEA=" + dea);
        System.out.println("MACD=" + macdRtn);
    }

    @Test
    public void testBbands() throws Exception {
        String startDate = "20150201";
        String stopDate = "20150812";
        String symbol = "601886";
        StockSlice stockSlice = stockDB.getStockSliceDaily(symbol, startDate, stopDate);
//        List<StockDailyData> stocks = stockSlice.getStocks();

        double[] closes = stockSlice.getValues("close");
        for (double close : closes) {
            System.out.println(close);
        }

        Indicators indicators = new Indicators();
        double[][] bbands = indicators.boll(closes);

        System.out.println(bbands[0][closes.length-1]);
        System.out.println(bbands[1][closes.length-1]);
        System.out.println(bbands[2][closes.length-1]);
    }

    @Test
    public void testRsi() throws IOException {

        String symbol = "600270";
        List<StockData> stockSlice = Provider.dailyData(symbol, false);
//        List<StockDailyData> stocks = stockSlice.getStocks();
//        for(Stock stock:stocks){
//            System.out.println(stock);
//        }
        double[] closes = Utils.getArrayFrom(stockSlice,"close");
        for (double close : closes) {
            System.out.println(close);
        }

        Indicators indicators = new Indicators();
        double[] rsis = indicators.rsi(closes, 6);
        for (double rsi : rsis) {
            System.out.println(rsi);
        }
    }

    @Test
    public void testAllTimeCost() throws IOException {
        String startDate = Constants.MARKET_START_DATE;
        String stopDate = "20150813";
        String symbol = "600376";
        StockSlice stockSliceDaily = stockDB.getStockSliceDaily(symbol, startDate, stopDate);

        double[] closes = stockSliceDaily.getClose();
        double[] volumes = stockSliceDaily.getVolumes();

        String date = Utils.formatDate(stockSliceDaily.getStocks().get(stockSliceDaily.getStocks().size()-1).date);
        System.out.println(date);
        Stopwatch stopwatch = Stopwatch.createStarted();
        double[][] macd = indicators.macd(closes);
        double dif = macd[0][closes.length - 1];
        double dea = macd[1][closes.length - 1];
        double macdRtn = (dif - dea) * 2;
        System.out.println("DIF=" + Utils.formatDouble(dif,"#0.00"));
        System.out.println("DEA=" + Utils.formatDouble(dea,"#0.00"));
        System.out.println("MACD=" + Utils.formatDouble(macdRtn, "#0.00"));

        double[][] bbands = indicators.boll(closes);
        System.out.println("upper="+Utils.formatDouble(bbands[0][closes.length - 1], "#0.00"));
        System.out.println("mid="+Utils.formatDouble(bbands[1][closes.length-1],"#0.00"));
        System.out.println("lower"+Utils.formatDouble(bbands[2][closes.length - 1], "#0.00"));

        double[] volumes_5day = indicators.sma(volumes, 5);
        double[] volumes_10day = indicators.sma(volumes, 10);
        double[] volumes_20day = indicators.sma(volumes, 20);
        double[] volumes_30day = indicators.sma(volumes, 30);
        double[] volumes_60day = indicators.sma(volumes, 60);
        double[] volumes_120day = indicators.sma(volumes, 120);

        System.out.println("volume_5day:"+volumes_5day[volumes.length-1]/1000000d);
        System.out.println("volume_10day:"+volumes_10day[volumes.length-1]/1000000d);
        System.out.println("volume_20day:"+volumes_20day[volumes.length-1]/1000000d);
        System.out.println("volume_30day:"+volumes_30day[volumes.length-1]/1000000d);
        System.out.println("volume_60day:"+volumes_60day[volumes.length-1]/1000000d);
        System.out.println("volume_120day:"+volumes_120day[volumes.length-1]/1000000d);

        double[] closes_5day = indicators.sma(closes, 5);
        double[] closes_10day = indicators.sma(closes, 10);
        double[] closes_20day = indicators.sma(closes, 20);
        double[] closes_30day = indicators.sma(closes, 30);
        double[] closes_60day = indicators.sma(closes, 60);
        double[] closes_120day = indicators.sma(closes, 120);

        System.out.println("closes_5day:"+Utils.formatDouble(closes_5day[closes.length-1],"#0.00"));
        System.out.println("closes_10day:"+Utils.formatDouble(closes_10day[closes.length-1],"#0.00"));
        System.out.println("closes_20day:"+Utils.formatDouble(closes_20day[closes.length-1],"#0.00"));
        System.out.println("closes_30day:"+Utils.formatDouble(closes_30day[closes.length-1],"#0.00"));
        System.out.println("closes_60day:"+Utils.formatDouble(closes_60day[closes.length-1],"#0.00"));
        System.out.println("closes_120day:"+Utils.formatDouble(closes_120day[closes.length-1],"#0.00"));

        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testKDJ(){
        List<StockData> stockDataList = Provider.dailyData("600270", false);
        double[] high = Utils.getArrayFrom(stockDataList, "high");
        double[] low = Utils.getArrayFrom(stockDataList, "low");
        double[] close = Utils.getArrayFrom(stockDataList,"close");

//        for(int i =0;i<stockDataList.size();i++){
//            System.out.println(high[i]+" "+low[i]+ " "+close[i] + stockDataList.get(i));
//        }

        double[][] kdj = indicators.kdj(high, low, close);
        for(int i =0;i<stockDataList.size();i++){
            System.out.println(kdj[0][i]+"\t"+kdj[1][i]+"\t"+kdj[2][i]);
        }
    }
}