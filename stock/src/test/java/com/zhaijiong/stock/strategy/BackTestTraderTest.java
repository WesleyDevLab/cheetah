package com.zhaijiong.stock.strategy;

import com.google.common.base.Stopwatch;
import com.zhaijiong.stock.BackTestTrader;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import com.zhaijiong.stock.strategy.sell.MACDSellStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BackTestTraderTest {

    private BackTestTrader backTestTrader;

    @Before
    public void setUp() throws Exception {
        Context context = new Context();
        MACDBuyStrategy macdBuyStrategy = new MACDBuyStrategy(1, PeriodType.DAY);
        MACDSellStrategy macdSellStrategy = new MACDSellStrategy(1, PeriodType.DAY);
        DefaultStrategy strategy = new DefaultStrategy(macdBuyStrategy,macdSellStrategy);
        backTestTrader = new BackTestTrader(context,strategy);
    }

    @After
    public void tearDown() throws Exception {
        backTestTrader.cleanup();
    }

    @Test
    public void testTest() throws Exception {
        backTestTrader.test("600030");
        backTestTrader.print();
    }

    @Test
    public void testTestList() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> stockDatalist = Provider.stockList();
        System.out.println("stockDataList:"+stockDatalist.size());
        backTestTrader.test(stockDatalist);
        System.out.println("cost:"+stopwatch.elapsed(TimeUnit.SECONDS)+"s");
    }
}