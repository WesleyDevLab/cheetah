package com.zhaijiong.stock.common;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DateRangeTest {

    @Test
    public void testGetRange() throws Exception {
        DateRange dateRange =DateRange.getRange(10);
        System.out.println(dateRange.start());
        System.out.println(dateRange.stop());
    }

    @Test
    public void testGetDateList(){
        DateRange dateRange = DateRange.getRange(10);
        List<String> dateList = dateRange.getDateList();
        dateList.forEach(date -> System.out.println(date));
    }
}