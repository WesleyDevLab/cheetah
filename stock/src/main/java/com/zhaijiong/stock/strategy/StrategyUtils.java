package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.model.StockData;

import java.util.List;

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
            Double cross = stockData.get(StockConstants.MACD_CROSS);
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
            Double cross = stockData.get(StockConstants.MACD_CROSS);
            if (cross != null && count - i <= period && cross == 0)
                return true;
        }
        return false;
    }
}
