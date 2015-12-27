package com.zhaijiong.stock;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.ArticleType;
import com.zhaijiong.stock.model.StockData;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class UtilsTest {

    @Test
    public void testIsTradingTime(){
        System.out.println(Utils.isTradingTime());

        DateTime dateTime = new DateTime();
        System.out.println(dateTime.dayOfWeek().get());
        dateTime = dateTime.plusDays(1);
        System.out.println(dateTime.dayOfWeek().get());
    }

    @Test
    public void testGetTomorrow() throws Exception {
        System.out.println(Utils.getTomorrow());
    }

    @Test
    public void testGetYesterday() throws Exception {
        System.out.println(Utils.getYesterday());
    }

    @Test
    public void testGetRowkeyWithMD5Prefix(){
        StockData stock = new StockData();
        stock.symbol ="002444";
        byte[] rowkey = Utils.getRowkeyWithMD5Prefix(stock);
        System.out.println(Bytes.toString(rowkey));
    }

    @Test
    public void testJoin(){
        List<String> test = Lists.newArrayList("a","b");
        System.out.println(Joiner.on(",").join(test));
    }

    @Test
    public void testIsBetween(){
        System.out.println(Utils.isBetween(8.8,7,10));
        System.out.println(Utils.isBetween(8.8,8.8,10));
        System.out.println(Utils.isBetween(8.8,5,7));
    }

    @Test
    public void testEnum(){
        System.out.println(Bytes.toInt(ArticleType.FINANCIAL_STATEMENTS.getType()));
    }

    @Test
    public void testGetYearBetween(){
        DateRange dateRange = DateRange.getRange(5000);
        List<String> years = Utils.getYearBetween(dateRange.start(),dateRange.stop());
        for(String year:years){
            System.out.println(year);
        }
    }

    @Test
    public void testGetRecentWorkingDay(){
        Date date = Utils.str2Date("20151211","yyyyMMdd");
        String dateStr = Utils.getRecentWorkingDay(date,"yyyyMMdd");
        Assert.assertEquals("20151211",dateStr);
        date = Utils.str2Date("20151213","yyyyMMdd");
        dateStr = Utils.getRecentWorkingDay(date,"yyyyMMdd");
        Assert.assertEquals("20151211",dateStr);
        date = Utils.str2Date("20151207","yyyyMMdd");
        dateStr = Utils.getRecentWorkingDay(date,"yyyyMMdd");
        Assert.assertNotEquals("20151206",dateStr);
    }
}