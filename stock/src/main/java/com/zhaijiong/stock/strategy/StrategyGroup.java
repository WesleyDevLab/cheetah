package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.buy.BuyStrategy;
import com.zhaijiong.stock.strategy.sell.SellStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-25.
 */
public class StrategyGroup implements BuyStrategy,SellStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(StrategyGroup.class);

    public enum OpType{AND,OR}

    OpType buy;
    OpType sell;

    List<BuyStrategy> buyStrategies = Lists.newArrayList();
    List<SellStrategy> sellStrategies = Lists.newArrayList();


    public void addBuyStrategy(BuyStrategy buyStrategy){
        LOG.info("add buy strategy:" + buyStrategy.getClass().getSimpleName());
        buyStrategies.add(buyStrategy);
    }

    public void addSellStrategy(SellStrategy sellStrategy){
        LOG.info("add sell strategy:" + sellStrategy.getClass().getSimpleName());
        sellStrategies.add(sellStrategy);
    }

    public List<BuyStrategy> getBuyStrategies() {
        return buyStrategies;
    }

    public List<SellStrategy> getSellStrategies() {
        return sellStrategies;
    }

    @Override
    public double buy(String symbol) {
        return 0;
    }

    @Override
    public double buy(List<StockData> stockDataList) {
        return 0;
    }

    @Override
    public boolean isBuy(String symbol) {
        return false;
    }

    @Override
    public boolean isBuy(List<StockData> stockDataList) {
        return false;
    }

    @Override
    public double sell(String symbol) {
        return 0;
    }

    @Override
    public double sell(List<StockData> stockDataList) {
        return 0;
    }

    @Override
    public boolean isSell(String symbol) {
        return false;
    }

    @Override
    public boolean isSell(List<StockData> stockDataList) {
        return false;
    }
}