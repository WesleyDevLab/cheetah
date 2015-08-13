package com.zhaijiong.stock.datasource;

import com.zhaijiong.stock.model.Stock;

import java.util.List;

/**
 * Created by eryk on 2015/7/4.
 */
public interface Collecter {

    public List<Stock> collect(String stock);
}
