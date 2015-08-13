package com.zhaijiong.stock.model;

import com.google.common.base.Strings;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-10.
 */
public class Symbol {

    public String code;

    public String site;

    public StockMarketType stockMarketType;

    public BoardType boardType;

    public static String getSymbol(String code,String url){
        if(url.contains("sina.com")){
            return sinaSymbol(code);
        }
        if(url.contains("163.com")){
            return netEaseSymbol(code);
        }
        if(url.contains("eastmoney.com")){
            return sinaSymbol(code);
        }
        if(url.contains("ifeng.com")){
            return sinaSymbol(code);
        }
        return "";
    }

    /**
     * 雅虎股票接口需要将股票代码后面加上.ss或者.sz
     * @param symbol
     * @return
     */
    public static String yahooSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return symbol + ".sz";
        }
        if(symbol.startsWith("6")){
            return symbol + ".ss";
        }
        return "";
    }

    public static String sinaSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return "sz" + symbol;
        }
        if(symbol.startsWith("6")){
            return "sh" + symbol;
        }
        return "";
    }

    public static String netEaseSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("6")){
            return "0" + symbol;
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return "1" + symbol;
        }
        return "";
    }

    public static StockMarketType getStockMarketType(String symbol){
        return StockMarketType.getType(symbol);
    }

    public static BoardType getBoardType(String symbol){
        return BoardType.getType(symbol);
    }
}
