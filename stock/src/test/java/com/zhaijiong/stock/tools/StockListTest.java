package com.zhaijiong.stock.tools;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StockListTest {

    @Test
    public void testGetMarginTradingStockList() throws Exception {
        List<String> stockList = StockList.getMarginTradingStockList();
        System.out.println(stockList.size());
        List<String> tradingStockList = StockList.getTradingStockList(stockList);
        for(String symbol:tradingStockList){
            System.out.println(symbol);
        }
        System.out.println(tradingStockList.size());
    }
}