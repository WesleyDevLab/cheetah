package com.zhaijiong.stock.strategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-21.
 */
public class Execution {
    private String symbol;
    private LocalDateTime date;
    private double price;
    private double volume;
    private String type;    //buy,sell
    private double fees;

    public Execution(String symbol,LocalDateTime date,double price,String type){
        this(symbol,date,price,1,type);
    }

    public Execution(String symbol, LocalDateTime date, double price, double volume, String type) {
        this(symbol,date,price,volume,type,0);
    }

    public Execution(String symbol, LocalDateTime date, double price, double volume, String type, double fees) {
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

    public String getType() {
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
