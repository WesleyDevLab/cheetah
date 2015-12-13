package com.zhaijiong.stock.tools;

import com.google.gson.Gson;
import com.zhaijiong.stock.model.StockBlock;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class StockCategoryTest {

    @Test
    public void testGetCategory() throws Exception {

    }

    @Test
    public void testGetStockCategory() throws Exception {
        StockCategory stockCategory1 = new StockCategory();
        stockCategory1.init();
        Map<String, Set<String>> stockCategory = StockCategory.getStockCategory("概念");
        for(Map.Entry<String,Set<String>> category:stockCategory.entrySet()){
            System.out.println(category.getKey());
            for(String item:category.getValue()){
                System.out.println(item);
            }
        }
    }

    @Test
    public void testGetStockCategory1() throws Exception {
        StockCategory stockCategory1 = new StockCategory();
        stockCategory1.init();
        Map<String, List<StockBlock>> stockCategory = StockCategory.getCategory();
        Gson gson = new Gson();
        String s = gson.toJson(stockCategory);
        System.out.println(s);
        stockCategory = gson.fromJson(s, Map.class);
        for(Map.Entry<String,List<StockBlock>> entry:stockCategory.entrySet()){
            System.out.println(entry.getKey());
//            for(StockBlock stockBlock:entry.getValue()){
//                System.out.println(stockBlock);
//            }
        }
    }
}