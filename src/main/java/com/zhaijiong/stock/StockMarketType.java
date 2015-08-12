package com.zhaijiong.stock;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-12.
 */
public enum StockMarketType {
    SH,SZ,UNKNOW;

    public static StockMarketType getType(String symbol){
        if(symbol.startsWith("6")){
            return SH;
        }else if(symbol.startsWith("0") || symbol.startsWith("3")){
            return SZ;
        }else{
            return UNKNOW;
        }
    }

    public boolean isMatchType(String symbol){
        if(this.equals(SH)){
            return symbol.startsWith("6");
        }
        if(this.equals(SZ)){
            return symbol.startsWith("0") || symbol.startsWith("3");
        }
        return false;
    }
}
