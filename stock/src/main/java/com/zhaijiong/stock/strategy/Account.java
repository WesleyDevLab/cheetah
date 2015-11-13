package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-11-13.
 */
public class Account implements Cloneable{
    public long timeStamp = 0;   //时间戳
    private Map<String,Position> positions = Maps.newConcurrentMap();   //股票持仓情况

    public double start = 100000;   //起始资产
    public double end = 100000;     //期末资产

    public double pnl = 0;    //交易盈亏
    public double pnlRate = 0;    //收益率=交易盈亏/起始资产

    public double maxEarnPerOp = 0; //最大单笔盈利
    public double maxLossPerOp = 0; //最大单笔亏损
    public double meanEarnPerOp = 0; //平均每笔盈利

//        public double continuousEarnOp = 0; //连续盈利次数
//        public double continuousLossOp = 0; //连续亏损次数

    public double benchmarkBenfit = 0; //基准收益额，同期股价涨跌额,单位：元
    public double benchmarkBenfitPercent = 0;   //基准收益百分比
    public double max = 0;     //最大资产
    public double min = 100000;     //最小资产
    public double drawdown = 0; //最大回撤=最大资产-最小资产

    public int totalOperate = 0;  //总交易次数
    public int earnOperate = 0;   //总盈利次数
    public int lossOperate = 0; //总亏损交易次数
    public double accuracy = 0;  //操作正确率=总盈利次数/总交易次数

    private List<Account> status = Lists.newLinkedList();

    //股票持仓情况
    public class Position{
        public String symbol;
        public Date date;
        public long volume;  //成交量
        public double amount;    //成交额
        public double price;   //成交价

        public Position(String symbol,Date date,long volume,double price){
            this.symbol = symbol;
            this.date = date;
            this.volume = volume;
            this.price = price;
            this.amount = price * volume;
        }
    }

    public List<Account> getStatus(){
        return status;
    }

    public boolean buy(String symbol,Date date,long volume,double buyPrice){
        Position position = new Position(symbol,date,volume,buyPrice);
        this.positions.put(symbol,position);
        return true;
    }

    public boolean buy(String symbol,Date date,double buyPrice){
        long volume = new Double((Math.floor((this.end / buyPrice/100)))).longValue()*100;
        if(volume>0){
            return buy(symbol,date,volume,buyPrice);
        }
        return false;
    }

    public void sell(String symbol,Date date,double sellPrice){
        Position position = this.positions.get(symbol);

        double pnl = (sellPrice - position.price) * position.volume;
        this.pnl += pnl;
        this.end += pnl;
        this.totalOperate++;
        if (pnl > 0) {
            this.earnOperate++;
            if (pnl > this.maxEarnPerOp) {
                this.maxEarnPerOp = pnl;
            }
            if (this.end > this.max) {
                this.max = this.end;
            }
        } else {
            this.lossOperate++;
            if (pnl < this.maxLossPerOp) {
                this.maxLossPerOp = pnl;
            }
            if (this.end < this.min) {
                this.min = this.end;
            }
        }
        this.pnlRate = this.pnl / this.end;
        this.accuracy = Double.parseDouble(String.valueOf(this.earnOperate)) / this.totalOperate;
        this.meanEarnPerOp = this.pnl / this.totalOperate;
        this.drawdown = this.max - this.min;
        this.positions.remove(symbol);
    }

    public void saveStatus(Date date) {
        try {
            Account account = (Account) this.clone();
            account.timeStamp = date.getTime();
            status.add(account);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "accuracy=" + accuracy +
                ", timeStamp=" + timeStamp +
                ", start=" + start +
                ", end=" + end +
                ", pnl=" + pnl +
                ", pnlRate=" + pnlRate +
                ", maxEarnPerOp=" + maxEarnPerOp +
                ", maxLossPerOp=" + maxLossPerOp +
                ", meanEarnPerOp=" + meanEarnPerOp +
                ", benchmarkBenfit=" + benchmarkBenfit +
                ", benchmarkBenfitPercent=" + benchmarkBenfitPercent +
                ", max=" + max +
                ", min=" + min +
                ", drawdown=" + drawdown +
                ", totalOperate=" + totalOperate +
                ", earnOperate=" + earnOperate +
                ", lossOperate=" + lossOperate +
                '}';
    }
}
