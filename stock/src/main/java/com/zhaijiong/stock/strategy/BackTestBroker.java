package com.zhaijiong.stock.strategy;

import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import com.zhaijiong.stock.strategy.sell.MACDSellStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-22.
 */
public class BackTestBroker {
    private static final Logger LOG = LoggerFactory.getLogger(BackTestBroker.class);

    private final Integer tradingDayCount = 10 * 250;    //默认10年
    //存储每个股票的交易状态
    private Map<String, Account> accountMap = Maps.newConcurrentMap();
    private ExecutorService executorService = Executors.newFixedThreadPool(32);

    private BaseStrategy strategy;

    public BackTestBroker(BaseStrategy strategy) {
        this.strategy = strategy;
    }

    public void test(List<String> symbols) {
        for (String symbol : symbols) {
            LOG.info(String.format("start test %s with strategy %s", symbol, strategy.getName()));
            try {
                test(symbol);
            } catch (Exception e) {
                LOG.error(String.format("fail to test symbol %s", symbol), e);
            }
        }
    }

    public void test(String symbol) {
        Account account = new Account();
        List<StockData> stockDataList = Provider.dailyData(symbol, 1000, false);


        boolean isHold = false;

        for (int i = 60; i < stockDataList.size(); i++) {
            List<StockData> tmpList = stockDataList.subList(0, i);
            Date date = tmpList.get(tmpList.size() - 1).date;
            if (isHold) {
                if (strategy.isSell(tmpList)) {
                    double stockStartPrice = tmpList.get(0).get("close");
                    double stockStopPrice = tmpList.get(tmpList.size() - 1).get("close");

                    double sellPrice = strategy.sell(tmpList);
                    account.sell(symbol,date,sellPrice);
                    account.benchmarkBenfit = stockStopPrice - stockStartPrice;
                    account.benchmarkBenfitPercent = (stockStopPrice - stockStartPrice) / stockStartPrice;
                    account.saveStatus(date);
                    isHold = false;
                }
//              else if(tmpList.get(i-1).get("close")-price<0 && Math.abs(tmpList.get(i-1).get("close")-price)>price*0.05){
                continue;
            } else {
                if (strategy.isBuy(tmpList)) {
                    double price = strategy.buy(tmpList);
                    if(account.buy(symbol,date,price)){
                        isHold = true;
                    }
                }
            }
        }
        accountMap.put(symbol,account);
    }

    public void print(){
        for(Map.Entry<String,Account> stock:accountMap.entrySet()){
            System.out.println(stock.getKey()+":"+stock.getValue());
            List<Account> accounts = stock.getValue().getStatus();
            for(Account account:accounts){
                System.out.println(Utils.formatDate(Utils.long2Date(account.timeStamp),"yyyyMMdd")+account);
            }
        }
    }

    public static void main(String[] args) {
        BaseStrategy strategy = new BaseStrategy();
        MACDBuyStrategy macdBuyStrategy = new MACDBuyStrategy(1, PeriodType.DAY);
        strategy.setBuyStrategy(macdBuyStrategy);
        MACDSellStrategy macdSellStrategy = new MACDSellStrategy(1, PeriodType.DAY);
        strategy.setSellStrategy(macdSellStrategy);
        BackTestBroker backTestBroker = new BackTestBroker(strategy);
//        List<String> stockDatalist = Provider.stockList();
//        backTestBroker.test(stockDatalist);
        backTestBroker.test("600030");
        backTestBroker.print();
    }
}
