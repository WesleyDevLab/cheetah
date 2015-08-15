package com.zhaijiong.stock.collect;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-15.
 */
public interface Collecter<K,V> {

    public Map<K,V> collect(String symbol);

    public String getPath(String symbol);
}
