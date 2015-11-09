package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.model.StockData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-9.
 */
public class BaseStrategy implements Strategy{
    private BuyStrategy buyStrategy;
    private SellStrategy sellStrategy;
    private RiskStrategy riskStrategy;

    public BaseStrategy(){}

    public BaseStrategy(BuyStrategy buyStrategy,SellStrategy sellStrategy,RiskStrategy riskStrategy){
        this.buyStrategy = buyStrategy;
        this.sellStrategy = sellStrategy;
        this.riskStrategy = riskStrategy;
    }

    public BuyStrategy getBuyStrategy() {
        return buyStrategy;
    }

    public void setBuyStrategy(BuyStrategy buyStrategy) {
        this.buyStrategy = buyStrategy;
    }

    public SellStrategy getSellStrategy() {
        return sellStrategy;
    }

    public void setSellStrategy(SellStrategy sellStrategy) {
        this.sellStrategy = sellStrategy;
    }

    public RiskStrategy getRiskStrategy() {
        return riskStrategy;
    }

    public void setRiskStrategy(RiskStrategy riskStrategy) {
        this.riskStrategy = riskStrategy;
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return LocalDateTime.now();
    }

    @Override
    public double buy(String symbol) {
        return buyStrategy.buy(symbol);
    }

    @Override
    public double buy(List<StockData> stockDataList) {
        return buyStrategy.buy(stockDataList);
    }

    @Override
    public boolean isBuy(String symbol) {
        return buyStrategy.isBuy(symbol);
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        return buyStrategy.isBuy(stockDataList);
    }

    @Override
    public Map<String, Double> risk(String symbol) {
        return riskStrategy.risk(symbol);
    }

    @Override
    public double sell(String symbol) {
        return sellStrategy.sell(symbol);
    }

    @Override
    public double sell(List<StockData> stockDataList) {
        return sellStrategy.sell(stockDataList);
    }

    @Override
    public boolean isSell(String symbol) {
        return sellStrategy.isSell(symbol);
    }

    @Override
    public boolean isSell(List<StockData> stockDataList) {
        return sellStrategy.isSell(stockDataList);
    }
}
