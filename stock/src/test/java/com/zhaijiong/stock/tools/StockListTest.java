package com.zhaijiong.stock.tools;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.provider.Provider;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StockListTest {

    @Test
    public void testGetMarginTradingStockList() throws Exception {
        Conditions conditions = new Conditions();
        conditions.addCondition("close", Conditions.Operation.LT,30d);
//        List<String> stockList = Provider.tradingStockList(Provider.marginTradingStockList());
        List<String> stockList = Provider.getStockListWithConditions(Provider.tradingStockList(Provider.marginTradingStockList()), conditions);
//        List<String> stockList = StockList.getMarginTradingStockList();
//        System.out.println(stockList.size());
//        List<String> tradingStockList = StockList.getTradingStockList(stockList);
        for(String symbol:stockList){
            System.out.println(symbol);
        }
        System.out.println(stockList.size());
    }
}