package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.strategy.buy.BuyStrategy;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-15.
 */
public class StrategyGroup {

    public List<Strategy> strategyList;

    public enum OperatorType{
        AND,OR
    }


}
