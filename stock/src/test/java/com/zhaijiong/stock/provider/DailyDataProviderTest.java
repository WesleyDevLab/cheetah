package com.zhaijiong.stock.provider;

import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.model.StockData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DailyDataProviderTest {

    @Test
    public void testGetFQ() throws Exception {
        String symbol ="002271";
        DateRange dateRange = DateRange.getRange(1000);
        List<StockData> fq = DailyDataProvider.getFQ(symbol, dateRange.start(), dateRange.stop());
        for(StockData stockData:fq){
            System.out.println(stockData);
        }
    }
}