package com.zhaijiong.stock.strategy;

import com.google.common.base.Preconditions;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-23.
 */
public class Order {
    private String traderId;
    private Execution execution;
    private int status = 0; //0:未成交，1:成交

    public Order(String traderId, Execution execution) {
        Preconditions.checkNotNull(traderId,"trader id must be set");
        this.traderId = traderId;
        this.execution = execution;
    }

    public void done(){
        this.status = 1;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "traderId='" + traderId + '\'' +
                ", execution=" + execution +
                ", status=" + status +
                '}';
    }
}
