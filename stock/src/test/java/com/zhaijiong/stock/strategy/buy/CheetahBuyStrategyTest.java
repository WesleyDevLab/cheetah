package com.zhaijiong.stock.strategy.buy;

import com.zhaijiong.stock.tools.StockPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"classpath:applicationContext.xml"}) //加载配置文件
public class CheetahBuyStrategyTest {

    @Autowired
    @Qualifier("stockPool")
    StockPool stockPool;

    @Autowired
    @Qualifier("cheetahBuyStrategy")
    CheetahBuyStrategy cheetahBuyStrategy;

    @Test
    public void testIsBuy() throws Exception {
        List<String> stockList = stockPool.tradingStock();
        for(String symbol:stockList){
            if(cheetahBuyStrategy.isBuy(symbol)){
                System.out.println(symbol);
            }
        }
    }

    @Test
    public void testIsBuy1() throws Exception {
        String symbol = "002461";
        if(cheetahBuyStrategy.isBuy(symbol)){
            System.out.println(symbol);
        }
    }
}