package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-10.
 */
public class Metrics {
    private static Integer INIT_FUND = 100000;  //起始资金数量

    private double start = 0;   //起始资产
    private double end = 0;     //期末资产
    private double earn = 0;    //交易盈亏
    private double benchmarkBenfit = 0; //基准收益额，同期股价涨跌额,单位：元
    private double benchmarkBenfitPercent = 0;   //基准收益百分比
    private double marketIndexPercent = 0; //大盘涨跌幅,同期大盘涨跌百分比
    private double max = 0;     //最大资产
    private double min = 0;     //最小资产
    private double maxDrawdown = 0; //最大回撤

    private Map<String,Double> lastOP;

    public List<Map<String,Double>> operator;

    public void buy(){}

    public void sell(){}

    public void export(){}

    public Metrics(){
        start = INIT_FUND;
        end = INIT_FUND;
        max = INIT_FUND;
        min = INIT_FUND;
        operator = Lists.newArrayList();
        lastOP = Maps.newHashMap();
    }

    public void add(Map<String,Double> oper){
        operator.add(oper);
        //TODO 计算收益变化
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "start=" + start +
                ", end=" + end +
                ", earn=" + earn +
                ", max=" + max +
                ", min=" + min +
                '}';
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public double getEarn() {
        return earn;
    }

    public void setEarn(double earn) {
        this.earn = earn;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getBenchmarkBenfit() {
        return benchmarkBenfit;
    }

    public void setBenchmarkBenfit(double benchmarkBenfit) {
        this.benchmarkBenfit = benchmarkBenfit;
    }

    public double getBenchmarkBenfitPercent() {
        return benchmarkBenfitPercent;
    }

    public void setBenchmarkBenfitPercent(double benchmarkBenfitPercent) {
        this.benchmarkBenfitPercent = benchmarkBenfitPercent;
    }

    public double getMarketIndexPercent() {
        return marketIndexPercent;
    }

    public void setMarketIndexPercent(double marketIndexPercent) {
        this.marketIndexPercent = marketIndexPercent;
    }

    public double getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(double maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }
}
