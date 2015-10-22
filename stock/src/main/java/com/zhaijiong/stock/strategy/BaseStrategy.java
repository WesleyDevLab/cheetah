package com.zhaijiong.stock.strategy;

import com.zhaijiong.stock.DataCenter;

public abstract class BaseStrategy{

    public DataCenter dataCenter;

    public Metrics metrics;

    public IBroker broker;

    public BaseStrategy(DataCenter dataCenter){
        this.dataCenter = dataCenter;
        metrics = new Metrics();
    }

    public BaseStrategy(){
        metrics = new Metrics();
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }
}

