package com.zhaijiong.stock;

import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.ParallelProcesser;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-23.
 */
public class DataCenter {
    private static Logger LOG = LoggerFactory.getLogger(DataCenter.class);


    private List<String> stockList;

    private Context context;
    /**
     * 实时股票数据
     * key=symbol,例如,600123
     * value=股票实时数据
     */
    private Map<String,StockData> realtimeData;
    private Map<String,List<StockData>> dailyData;

    public DataCenter(Context context){
        this.context = context;
        ParallelProcesser.init(
                context.getInt(Constants.SCHEDULE_POOL_SIZE,8),
                context.getInt(Constants.THREAD_POOL_SIZE,32)
        );
    }

    public void close(){
        ParallelProcesser.close();
    }

    public void init(List<String> stockList){
        this.stockList = stockList;
        realtimeData = Maps.newHashMapWithExpectedSize(stockList.size());
        dailyData = Maps.newHashMapWithExpectedSize(stockList.size());
        ParallelProcesser.process(() -> refreshDailyData());
        ParallelProcesser.schedule(() -> refreshRealTimeData(),0,3);
//        while(!(stockList.size()==dailyData.size()&&stockList.size()==realtimeData.size())){
//            LOG.info("loading...");
//            LOG.info("stockList:" +stockList.size());
//            LOG.info("dailyData:" +dailyData.size());
//            LOG.info("realtimeData:" +realtimeData.size());
//            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void refreshDailyData(){
        for(String symbol:stockList){
            ParallelProcesser.process(() -> dailyData.put(symbol,Provider.dailyData(symbol)));
        }
    }

    public void refreshRealTimeData(){
        LOG.info("refreshRealTimeData");
        for(String symbol:stockList){
            ParallelProcesser.process(() -> realtimeData.put(symbol,Provider.realtimeData(symbol)));
        }
    }

    /**
     * 获取股票列表
     * @return
     */
    public List<String> getStockList(){
        return stockList;
    }
    //获取股票日线级别数据
    public Map<String,List<StockData>> getDailyData(){
        return dailyData;
    }

    public Map<String,StockData> getSymbolDataMap(){
        return realtimeData;
    }

    /**
     * 获取股票实时信息
     * @param symbol
     * @return
     */
    public StockData getSymbolData(String symbol){
        StockData stockData = getRealTimeData(symbol);
        return stockData;
    }

    public List<StockData> getDailyData(String symbol){
        List<StockData> stockDataList = dailyData.get(symbol);
        if(stockDataList==null){
            stockDataList = Provider.dailyData(symbol);
            dailyData.put(symbol,stockDataList);
        }
        return stockDataList;
    }

    public StockData getRealTimeData(String symbol){
        StockData stockData = realtimeData.get(symbol);
        if(stockData==null){
            stockData = Provider.realtimeData(symbol);
            realtimeData.put(symbol,stockData);
        }
        return stockData;
    }

    public static void main(String[] args) throws InterruptedException {
        Context context = new Context();
        DataCenter dataCenter = new DataCenter(context);
        List<String> stockList = Provider.tradingStockList();
        dataCenter.init(stockList);
        TimeUnit.SECONDS.sleep(10);
        while(true){
            System.out.println("dailyData:" +dataCenter.dailyData.size());
            System.out.println("realtimeData:" +dataCenter.realtimeData.size());
            System.out.println("-----------------");
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
