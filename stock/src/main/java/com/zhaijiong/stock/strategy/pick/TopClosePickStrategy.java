package com.zhaijiong.stock.strategy.pick;

import com.zhaijiong.stock.strategy.BuyStrategy;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-6.
 */
public class TopClosePickStrategy implements BuyStrategy {
    @Override
    public double buy(String symbol) {
        return 0;
    }

    @Override
    public boolean isBuy(String symbol) {
        return false;
    }
}
