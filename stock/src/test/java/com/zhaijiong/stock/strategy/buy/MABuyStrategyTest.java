package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.tools.StockPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"classpath:applicationContext.xml"}) //加载配置文件
public class MABuyStrategyTest {

    @Autowired
    StockPool stockPool;

    @Autowired
    private MABuyStrategy maBuyStrategy;

    @Test
    public void testIsBuy() throws Exception {
        List<String> stockList = stockPool.get("small");
        for(String symbol :stockList){
            if(maBuyStrategy.isBuy(symbol)){
                System.out.println("isBuy:"+symbol);
            }
        }
    }

    @Test
    public void testIsBuy1() throws Exception {
        String symbol = "002135";
        if(maBuyStrategy.isBuy(symbol)){
            System.out.println("isBuy:"+symbol);
        }
    }
}