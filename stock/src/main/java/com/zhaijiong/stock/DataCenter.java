package com.zhaijiong.stock;

import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-23.
 */
public abstract class DataCenter {
    private static Logger LOG = LoggerFactory.getLogger(DataCenter.class);

    private ScheduledExecutorService servicePool;
    private ExecutorService threadPool;

    private List<String> stockList;
    /**
     * 实时股票数据
     * key=symbol,例如,600123
     * value=股票实时数据
     */
    private Map<String,StockData> realtimeData;
    private Map<String,List<StockData>> dailyData;

    public DataCenter(){
        servicePool = Executors.newScheduledThreadPool(8);
        threadPool = Executors.newFixedThreadPool(32);
    }

    public void close(){
        Utils.closeThreadPool(servicePool);
        Utils.closeThreadPool(threadPool);
    }

    public void init(){
        stockList = Provider.tradingStockList();
        realtimeData = Maps.newHashMapWithExpectedSize(stockList.size());
        dailyData = Maps.newHashMapWithExpectedSize(stockList.size());
    }

    public void refresh(){
        for(String symbol:stockList){
            threadPool.execute(() -> dailyData.put(symbol,Provider.dailyData(symbol)));
            threadPool.execute(() -> realtimeData.put(symbol,Provider.realtimeData(symbol)));
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

    private StockData getRealTimeData(String symbol){
        StockData stockData = realtimeData.get(symbol);
        if(stockData==null){
            stockData = Provider.realtimeData(symbol);
            realtimeData.put(symbol,stockData);
        }
        return stockData;
    }
}
