package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.model.StockData;

import java.util.List;

import static com.zhaijiong.stock.common.StockConstants.*;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-17.
 */
public class StrategyUtils {

    /**
     * 判断最近n个时间周期内是否出现金叉
     *
     * @param period
     * @return 如果是返回true
     */
    public static boolean isMACDGoldenCrossIn(List<StockData> stockDataList, int period) {
        int count = stockDataList.size();
        for (int i = count - 1; i > 0; i--) {
            StockData stockData = stockDataList.get(i);
            Double cross = stockData.get(MACD_CROSS);
            if (cross != null && count - i <= period && cross == 1)
                return true;
        }
        return false;
    }

    /**
     * 判断最近n个时间周期内是否出现死叉
     *
     * @param period
     * @return 如果是返回true
     */
    public static boolean isMACDDiedCrossIn(List<StockData> stockDataList, int period) {
        int count = stockDataList.size();
        for (int i = count - 1; i > 0; i--) {
            StockData stockData = stockDataList.get(i);
            Double cross = stockData.get(MACD_CROSS);
            if (cross != null && count - i <= period && cross == 0)
                return true;
        }
        return false;
    }

    /**
     * 计算有几条均线粘合,粘合数加入到AVERAGE_BOND参数中
     *
     * @return
     */
    public static List<StockData> averageBond(List<StockData> stockDataList,double threshold) {
        List<StockData> result = Lists.newLinkedList();
        for (int i = 0; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);
            double ma5 = stockData.get(CLOSE_MA5);
            double ma10 = stockData.get(CLOSE_MA10);
            double ma20 = stockData.get(CLOSE_MA20);
            double ma30 = stockData.get(CLOSE_MA30);
            double ma40 = stockData.get(CLOSE_MA40);
            double ma60 = stockData.get(CLOSE_MA60);
//            double ma120 = stockData.get(CLOSE_MA120);
            if (ma5 == 0 || ma10 == 0 || ma20 == 0 || ma30 == 0 || ma40==0 || ma60 == 0){
                continue;
            }
            double maCount = 0;
            double[] maArr = {ma5,ma10,ma20,ma30,ma40,ma60};
            for(int j=0;j<5;j++){
                for(int k =j+1;k<6;k++){
                    if(Math.abs(maArr[j]/maArr[k] - 1) < threshold){
                        maCount++;
                    }
                }
            }
            stockData.put(AVERAGE_BOND,maCount);
            result.add(stockData);
        }
        return result;
    }
}
