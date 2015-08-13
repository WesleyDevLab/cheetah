package com.zhaijiong.stock.model;

import java.util.Date;

public class Stock implements Comparable{

    public Date date;   //日期
    public String   symbol; //代码
    public String   name;   //名称
    public double   close;  //收盘价
    public double   high;   //最高价
    public double   low;    //最底价
    public double   open;   //开盘价
    public double   lastClose;  //前收盘
    public double   changeAmount;   //涨跌额
    public double   change; //涨跌幅
    public double   turnoverRate;   //换手率
    public double   volume; //成交量，单位：手
    public double   amount; //成交金额
    public double   totalValue; //总市值
    public double   marketValue;    //流通市值
    public double   amplitude;  //振幅，百分比    (high-low)/前一天close

    @Override
    public String toString() {
        return "Stock{" +
                "date=" + date +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", lastClose=" + lastClose +
                ", changeAmount=" + changeAmount +
                ", change=" + change +
                ", turnoverRate=" + turnoverRate +
                ", volume=" + volume +
                ", amount=" + amount +
                ", totalValue=" + totalValue +
                ", marketValue=" + marketValue +
                ", amplitude=" + amplitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;

        Stock stock = (Stock) o;

        if (Double.compare(stock.amount, amount) != 0) return false;
        if (Double.compare(stock.amplitude, amplitude) != 0) return false;
        if (Double.compare(stock.change, change) != 0) return false;
        if (Double.compare(stock.changeAmount, changeAmount) != 0) return false;
        if (Double.compare(stock.close, close) != 0) return false;
        if (Double.compare(stock.high, high) != 0) return false;
        if (Double.compare(stock.lastClose, lastClose) != 0) return false;
        if (Double.compare(stock.low, low) != 0) return false;
        if (Double.compare(stock.marketValue, marketValue) != 0) return false;
        if (Double.compare(stock.open, open) != 0) return false;
        if (Double.compare(stock.totalValue, totalValue) != 0) return false;
        if (Double.compare(stock.turnoverRate, turnoverRate) != 0) return false;
        if (Double.compare(stock.volume, volume) != 0) return false;
        if (date != null ? !date.equals(stock.date) : stock.date != null) return false;
        if (name != null ? !name.equals(stock.name) : stock.name != null) return false;
        if (!symbol.equals(stock.symbol)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = date != null ? date.hashCode() : 0;
        result = 31 * result + symbol.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(close);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(high);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(low);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(open);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lastClose);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(changeAmount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(change);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(turnoverRate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(volume);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(marketValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(amplitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
