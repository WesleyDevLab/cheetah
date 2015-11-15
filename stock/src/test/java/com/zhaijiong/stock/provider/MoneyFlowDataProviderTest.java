package com.zhaijiong.stock.provider;

import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.model.StockData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MoneyFlowDataProviderTest {

    @Test
    public void testGet() throws Exception {
        StockData stockData = Provider.moneyFlowData("600030");
        System.out.println(stockData);
    }

    @Test
    public void testGet1() throws Exception {
        DateRange range = DateRange.getRange(90);
        List<StockData> stockDataList = Provider.moneyFlowData("600887", range.start(), range.stop());
        for(StockData stockData:stockDataList){
            System.out.println(stockData);
        }

    }

    @Test
    public void testGetDapan() throws Exception {

    }

    @Test
    public void testGetIndustry() throws Exception {

    }

    @Test
    public void testGetConcept() throws Exception {

    }

    @Test
    public void testGetRegion() throws Exception {

    }
}