package com.zhaijiong.stock.datasource;

import com.zhaijiong.stock.Stock;

import java.util.List;

/**
 * Created by eryk on 2015/7/4.
 */
public interface StockDataCollecter {

    public List<Stock> collect(String stock,String startDate,String stopDate);
}
