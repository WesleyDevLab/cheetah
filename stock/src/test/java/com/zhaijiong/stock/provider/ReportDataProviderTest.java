package com.zhaijiong.stock.provider;

import com.zhaijiong.stock.model.StockData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ReportDataProviderTest {

    @Test
    public void testGetStockReportData() throws Exception {
        List<StockData> stockReportData = ReportDataProvider.getStockReportData("20150921");
        stockReportData.forEach(stockData -> System.out.println(stockData));
    }

    @Test
    public void testGetExpectEarnings() throws Exception {
        List<StockData> stockDataList = ReportDataProvider.getExpectEarnings();
        stockDataList.forEach(stockData ->{
            System.out.println(stockData);
        });
        System.out.println("count:"+stockDataList.size());
    }
}