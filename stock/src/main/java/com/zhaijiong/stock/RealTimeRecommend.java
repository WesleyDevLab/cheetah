package com.zhaijiong.stock;

import com.google.common.collect.Maps;
import com.zhaijiong.stock.collect.Collecter;
import com.zhaijiong.stock.collect.MinuteDataCollecter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.indicators.Indicators;

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
    private Indicators indicators;

    public RealTimeRecommend(){
        context = new Context();
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
            boolean isRecommend = false;

            DateRange dateRange = DateRange.getRange(8);

            Collecter collect = new MinuteDataCollecter(dateRange.start(), dateRange.stop(), "15");

            Map<String, Map<String,String>> values = collect.collect(symbol);
            double[] closes = new double[values.size()];
            double[] volumes = new double[values.size()];
            int i=0;
            Map<String,String> lastStatus = Maps.newHashMap();
            for(Map.Entry<String,Map<String,String>> entry:values.entrySet()){
                closes[i]=Double.parseDouble(entry.getValue().get("close"));
                volumes[i]=Double.parseDouble(entry.getValue().get("volume"));
                lastStatus = entry.getValue();
                i++;
            }
            double close = Double.parseDouble(lastStatus.get("close"));
            double open = Double.parseDouble(lastStatus.get("open"));
            double high = Double.parseDouble(lastStatus.get("high"));
            double low = Double.parseDouble(lastStatus.get("low"));

            double ma5 = Utils.formatDouble(indicators.sma(closes, 5)[values.size()-1],"#.##");
            double ma10 = Utils.formatDouble(indicators.sma(closes, 10)[values.size()-1],"#.##");
            double ma20 = Utils.formatDouble(indicators.sma(closes, 20)[values.size()-1],"#.##");

            counter.countDown();
            if(close<=ma5){
                return;
            }
            if(ma5<ma10){
                return;
            }
            if(ma10<ma20){
                return;
            }
            if(ma5<ma20){
                return;
            }


            double[][] macd = indicators.macd(closes);
            double dif = macd[0][values.size()-1];
            double dea = macd[1][values.size()-1];
            double macdRtn = (dif - dea) * 2;

            if(macdRtn>0 && dif>dea){
                System.out.println(symbol+":"+lastStatus);
            }
        }
    }

    public void run(){
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RealTimeRecommend recommend = new RealTimeRecommend();
        recommend.run();
    }
}
