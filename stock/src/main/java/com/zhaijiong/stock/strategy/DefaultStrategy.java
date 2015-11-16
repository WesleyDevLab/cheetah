package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.strategy.buy.BuyStrategy;
import com.zhaijiong.stock.strategy.sell.SellStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-9.
 */
public class DefaultStrategy implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategy.class);

    private final String name;
    private final BuyStrategy buyStrategy;
    private final SellStrategy sellStrategy;

    private static final String DEFAULT_NAME = "default_strategy";

    public DefaultStrategy(BuyStrategy buyStrategy, SellStrategy sellStrategy){
        this(DEFAULT_NAME,buyStrategy,sellStrategy);
    }

    public DefaultStrategy(String name, BuyStrategy buyStrategy, SellStrategy sellStrategy){
        this.name = name;
        LOG.info(String.format("strategy name is [%s]",name));
        this.buyStrategy = buyStrategy;
        this.sellStrategy = sellStrategy;
    }

    public String getName(){
        return this.name;
    }

    public BuyStrategy getBuyStrategy() {
        return buyStrategy;
    }

    public SellStrategy getSellStrategy() {
        return sellStrategy;
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
