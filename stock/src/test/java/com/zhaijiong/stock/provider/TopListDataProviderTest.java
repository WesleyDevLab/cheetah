package com.zhaijiong.stock.provider;

import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TopListDataProviderTest {

    @Test
    public void testGetDailyTopList() throws Exception {

    }

    @Test
    public void testGetStockRanking() throws Exception {
        List<StockData> stockRanking = TopListDataProvider.getStockRanking(5);
        stockRanking.forEach(stockData -> {
            System.out.println(stockData);
        });
        System.out.println("count:"+stockRanking.size());
    }

    @Test
    public void testGetOrganizationRanking(){
        List<StockData> stockRanking = TopListDataProvider.getOrganizationRanking(5);
        stockRanking.forEach(stockData -> {
            System.out.println(stockData);
        });
        System.out.println("count:"+stockRanking.size());
    }

    @Test
    public void testGetOrganizationDetailRanking(){
        List<StockData> stockRanking = TopListDataProvider.getOrganizationDetailRanking(5);
        stockRanking.forEach(stockData -> {
            System.out.println(stockData);
        });
        System.out.println("count:"+stockRanking.size());
    }
}