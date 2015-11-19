package com.zhaijiong.stock.trade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-21.
 */
public class Execution {
    private String stockname;
    private String symbol;
    private LocalDateTime date;
    private double price;
    private double volume;
    private Type type;    //buy,sell
    private double fees;

    public enum Type{
        BUY,SELL
    }

    public Execution(String symbol,LocalDateTime date,double price,Type type){
        this(symbol,date,price,1,type);
    }

    public Execution(String symbol, LocalDateTime date, double price, double volume, Type type) {
        this(symbol,date,price,volume,type,0);
    }

    public Execution(String symbol, LocalDateTime date, double price, double volume, Type type, double fees) {
        this.symbol = symbol;
        this.date = date;
        this.price = price;
        this.volume = volume;
        this.type = type;
        this.fees = fees;
    }

    public String getSymbol() {
        return symbol;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public double getVolume() {
        return volume;
    }

    public Type getType() {
        return type;
    }

    public double getFees() {
        return fees;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "symbol='" + symbol + '\'' +
                ", date=" + date +
                ", price=" + price +
                ", volume=" + volume +
                ", type='" + type + '\'' +
                ", fees=" + fees +
                '}';
    }
}
