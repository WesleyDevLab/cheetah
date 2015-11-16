package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.strategy.sell.SellStrategy;
import com.zhaijiong.stock.strategy.buy.BuyStrategy;
import com.zhaijiong.stock.strategy.risk.RiskStrategy;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-28.
 */
public interface Strategy extends BuyStrategy,SellStrategy {

}
