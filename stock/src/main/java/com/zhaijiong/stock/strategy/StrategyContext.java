package com.zhaijiong.stock.strategy;

import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-28.
 */
public class StrategyContext implements Strategy{

    PickStrategy pickStrategy;

    BuyStrategy buyStrategy;

    SellStrategy sellStrategy;

    RiskStrategy riskStrategy;

    public void setPickStrategy(PickStrategy pickStrategy) {
        this.pickStrategy = pickStrategy;
    }

    public void setBuyStrategy(BuyStrategy buyStrategy) {
        this.buyStrategy = buyStrategy;
    }

    public void setSellStrategy(SellStrategy sellStrategy) {
        this.sellStrategy = sellStrategy;
    }

    public void setRiskStrategy(RiskStrategy riskStrategy) {
        this.riskStrategy = riskStrategy;
    }

    @Override
    public boolean pick(String stock) {
        return pickStrategy.pick(stock);
    }

    @Override
    public Map<String,Double> buy(String stock) {
        return buyStrategy.buy(stock);
    }

    @Override
    public Map<String,Double> sell(String stock) {
        return sellStrategy.sell(stock);
    }

    @Override
    public Map<String,Double> risk(String stock) {
        return riskStrategy.risk(stock);
    }
}
