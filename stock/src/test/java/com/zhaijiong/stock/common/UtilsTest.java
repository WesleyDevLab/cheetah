package com.zhaijiong.stock.common;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.provider.Provider;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;


public class UtilsTest {

    @Test
    public void testHeadTailList() throws Exception {
        List<String> strings = Lists.newArrayList("a", "b", "c", "d", "e", "f", "g");
        List<String> list = Utils.headList(strings, 3);
        Assert.assertEquals("a",list.get(0));
        Assert.assertEquals("c",list.get(2));
        list = Utils.tailList(strings,5);
        Assert.assertEquals("c",list.get(0));
        Assert.assertEquals("g",list.get(4));
    }

    @Test
    public void testHeadTailArray() throws Exception{
        double[] arr = {0,1,2,3,4,5,6,7,8,9};
        double[] head = Utils.headArray(arr,3);
        double[] expectHead = {0,1,2};
        Assert.assertArrayEquals(expectHead,head,0);
        double[] tail = Utils.tailArray(arr,5);
        double[] expectTail = {5,6,7,8,9};
        Assert.assertArrayEquals(expectTail,tail,0);
    }

    @Test
    public void testGetYear() throws Exception {
        System.out.println(Utils.getYear(0));
        System.out.println(Utils.getYear(1));
        System.out.println(Utils.getYear(-1));
    }

    @Test
    public void testIsTradingTime(){
        System.out.println(Utils.isTradingTime());
    }

    @Test
    public void toArray(){
        String[] array = Utils.toArray(Provider.tradingStockList());
        for(String stock:array){
            System.out.println(stock);
        }
    }
}