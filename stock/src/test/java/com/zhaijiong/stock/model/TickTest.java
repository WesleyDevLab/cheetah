package com.zhaijiong.stock.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class TickTest {

    @Test
    public void testToString() throws Exception {
        Tick.Type tick = Tick.Type.BUY;
        System.out.println(tick.getType());
    }
}