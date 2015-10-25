package com.zhaijiong.stock.strategy;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-21.
 */
public class Operation {
    private String symbol;
    private LocalDateTime date;
    private double price;
    private double volume;
    private String type;
    private double fees;

    public Operation(String symbol, LocalDateTime date, double price, double volume, String type) {
        this.symbol = symbol;
        this.date = date;
        this.price = price;
        this.volume = volume;
        this.type = type;
        this.fees = 0.0;
    }

    public Operation(String symbol, LocalDateTime date, double price, double volume, String type, double fees) {
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
