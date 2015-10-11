package com.zhaijiong.stock.strategy;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-28.
 */
public interface PickStrategy {

    /**
     * 选股策略
     * @param stock
     * @return
     */
    boolean pick(String stock);
}
