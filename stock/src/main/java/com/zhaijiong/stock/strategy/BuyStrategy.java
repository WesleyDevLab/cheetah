package com.zhaijiong.stock.strategy;


import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-28.
 */
public interface BuyStrategy {
    /**
     * 买入价格
     * @return
     */
    double buy(String symbol);

    boolean isPicked(String symbol);
}
