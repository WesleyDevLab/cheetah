package com.zhaijiong.stock;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.collect.Collecter;
import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.collect.MoneyFlowDataCollecter;
import com.zhaijiong.stock.collect.RealtimeDataCollecter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.convert.MoneyFlowDataConverter;
import com.zhaijiong.stock.convert.RealTimeDataConverter;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.indicators.Indicators;
import com.zhaijiong.stock.model.StockData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-24.
 */
public class RealTimeRecommend {
    private ExecutorService threadPool;
    private CountDownLatch counter;

    private Context context;
    private StockDB stockDB;
    private Indicators indicators;

    public RealTimeRecommend(){
        context = new Context();
        stockDB = new StockDB(context);
        threadPool = Executors.newFixedThreadPool(context.getInt(Constants.DATABASE_POOL_SIZE,1));
        indicators = new Indicators();

    }

    public class Analyzer implements Runnable{

        private String symbol;

        public Analyzer(String symbol){
            this.symbol = symbol;
        }

        @Override
        public void run() {
            counter.countDown();

//            DateRange dateRange = DateRange.getRange(8);
            DateRange dateRange = DateRange.getRange(60);

            MinuteDataCollecter collect = new MinuteDataCollecter(dateRange.start(), dateRange.stop(), "30");
            RealtimeDataCollecter realtimeDataCollecter = new RealtimeDataCollecter();
            Map<String, Map<String,String>> values = collect.collect(symbol);
            if(values.size()==0){
                return;
            }
            double[] closes = new double[values.size()];
            double[] volumes = new double[values.size()];
            int i=0;

//            List<StockData> stockDataDaily = stockDB.getStockDataDaily(symbol, dateRange.start(), dateRange.stop());
//            System.out.println("symbol:"+symbol+",size"+stockDataDaily.size());
//            for(StockData stockData:stockDataDaily){
//                closes[i] = stockData.get("close");
//                volumes[i] = stockData.get("volume");
//            }
//            StockData stockData = stockDataDaily.get(stockDataDaily.size()-1);
//            i=0;
            Map<String,String> lastStatus = Maps.newHashMap();
            for(Map.Entry<String,Map<String,String>> entry:values.entrySet()){
                closes[i]=Double.parseDouble(entry.getValue().get("close"));
                volumes[i]=Double.parseDouble(entry.getValue().get("volume"));
                lastStatus = entry.getValue();
                i++;
            }
            double close = Double.parseDouble(lastStatus.get("close"));
//            double close = stockData.get("close");
//            double open = Double.parseDouble(lastStatus.get("open"));
//            double high = Double.parseDouble(lastStatus.get("high"));
//            double low = Double.parseDouble(lastStatus.get("low"));

            double ma5 = Utils.formatDouble(indicators.sma(closes, 5)[values.size()-1],"#.##");
            double ma10 = Utils.formatDouble(indicators.sma(closes, 10)[values.size()-1],"#.##");
            double ma20 = Utils.formatDouble(indicators.sma(closes, 20)[values.size()-1],"#.##");

            if(close>20){
                return;
            }
            if(close<=ma5){
                return;
            }
            if(ma5<ma10){
//                return;
            }
            if(ma10<ma20){
//                return;
            }
            if(ma5<ma20){
//                return;
            }

            RealTimeDataConverter realTimeDataConverter = new RealTimeDataConverter();
            Map<String, Double> realTimeData = realTimeDataConverter.toMap(realtimeDataCollecter.collect(symbol));
            if(realTimeData.get("PE")>200 || realTimeData.get("PE")<0){
                return;
            }
            //流通市值大于200亿
            if(realTimeData.get("circulationMarketValue")>20000000000d){
                return;
            }

            if(realTimeData.get("avgCost")<realTimeData.get("close")){
                return;
            }

            MoneyFlowDataCollecter collecter = new MoneyFlowDataCollecter();
            MoneyFlowDataConverter moneyFlowDataConverter = new MoneyFlowDataConverter(symbol);
            Map<String, Double> moneyFlowData = moneyFlowDataConverter.toMap(collecter.collect(symbol));


            double[][] macd = indicators.macd(closes);
            double dif = macd[0][values.size()-1];
            double dea = macd[1][values.size()-1];
            double macdRtn = (dif - dea) * 2;

            double difOld = macd[0][values.size()-2];
            double deaOld = macd[1][values.size()-2];
            double macdRtnOld = (difOld - deaOld) * 2;
            if(deaOld>difOld && dea<dif && macdRtn>0 && macdRtn > macdRtnOld){
//                System.out.println(symbol+":"+stockData);
                System.out.println(symbol+":"+lastStatus);
            }
        }
    }

    public void alert(String symbol){
        System.out.println(symbol);
    }

    public void run(){
        Stopwatch stopwatch = Stopwatch.createStarted();
        StockDB stockDB = new StockDB(context);
        List<String> symbols = stockDB.getTradingStockSymbols();
        counter = new CountDownLatch(symbols.size());
        for(String symbol :symbols){
            threadPool.submit(new Analyzer(symbol));
        }
        try {
            counter.await();
            threadPool.shutdown();

            System.out.println("shutdown now");
            while(!threadPool.isTerminated()){
                System.out.println("shutdown...");
                threadPool.shutdownNow();
                Thread.sleep(1000);
            }
            System.out.println("cost:"+stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        RealTimeRecommend recommend = new RealTimeRecommend();
        recommend.run();
    }
}
