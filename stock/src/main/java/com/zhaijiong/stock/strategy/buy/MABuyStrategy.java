package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.StrategyBase;
import com.zhaijiong.stock.strategy.StrategyUtils;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-5.
 * 买入策略由两条均线是否金叉决定
 */

public class MABuyStrategy extends StrategyBase implements BuyStrategy{

    private int period = 0; //用户判断ma在多少个周期内相交
    private PeriodType type = PeriodType.DAY;
    private static final String NAME = "maBuy";
    private String maFast = "close_ma5";
    private String maSlow = "close_ma10";

    public MABuyStrategy(){
        this.name = NAME;
    }

    public MABuyStrategy(int period,String maFast,String maSlow,PeriodType type){
        this.period = period;
        this.maFast = maFast;
        this.maSlow = maSlow;
        this.type = type;
        this.name = NAME;
    }

    @Override
    public double buy(String symbol) {
        List<StockData> stockDataList = getStockDataByType(type, symbol);
        return buy(stockDataList);
    }

    @Override
    public double buy(List<StockData> stockDataList) {
        return StrategyUtils.goldenCrossPrice(stockDataList,maFast,maSlow,period);
    }

    @Override
    public boolean isBuy(String symbol) {
        List<StockData> stockDataList = getStockDataByType(type, symbol);
        if(stockDataList!=null && stockDataList.size()>60){
            return isBuy(stockDataList);
        }
        return false;
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        stockDataList = Provider.computeMA(stockDataList, StockConstants.CLOSE);
        return StrategyUtils.isGoldenCrossIn(stockDataList,maFast,maSlow,period);
    }


}
