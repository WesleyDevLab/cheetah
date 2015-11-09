package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-22.
 */
public class BackTestBroker{

    private double start = 1;   //起始资产
    private double end = 1;     //期末资产
    private double earn = 0;    //交易盈亏
    private double benchmarkBenfit = 0; //基准收益额，同期股价涨跌额,单位：元
    private double benchmarkBenfitPercent = 0;   //基准收益百分比
    private double max = 0;     //最大资产
    private double min = 0;     //最小资产
    private double maxDrawdown = 0; //最大回撤

    private String symbol;
    private Strategy strategy;

    public BackTestBroker(Strategy strategy){
        this.strategy = strategy;
    }

    public void test(String symbol){
        List<StockData> stockDataList = Provider.dailyData(symbol,1000,false);
        double price = 0;

        double stockStartPrice = stockDataList.get(89).get("close");
        double stockStopPrice = stockDataList.get(stockDataList.size()-1).get("close");
        benchmarkBenfitPercent = (stockStopPrice-stockStartPrice)/stockStartPrice;

        boolean isHold = false;

        for(int i =90;i<stockDataList.size();i++){
            List<StockData> tmpList = stockDataList.subList(0, i);
            if(isHold){
                if(strategy.isSell(stockDataList)){

                }
                continue;
            }else{
                if(strategy.isBuy(tmpList)){
                    price = strategy.buy(tmpList);
                }
            }
        }
        System.out.println("benchmarkBenfitPercent:"+benchmarkBenfitPercent);
    }

    public static void main(String[] args) {
        BaseStrategy strategy = new BaseStrategy();
        MACDBuyStrategy macdBuyStrategy = new MACDBuyStrategy(3, PeriodType.DAY);
        strategy.setBuyStrategy(macdBuyStrategy);
        BackTestBroker backTestBroker = new BackTestBroker(strategy);
        backTestBroker.test("600887");
    }
}
