package com.zhaijiong.stock.strategy;

import com.google.common.collect.Maps;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-10.
 */
public class Account {
    public static final String DEFAULT_NAME = "cheetah";
    private String name;

    private static Integer INIT_FUND = 100000;  //起始资金数量

    private Record status = new Record(LocalDateTime.MIN,INIT_FUND);

    private TreeMap<LocalDateTime,Record> records = new TreeMap<LocalDateTime,Record>();    //记录各个时间点账户状态

    private LocalDateTime ts;//最后更新record的时间点

    private Map<String,Position> positions = Maps.newConcurrentMap();   //记录账户当前持仓情况

    private class Record {
        private double start = 0;   //起始资产
        private double end = 0;     //期末资产
        private double earn = 0;    //交易盈亏
        private double benchmarkBenfit = 0; //基准收益额，同期股价涨跌额,单位：元
        private double benchmarkBenfitPercent = 0;   //基准收益百分比
        private double marketIndexPercent = 0; //大盘涨跌幅,同期大盘涨跌百分比
        private double max = 0;     //最大资产
        private double min = 0;     //最小资产
        private double maxDrawdown = 0; //最大回撤
        private double txnFees = 0; //税费总计
        private LocalDateTime ts;

        public Record(LocalDateTime ts, double end) {
            this.ts = ts;
            this.start = end;
            this.end = end;
        }

        public Record(LocalDateTime ts) {
            this.ts = ts;
        }
    }

    //证券名称，证券数量，可卖数量，成本价，浮动盈亏，盈亏比例，最新市值，当前价，今买数量，今卖数量
    public class Position{
        public String stockName;   //证券名称
        public LocalDateTime ts;    //更新时间戳
        public String symbol;      //证券代码
        public int amount;      //证券数量
        public int canSell;     //可卖数量
        public double costPrice;   //成本价
        public double floatPnl;    //浮动盈亏
        public double pnlRatio;    //盈亏比例
        public double latestValue; //最新市值
        public double close;       //当前价
        public double buyAmount;   //今买数量
        public double sellAmount;  //今卖数量

        public Position(){}

        public Position(String symbol,LocalDateTime ts,int amount,double price,double close){
            this.symbol = symbol;
            this.ts = ts;
            this.amount = amount;
            this.canSell = amount;
            this.costPrice = price;
            this.floatPnl = amount*(price-close);
            this.pnlRatio = 0;
            this.latestValue = amount * price;
            this.close = close;
            this.buyAmount = amount;
            this.sellAmount = 0;
        }
    }

    public Account(String name, LocalDateTime startDate, double initial){
        this.name = name;
        this.ts = startDate;
        records.put(ts, new Record(ts, INIT_FUND));
    }

    public Account(){
        this(DEFAULT_NAME, LocalDateTime.MIN, 0.0);
    }

    public Account(String name){
        this(name, LocalDateTime.MIN, 0.0);
    }

    public void record(LocalDateTime ts, double earn){
        Record record = status;
        record.end += earn; //TODO compute new status
        records.put(ts,record);
    }

    public Record getStatus() {
        return status;
    }

    public void setStatus(Record status) {
        this.status = status;
    }

    public Map<String, Position> getPositions() {
        return positions;
    }

    public void setPositions(Map<String,Position> positions){
        this.positions = positions;
    }

}
