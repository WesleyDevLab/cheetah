package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.tools.SharpeRatio;
import com.zhaijiong.stock.tools.SortinoRatio;
import org.apache.http.annotation.NotThreadSafe;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-11-13.
 */
@NotThreadSafe
public class Account implements Cloneable{
    public Date date;   //时间戳
    private Map<String,Position> positions = Maps.newConcurrentMap();   //股票持仓情况

    public double start = 100000;   //起始资产
    public double end = 100000;     //期末资产

    public double pnl = 0;    //交易盈亏
    public double pnlRate = 0;    //收益率=交易盈亏/起始资产
    public double annualReturn = 0; //年化收益率= 交易盈亏 / (平均持股天数/交易总天数)

    public double avgPostionDays = 0;   //平均每只股票持仓天数

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

    public double sharpe = 0;   //夏普率
    public double sortino = 0;  //所提诺比率

    private List<Account> status = Lists.newLinkedList();
    private List<Position> positionHis = Lists.newLinkedList();

    //股票持仓情况
    public class Position{
        public String symbol;
        public Date buyDate;
        public Date sellDate;
        public long volume;  //成交量
        public double amount;    //成交额
        public double price;   //成交价
        public double sellPrice;    //卖出价
        public double pnl;

        public Position(String symbol,Date buyDate,long volume,double price){
            this.symbol = symbol;
            this.buyDate = buyDate;
            this.volume = volume;
            this.price = price;
            this.amount = price * volume;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Date getBuyDate() {
            return buyDate;
        }

        public void setBuyDate(Date buyDate) {
            this.buyDate = buyDate;
        }

        public Date getSellDate() {
            return sellDate;
        }

        public void setSellDate(Date sellDate) {
            this.sellDate = sellDate;
        }

        public long getVolume() {
            return volume;
        }

        public void setVolume(long volume) {
            this.volume = volume;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getSellPrice() {
            return sellPrice;
        }

        public void setSellPrice(double sellPrice) {
            this.sellPrice = sellPrice;
        }

        public double getPnl() {
            return pnl;
        }

        public void setPnl(double pnl) {
            this.pnl = pnl;
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

    public void sell(String symbol,Date sellDate,double sellPrice){
        Position position = this.positions.get(symbol);
        position.symbol = " "+symbol;   //保存到excel，如果是数字字符串，开头的00会被删掉
        position.sellDate = sellDate;
        position.sellPrice = sellPrice;

        double pnl = (sellPrice - position.price) * position.volume;
        position.pnl = pnl;    //计算单笔交易盈亏
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
        computeRate(getStatus(),0.03);
        this.positions.remove(symbol);
        this.positionHis.add(position);
    }

    private void computeRate(List<Account> status, double rf) {
        List<Double> returns = Lists.newLinkedList();
        for(Account account :status){
            returns.add(account.pnlRate);
        }
        this.sharpe = SharpeRatio.value(returns,rf);
        this.sortino = SortinoRatio.value(returns,rf);
    }

    public void saveStatus(Date date) {
        try {
            Account account = (Account) this.clone();
            account.date = date;
            status.add(account);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public boolean isHold(String symbol){
        return this.positions.containsKey(symbol);
    }

    @Override
    public String toString() {
        return "Account{" +
                "date=" + Utils.formatDate(date,"yyyyMMdd") +
                ", start=" + start +
                ", end=" + end +
                ", pnl=" + pnl +
                ", pnlRate=" + pnlRate +
                ", maxEarnPerOp=" + maxEarnPerOp +
                ", meanEarnPerOp=" + meanEarnPerOp +
                ", maxLossPerOp=" + maxLossPerOp +
                ", benchmarkBenfit=" + benchmarkBenfit +
                ", benchmarkBenfitPercent=" + benchmarkBenfitPercent +
                ", max=" + max +
                ", min=" + min +
                ", drawdown=" + drawdown +
                ", earnOperate=" + earnOperate +
                ", totalOperate=" + totalOperate +
                ", lossOperate=" + lossOperate +
                ", accuracy=" + accuracy +
                ", sharpe=" + sharpe +
                ", sortino=" + sortino +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public double getPnl() {
        return pnl;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public double getPnlRate() {
        return pnlRate;
    }

    public void setPnlRate(double pnlRate) {
        this.pnlRate = pnlRate;
    }

    public double getMaxLossPerOp() {
        return maxLossPerOp;
    }

    public void setMaxLossPerOp(double maxLossPerOp) {
        this.maxLossPerOp = maxLossPerOp;
    }

    public double getMaxEarnPerOp() {
        return maxEarnPerOp;
    }

    public void setMaxEarnPerOp(double maxEarnPerOp) {
        this.maxEarnPerOp = maxEarnPerOp;
    }

    public double getMeanEarnPerOp() {
        return meanEarnPerOp;
    }

    public void setMeanEarnPerOp(double meanEarnPerOp) {
        this.meanEarnPerOp = meanEarnPerOp;
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

    public double getDrawdown() {
        return drawdown;
    }

    public void setDrawdown(double drawdown) {
        this.drawdown = drawdown;
    }

    public int getTotalOperate() {
        return totalOperate;
    }

    public void setTotalOperate(int totalOperate) {
        this.totalOperate = totalOperate;
    }

    public int getEarnOperate() {
        return earnOperate;
    }

    public void setEarnOperate(int earnOperate) {
        this.earnOperate = earnOperate;
    }

    public int getLossOperate() {
        return lossOperate;
    }

    public void setLossOperate(int lossOperate) {
        this.lossOperate = lossOperate;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getSharpe() {
        return sharpe;
    }

    public void setSharpe(double sharpe) {
        this.sharpe = sharpe;
    }

    public double getSortino() {
        return sortino;
    }

    public void setSortino(double sortino) {
        this.sortino = sortino;
    }

    public double getAvgPostionDays() {
        return avgPostionDays;
    }

    public void setAvgPostionDays(double avgPostionDays) {
        this.avgPostionDays = avgPostionDays;
    }

    public List<Position> getPositionHis() {
        return positionHis;
    }

    public void setPositionHis(List<Position> positionHis) {
        this.positionHis = positionHis;
    }
}
