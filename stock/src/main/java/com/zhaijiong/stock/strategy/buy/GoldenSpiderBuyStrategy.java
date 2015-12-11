package com.zhaijiong.stock.strategy.buy;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.StrategyBase;
import com.zhaijiong.stock.strategy.StrategyUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.zhaijiong.stock.common.StockConstants.CLOSE;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-22.
 */
public class GoldenSpiderBuyStrategy extends StrategyBase implements BuyStrategy {

    private int crossCount = 3;
    private static final String NAME = "goldenSpiderBuy";

    public GoldenSpiderBuyStrategy(){
        this.name = NAME;
    }

    public GoldenSpiderBuyStrategy(int crossCount){
        this.crossCount = crossCount;
        this.name = NAME;
    }

    @Override
    public double buy(String symbol) {
        List<StockData> stockDataList = getDailyData(symbol);
        return buy(stockDataList);
    }

    @Override
    public double buy(List<StockData> stockDataList) {
        stockDataList = StrategyUtils.goldenSpider(stockDataList);
        if(stockDataList.size()>1){
            double status = stockDataList.get(stockDataList.size() - 1).get(StockConstants.GOLDEN_SPIDER);
            if(status>=crossCount){
                System.out.println("buy:"+stockDataList.get(stockDataList.size() - 1));
                return stockDataList.get(stockDataList.size() - 1).get(StockConstants.CLOSE);
            }
        }
        return -1;
    }

    @Override
    public boolean isBuy(String symbol) {
        if(blackList.contains(symbol)){
            return false;
        }
        List<StockData> stockDataList = getDailyData(symbol);
        if(stockDataList.size()>=0){
            //如果最后一个stockData不包含今天，则请求实时数据补全
            Date lastDate = stockDataList.get(stockDataList.size()-1).date;
            Date recentDate = Utils.str2Date(Utils.getRecentWorkingDay(lastDate,"yyyyMMdd"),"yyyyMMdd");
            if(recentDate.getTime()!=lastDate.getTime()){
                StockData stockData = Provider.realtimeData(symbol);
                stockDataList.add(stockData);
                LOG.warn(String.format("%s data is expired.",symbol));
            }
        }
        return isBuy(stockDataList);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        int size = stockDataList.size();
        if(size==0 || size < 60){
            return false;
        }
        stockDataList = StrategyUtils.goldenSpider(stockDataList);
        if(size>2){
            double status = stockDataList.get(size - 1).get(StockConstants.GOLDEN_SPIDER);
            if(status>=crossCount){
                return true;
            }
        }
        return false;
    }

}
