package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.model.StockData;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-28.
 */
public interface SellStrategy {
    /**
     * 卖出价格
     * @return
     */
    double sell(String symbol);

    public boolean isSell(String symbol);
}
