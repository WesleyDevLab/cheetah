package com.zhaijiong.stock.convert;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;

import java.util.List;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-16.
 */
public interface Converter<F> {

    List<Put> toPut(F obj);

}
