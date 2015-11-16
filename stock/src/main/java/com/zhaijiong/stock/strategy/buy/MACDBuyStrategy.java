package com.zhaijiong.stock.strategy.buy;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.Sleeper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-18.
 *
 * 策略判断步骤说明:
 * 1.判断日线级别macd最近n天是否处于金叉状态,并且红柱持续放大
 * 2.判断最近n根15分钟数据是否处于金叉状态
 */
public class MACDBuyStrategy implements BuyStrategy {

    private int timeRange = 5;
    private PeriodType type;

    public MACDBuyStrategy(int timeRange, PeriodType type){
        this.timeRange = timeRange;
        this.type = type;
    }

    @Override
    public double buy(String symbol){
        List<StockData> stockDataList = getStockDataByType(symbol);
        return buy(stockDataList);
    }

    @Override
    public double buy(List<StockData> stockDataList){
        int count = stockDataList.size();
        stockDataList = Provider.computeMACD(stockDataList);
        for (int i = count - 1; i > 0; i--) {
            StockData stockData = stockDataList.get(i);
            Double cross = stockData.get(StockConstants.MACD_CROSS);
            if (cross != null && count - i <= timeRange && cross == 1)
                return stockData.get("close");
        }
        return -1;
    }


    @Override
    public boolean isBuy(String symbol) {
        List<StockData> stockDataList = getStockDataByType(symbol);
        return isBuy(stockDataList);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList){
        stockDataList = Provider.computeMACD(stockDataList);
        if (isGoldenCrossIn(stockDataList, timeRange)) {
            return true;
        }
        return false;
    }

    private List<StockData> getStockDataByType(String symbol) {
        List<StockData> stockDataList;
        switch (type){
            case FIVE_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "5"));
                break;
            case FIFTEEN_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "15"));
                break;
            case THIRTY_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "30"));
                break;
            case SIXTY_MIN:
                stockDataList = Lists.newArrayList(Provider.minuteData(symbol, "60"));
                break;
            case DAY:
                stockDataList = Lists.newArrayList(Provider.dailyData(symbol,false));
                break;
            default:
                stockDataList = Lists.newArrayList(Provider.dailyData(symbol,false));
        }
        return stockDataList;
    }

    /**
     * 判断最近n个时间周期内是否出现金叉
     *
     * @param period
     * @return 如果是返回true
     */
    public boolean isGoldenCrossIn(List<StockData> stockDataList, int period) {
        int count = stockDataList.size();
        for (int i = count - 1; i > 0; i--) {
            StockData stockData = stockDataList.get(i);
            Double cross = stockData.get(StockConstants.MACD_CROSS);
            if (cross != null && count - i <= period && cross == 1)
                return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {

        MACDBuyStrategy dayMacdStrategy = new MACDBuyStrategy(3,PeriodType.DAY);
        MACDBuyStrategy minute15MacdStrategy = new MACDBuyStrategy(3,PeriodType.FIFTEEN_MIN);
        MACDBuyStrategy minute5MacdStrategy = new MACDBuyStrategy(3,PeriodType.FIVE_MIN);

        Conditions conditions = new Conditions();
        conditions.addCondition("close", Conditions.Operation.LT,30d);
        List<String> stockList = Provider.getStockListWithConditions(Provider.tradingStockList(Provider.marginTradingStockList()),conditions);

        ExecutorService pool = Executors.newFixedThreadPool(32);
        while(true){
            if(Utils.isTradingTime()){
                Stopwatch stopwatch = Stopwatch.createStarted();
                System.out.println(Utils.formatDate(new Date(),"yyyyMMdd HH:mm:ss"));
                CountDownLatch countDownLatch = new CountDownLatch(stockList.size());

                for (String symbol : stockList) {
                    pool.execute(() -> {
                        if (dayMacdStrategy.isBuy(symbol)) {
                            if (minute15MacdStrategy.isBuy(symbol)) {
                                if(minute5MacdStrategy.isBuy(symbol)){
                                    System.out.println(Provider.realtimeData(symbol));
                                }
                            }
                        }
                        countDownLatch.countDown();
                    });
                }
                countDownLatch.await();
                System.out.println("cost:"+stopwatch.elapsed(TimeUnit.MILLISECONDS)+"ms");
            }
            Sleeper.sleep(300*1000);
        }

//        Utils.closeThreadPool(pool);

    }
}
