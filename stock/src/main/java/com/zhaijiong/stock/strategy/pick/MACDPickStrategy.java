package com.zhaijiong.stock.strategy.pick;

import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.BaseStrategy;
import com.zhaijiong.stock.strategy.PickStrategy;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-18.
 * 结合日线和15分钟k线选股
 */
public class MACDPickStrategy extends BaseStrategy implements PickStrategy {

    private int timeRange = 5;

    /**
     * 策略判断步骤说明:
     * 1.判断日线级别macd最近n天是否处于金叉状态,并且红柱持续放大
     * 2.判断最近n根15分钟数据是否处于金叉状态
     *
     * @param stockDataList
     * @return
     */
    @Override
    public boolean pick(String symbol,List<StockData> stockDataList) {
        if (isGoldenCrossIn(stockDataList, timeRange)) {
            return true;
        }
        return false;
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
        MACDPickStrategy strategy = new MACDPickStrategy();
        List<String> stockList = Provider.tradingStockList();
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());

        for (String symbol : stockList) {
            pool.execute(() -> {
                List<StockData> stockDataList = Provider.computeDailyMACD(symbol,250);
                if (strategy.pick(symbol,stockDataList)) {
                    List<StockData> minuteStockDataList = Provider.minuteData(symbol, "15");
                    stockDataList = Provider.computeMACD(minuteStockDataList);
                    if (strategy.pick(symbol,stockDataList)) {
                        System.out.println(symbol);
                    }
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        Utils.closeThreadPool(pool);
    }
}
