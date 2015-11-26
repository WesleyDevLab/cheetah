package com.zhaijiong.stock;

import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.strategy.buy.GoldenSpiderBuyStrategy;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import com.zhaijiong.stock.tools.StockPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RecommenderTest {

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
        List<String> stockList = StockPool.listByConditions(conditions);

        Recommender recommender = new Recommender("macd_day_15min") {
            @Override
            public boolean isBuy(String symbol) {
                if (dayMacdStrategy.isBuy(symbol) && minute15MacdStrategy.isBuy(symbol)) {
                    return true;
                }
                return false;
            }
        };
        recommender.process(stockList);
        recommender.close();
    }
}