package com.zhaijiong.stock.strategy;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class AccountTest {

    @Test
    public void testFields() throws Exception {
        Account account = new Account();
        Field[] fields = account.getClass().getDeclaredFields();
        for(Field field:fields){
            System.out.println(field.getName());
        }
    }
}