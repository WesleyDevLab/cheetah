package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.model.StockData;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-28.
 */
public interface PickStrategy {

    /**
     * 选股策略
     * @return
     */
    boolean pick(String symbol);
}
