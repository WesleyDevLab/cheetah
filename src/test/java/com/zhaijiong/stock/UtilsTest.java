package com.zhaijiong.stock;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testGetTomorrow() throws Exception {
        System.out.println(Utils.getTomorrow());
    }

    @Test
    public void testGetYesterday() throws Exception {
        System.out.println(Utils.getYesterday());
    }
}