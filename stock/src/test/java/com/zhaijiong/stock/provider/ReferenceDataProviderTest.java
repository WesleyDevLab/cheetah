package com.zhaijiong.stock.provider;

import com.zhaijiong.stock.model.StockData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ReferenceDataProviderTest {



    @Test
    public void testGetShareHolderCountData(){
        List<StockData> stockDataList = ReferenceDataProvider.getShareHolderCountData("1","20150630");
        stockDataList.forEach(stockData ->{
            System.out.println(stockData);
        });
        System.out.println("count:"+stockDataList.size());
    }

    @Test
    public void testGetShareHolderCountData1(){
        List<StockData> stockDataList = ReferenceDataProvider.getShareHolderCountData("002271");
        stockDataList.forEach(stockData ->{
            System.out.println(stockData);
        });
        System.out.println("count:"+stockDataList.size());
    }

    @Test
    public void testFHRZ(){
        List<String> stockList = Provider.tradingStockList();
        for(String symbol:stockList){
            List<StockData> stockDataList = ReferenceDataProvider.getFHRZ(symbol);
            stockDataList.forEach(stockData ->{
                System.out.println(stockData);
            });
            System.out.println("count:"+stockDataList.size());
        }
    }

    @Test
    public void testGetFPYA() throws Exception {

    }

    @Test
    public void testGetTotalMarginTrade() throws Exception {

    }

    @Test
    public void testGetMarginTrade() throws Exception {

    }

    @Test
    public void testGetMarginTrade1() throws Exception {

    }
}