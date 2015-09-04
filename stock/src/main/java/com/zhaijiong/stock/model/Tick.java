package com.zhaijiong.stock.model;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-3.
 */
public class Tick {

    public String date;     //时间，格式是HH:mm:ss

    public double price;    //成交价格  单位：元

    public double volume;   //成交量   单位：手

    public double amount;   //成交额

    public Type type;

    public enum Type{
        BUY,    //买盘
        SELL,   //卖盘
        MID;     //中性盘

        @Override
        public String toString(){
            if(this == BUY){
                return "买盘";
            }else if(this == SELL){
                return "卖盘";
            }else{
                return "中性盘";
            }
        }
    }

    @Override
    public String toString() {
        return "Tick{" +
                "时间='" + date + '\'' +
                ", 价格=" + price +
                ", 成交量=" + volume +
                ", 成交额=" + amount +
                ", 类型=" + type +
                '}';
    }
}
