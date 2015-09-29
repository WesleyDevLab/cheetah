package com.zhaijiong.stock.strategy;

import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-29.
 */
public interface RiskStrategy {

    Map<String,Double> risk(String stock);
}
