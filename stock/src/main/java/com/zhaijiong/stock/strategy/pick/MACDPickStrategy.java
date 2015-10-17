package com.zhaijiong.stock.strategy.pick;

import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.BaseStrategy;
import com.zhaijiong.stock.strategy.PickStrategy;

import java.util.List;

/**
 * author: xuqi.xq
 * mail: xuqi86@gmail.com
 * date: 15-10-18.
 * 结合日线和15分钟k线选股
 */
public class MACDPickStrategy extends BaseStrategy implements PickStrategy{

    /**
     * 策略判断步骤说明:
     * 1.判断日线级别macd最近5天是否处于金叉状态,并且红柱持续放大
     * 2.判断最近8根15分钟数据是否处于金叉状态
     * @param stock
     * @return
     */
    @Override
    public boolean pick(String stock) {
        List<StockData> stockDataList = Provider.computeDailyMACD(stock, 250);
        if(isGoldenCrossIn(stockDataList,3)){
            List<StockData> minute15Data = Provider.minuteData(stock,"15");
            List<StockData> macdStockDataList = Provider.computeMACD(minute15Data);
            if(isGoldenCrossIn(macdStockDataList,3)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断最近n个时间周期内是否出现金叉
     * @param period
     * @return 如果是返回true
     */
    public boolean isGoldenCrossIn(List<StockData> stockDataList,int period){
        int count = stockDataList.size();
        for(int i =count-1;i>0;i--){
            StockData stockData = stockDataList.get(i);
            Double cross = stockData.get(StockConstants.MACD_CROSS);
            if(cross!=null && count-i<=period && cross==1)
                return true;
        }
        return false;
    }
}
