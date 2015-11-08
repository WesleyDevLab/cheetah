package com.zhaijiong.stock.strategy.impl;

import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.indicators.Indicators;
import com.zhaijiong.stock.indicators.TDXFunction;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.Strategy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-20.
 */
public class QSDDStrategy implements Strategy {
    TDXFunction tdxFunction = new TDXFunction();
    Indicators indicators = new Indicators();

    /**
     * http://www.88gs.com/soft/sort013/sort05/down-27108.html
     * 长期线 A:=MA(-100*(HHV(HIGH,34)-CLOSE)/(HHV(HIGH,34)-LLV(LOW,34)),19),COLORRED;
     * 短期线 B:=-100*(HHV(HIGH,14)-CLOSE)/(HHV(HIGH,14)-LLV(LOW,14));
     * 中期线 D:=EMA(-100*(HHV(HIGH,34)-CLOSE)/(HHV(HIGH,34)-LLV(LOW,34)),4),LINETHICK2;
     * <p>
     * 见顶:(REF(中期线,1)>85 AND REF(短期线,1)>85 AND REF(长期线,1)>65) AND CROSS(长期线,短期线) ;
     * 顶部区域:(中期线<REF(中期线,1) AND REF(中期线,1)>80) AND (REF(短期线,1)>95 OR REF(短期线,2)>95 ) AND 长期线>60 AND 短期线<83.5
     * AND 短期线<中期线 AND 短期线<长期线+4;
     * <p>
     * 低位金叉:长期线<15 AND REF(长期线,1)<15 AND 中期线<18 AND 短期线>REF(短期线,1) AND CROSS(短期线,长期线) AND 短期线>中期线
     * AND (REF(短期线,1)<5 OR REF(短期线,2)<5 ) AND (中期线>=长期线 OR REF( 短期线,1)<1 );
     */

    public double[][] compute(List<StockData> stockDataList) {
        double[] closes = Utils.getArrayFrom(stockDataList, StockConstants.CLOSE);
        double[] high = Utils.getArrayFrom(stockDataList, StockConstants.HIGH);
        double[] hhvHigh14 = tdxFunction.hhv(high, 14);
        double[] hhvHigh34 = tdxFunction.hhv(high, 34);
        double[] low = Utils.getArrayFrom(stockDataList, StockConstants.LOW);
        double[] llvLow14 = tdxFunction.llv(low, 14);
        double[] llvLow34 = tdxFunction.llv(low, 34);

        double[] longLine = new double[stockDataList.size()];
        double[] longLineTmp = new double[stockDataList.size()];
        double[] midLine = new double[stockDataList.size()];
        double[] midLineTmp = new double[stockDataList.size()];
        double[] shortLine = new double[stockDataList.size()];
        double[] shortLineTmp = new double[stockDataList.size()];

        for (int i = 34; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);
            //长期线 A:=MA(-100*(HHV(HIGH,34)-CLOSE)/(HHV(HIGH,34)-LLV(LOW,34)),19),COLORRED;
            longLineTmp[i] = -100 * (hhvHigh34[i] - closes[i]) / (hhvHigh34[i] - llvLow34[i]);
            //短期线 B:=-100*(HHV(HIGH,14)-CLOSE)/(HHV(HIGH,14)-LLV(LOW,14));
            shortLineTmp[i] = -100 * (hhvHigh14[i] - closes[i]) / (hhvHigh14[i] - llvLow14[i]);
            midLineTmp[i] = longLineTmp[i];
        }
        shortLine = Arrays.copyOfRange(shortLineTmp, 34, shortLineTmp.length);

        longLineTmp = Arrays.copyOfRange(longLineTmp, 34, longLineTmp.length);
        longLine = indicators.sma(longLineTmp, 19);

        midLineTmp = Arrays.copyOfRange(midLineTmp, 34, midLineTmp.length);
        midLine = indicators.ema(midLineTmp, 4);

        double[][] result = {longLine, midLine, shortLine};
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> stockList = Provider.tradingStockList();

        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());


        for (String symbol : stockList) {
            pool.execute(() -> {
                System.out.println("compute:" + symbol);
                List<StockData> stockDataList = Provider.dailyData(symbol, 240);
                QSDDStrategy strategy = new QSDDStrategy();
                double[][] qsdd = strategy.compute(stockDataList);
                /**
                 * 见顶:(REF(中期线,1)>85 AND REF(短期线,1)>85 AND REF(长期线,1)>65) AND CROSS(长期线,短期线) ;
                 */
                int i = qsdd.length - 1;
//                if(qsdd[1][i-1]>85 && qsdd[2][i-1]>85 && qsdd[0][i-1]>65 && qsdd[0][i-1] < qsdd[2][i-1] && qsdd[0][i] > qsdd[2][i]){
//                    System.out.println(Utils.formatDate(stockDataList.get(i).date,"yyyyMMdd")+"\t"+symbol+"\t"+qsdd[0][i]+"\t"+qsdd[1][i]+"\t"+qsdd[2][i]);
//                }
                if (qsdd[2][i]+100 < 15) {
                    System.out.println(Utils.formatDate(stockDataList.get(i).date, "yyyyMMdd") + "\t" + symbol + "\t" + qsdd[0][i] + "\t" + qsdd[1][i] + "\t" + qsdd[2][i]);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        Utils.closeThreadPool(pool);
    }

    @Override
    public double buy(String symbol) {
        return 0;
    }

    @Override
    public boolean isBuy(String symbol) {
        return false;
    }

    @Override
    public Map<String, Double> risk(String symbol) {
        return null;
    }

    @Override
    public double sell(String symbol) {
        return 0;
    }

    @Override
    public boolean isSell(String symbol) {
        return false;
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return null;
    }
}
