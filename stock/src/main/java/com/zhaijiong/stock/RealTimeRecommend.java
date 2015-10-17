package com.zhaijiong.stock;

import com.google.common.base.Stopwatch;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.indicators.Indicators;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.DailyDataProvider;
import com.zhaijiong.stock.provider.MinuteDataProvider;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.provider.RealTimeDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-24.
 */
public class RealTimeRecommend {
    private static Logger LOG = LoggerFactory.getLogger(RealTimeRecommend.class);

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

            DateRange dateRange = DateRange.getRange(10);

//            List<StockData> values = DailyDataProvider.get(symbol, dateRange.start(), dateRange.stop());
            List<StockData> values = MinuteDataProvider.get(symbol, dateRange.start(), dateRange.stop(),"15");
            if(values.size()==0){
                return;
            }
            double[] closes = new double[values.size()];
            double[] volumes = new double[values.size()];
            int i=0;

            StockData lastStatus = new StockData();
            for(StockData stockData:values){
                closes[i]=stockData.get("close");
                volumes[i]=stockData.get("volume");
                lastStatus = stockData;
                i++;
            }
            double close = lastStatus.get("close");
//            double close = stockData.get("close");
//            double open = Double.str2Double(lastStatus.get("open"));
//            double high = Double.str2Double(lastStatus.get("high"));
//            double low = Double.str2Double(lastStatus.get("low"));

            double ma5 = Utils.formatDouble(indicators.sma(closes, 5)[values.size()-1],"#.##");
            double ma10 = Utils.formatDouble(indicators.sma(closes, 10)[values.size()-1],"#.##");
            double ma20 = Utils.formatDouble(indicators.sma(closes, 20)[values.size()-1],"#.##");

            if(close>20){
                return;
            }
            if(close<=ma5){
//                return;
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

            StockData realTimeData = RealTimeDataProvider.get(symbol);
            if(realTimeData.get("PE")>200 || realTimeData.get("PE")<0){
//                return;
            }
            //流通市值大于200亿
            if(realTimeData.get("circulationMarketValue")>20000000000d){
                return;
            }

            if(realTimeData.get("avgCost")<realTimeData.get("close")){
//                return;
            }

            double[][] macd = indicators.macd(closes);
            double dif = macd[0][values.size()-1];
            double dea = macd[1][values.size()-1];
            double macdRtn = (dif - dea) * 2;

            double difOld = macd[0][values.size()-2];
            double deaOld = macd[1][values.size()-2];
            double macdRtnOld = (difOld - deaOld) * 2;
            if(/*deaOld>difOld &&*/ dea<dif ){ //&& macdRtn>0 && macdRtn > macdRtnOld
//                System.out.println(symbol+":"+stockData);
                System.out.println(symbol+":"+lastStatus);
            }
//            System.out.println(symbol+":"+lastStatus);
        }
    }

    public void alert(String symbol){
        System.out.println(symbol);
    }

    public void run(){
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> symbols = Provider.tradingStockList();
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
