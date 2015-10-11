package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.DataCenter;

import java.util.List;
import java.util.Map;

public abstract class BaseStrategy implements Strategy{

    public DataCenter dataCenter;

    public Metrics metrics;

    public BaseStrategy(DataCenter dataCenter){
        this.dataCenter = dataCenter;
        metrics = new Metrics();
    }

}
