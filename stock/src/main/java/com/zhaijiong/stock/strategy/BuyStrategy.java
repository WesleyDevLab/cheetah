package com.zhaijiong.stock.strategy;

import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-28.
 */
public interface BuyStrategy {
    /**
     * 买入价格
     * @param stock
     * @return
     */
    Map<String,Double> buy(String stock);
}
