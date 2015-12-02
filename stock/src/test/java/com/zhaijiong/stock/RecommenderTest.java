package com.zhaijiong.stock;

import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.recommend.Recommender;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import com.zhaijiong.stock.tools.StockPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"classpath:applicationContext.xml"}) //加载配置文件
public class RecommenderTest {

    @Autowired
    StockPool stockPool;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testProcess() throws Exception {
        MACDBuyStrategy dayMacdStrategy = new MACDBuyStrategy(1, PeriodType.DAY);
        MACDBuyStrategy minute15MacdStrategy = new MACDBuyStrategy(3, PeriodType.FIFTEEN_MIN);

        Conditions conditions = new Conditions();
        conditions.addCondition("close", Conditions.Operation.LT, 20d);
        conditions.addCondition("PE", Conditions.Operation.LT, 200d);
        conditions.addCondition("marketValue", Conditions.Operation.LT, 200d);
        List<String> stockList = stockPool.get("small");

        Recommender recommender = new Recommender("macd_day_15min") {
            @Override
            public boolean isBuy(String symbol) {
                if (dayMacdStrategy.isBuy(symbol) ) {
                    if(minute15MacdStrategy.isBuy(symbol))
                    return true;
                }
                return false;
            }

            @Override
            public boolean isBuy(List<StockData> stockDataList) {
                if (dayMacdStrategy.isBuy(stockDataList) && minute15MacdStrategy.isBuy(stockDataList)) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean isSell(String symbol) {
                return false;
            }

            @Override
            public boolean isSell(List<StockData> stockDataList) {
                return false;
            }

        };

        recommender.process(stockList);
        recommender.close();
    }
}