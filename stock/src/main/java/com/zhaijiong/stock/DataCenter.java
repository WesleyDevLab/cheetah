package com.zhaijiong.stock;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.*;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Tick;
import com.zhaijiong.stock.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
//TODO 线程安全改造
@SuppressWarnings("not thread safe")
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
    /**
     * 日线级别数据
     * key=symbol
     * value=每天一个StockData
     */
    private Map<String,List<StockData>> dailyData;

    private Map<String,List<StockData>> minute15Data;

    private Map<String,List<StockData>> moneyFlowData;

    private Map<String,Map<String,List<Tick>>> ticksData;

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
        realtimeData = Maps.newConcurrentMap();
        dailyData = Maps.newConcurrentMap();
        minute15Data = Maps.newConcurrentMap();
        moneyFlowData = Maps.newConcurrentMap();
        ticksData = Maps.newConcurrentMap();

//        refreshDailyData();
        ParallelProcesser.schedule(() -> refreshRealTimeData(), 0, 5);
        ParallelProcesser.schedule(() -> refreshMinutesData("15"),0,5);
//        ParallelProcesser.schedule(() -> refreshMoneyFlowData(),0,5);
//        ParallelProcesser.schedule(() -> refreshTicksData(),0,5);
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

    public void refreshMinutesData(String period){
        for(String symbol:stockList){
            ParallelProcesser.process(() -> minute15Data.put(symbol,Provider.minuteData(symbol,period)));
        }
    }

    public void refreshMoneyFlowData(){
        DateRange dateRange = DateRange.getRange(120);
        for(String symbol:stockList){
            ParallelProcesser.process(() -> moneyFlowData.put(symbol,Provider.moneyFlowData(symbol, dateRange.start(),dateRange.stop())));
        }
    }

    public void refreshTicksData(){
        DateRange dateRange = DateRange.getRange(20);
        List<String> dateList = dateRange.getDateList();
        for(String symbol:stockList){
            Map<String,List<Tick>> map = Maps.newConcurrentMap();
            for(String date:dateList){
                ParallelProcesser.process(() -> map.put(date,Provider.tickData(symbol,date)));
            }
            ticksData.put(symbol,map);
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
        return getDailyData(symbol,250);
    }

    /**
     * 获取日线股票数据
     * @param symbol    股票代码
     * @param period    时间长度
     * @return
     */
    public List<StockData> getDailyData(String symbol,int period){
        DateRange dateRange = DateRange.getRange(period);
        List<StockData> stockDataList = dailyData.get(symbol);
        if(stockDataList==null){
            stockDataList = Provider.dailyData(symbol,dateRange.start(),dateRange.stop());
            dailyData.put(symbol,stockDataList);
        }
        return stockDataList;
    }

    /**
     * 获取股票实时数据
     * @param symbol
     * @return
     */
    public StockData getRealTimeData(String symbol){
        StockData stockData = realtimeData.get(symbol);
        if(stockData==null){
            stockData = Provider.realtimeData(symbol);
            realtimeData.put(symbol,stockData);
        }
        return stockData;
    }

    /**
     * 获取股票15分钟k线数据
     * @param symbol
     * @return
     */
    public List<StockData> getMinute15Data(String symbol){
        List<StockData> stockDataList = minute15Data.get(symbol);
        if(stockDataList==null){
            stockDataList = Provider.minuteData(symbol,"15");
            minute15Data.put(symbol,stockDataList);
        }
        return stockDataList;
    }

    /**
     * 打印当前数据中心管理的股票列表
     */
    public void printStockList(){
        LOG.info(Utils.formatDate(new Date(),"yyyyMMdd HH:mm:ss")+"stockList count="+stockList.size());
        stockList.forEach(symbol -> LOG.info(symbol));
    }

    public static void main(String[] args) throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Context context = new Context();
        DataCenter dataCenter = new DataCenter(context);
        List<String> stockList = Provider.tradingStockList();
        dataCenter.init(stockList);
        TimeUnit.SECONDS.sleep(10);
        while(true){
            System.out.println("dailyData:" +dataCenter.dailyData.size());
            System.out.println("realtimeData:" +dataCenter.realtimeData.size());
            System.out.println("minute15Data:" + dataCenter.minute15Data.size());
            System.out.println("moneyFlowData:" + dataCenter.moneyFlowData.size());
            System.out.println("ticksData:" + dataCenter.ticksData.size());
            System.out.println("----------------- cost:"+stopwatch.elapsed(TimeUnit.SECONDS) + "s");
            TimeUnit.SECONDS.sleep(3);

        }
    }
}
