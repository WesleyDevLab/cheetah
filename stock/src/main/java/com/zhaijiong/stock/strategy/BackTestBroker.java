package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rits.cloning.Cloner;
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

    private class Account implements Cloneable{
        public long timeStamp = 0;   //时间戳
        public long amount = 0;    //购买股票数量

        public double start = 100000;   //起始资产
        public double end = 100000;     //期末资产

        public double earn = 0;    //交易净利润
        public double earnRate = 0;    //收益率

        public double maxEarnPerOp = 0; //最大单笔盈利
        public double maxLossPerOp = 0; //最大单笔亏损
        public double meanEarnPerOp = 0; //平均每笔盈利

//        public double continuousEarnOp = 0; //连续盈利次数
//        public double continuousLossOp = 0; //连续亏损次数

        public double benchmarkBenfit = 0; //基准收益额，同期股价涨跌额,单位：元
        public double benchmarkBenfitPercent = 0;   //基准收益百分比
        public double max = 0;     //最大资产
        public double min = 100000;     //最小资产
        public double drawdown = 0; //最大回撤=最大资产-最小资产

        public int totalOperate = 0;  //总交易次数
        public int earnOperate = 0;   //总盈利次数
        public int lossOperate = 0; //总亏损交易次数
        public double accuracy = 0;  //操作正确率=总盈利次数/总交易次数

        private List<Account> status = Lists.newLinkedList();

        public void saveStatus(Date date) {
            try {
                Account account = (Account) this.clone();
                account.timeStamp = date.getTime();
                status.add(account);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "Account{" +
                    "accuracy=" + accuracy +
                    ", timeStamp=" + timeStamp +
                    ", amount=" + amount +
                    ", start=" + start +
                    ", end=" + end +
                    ", earn=" + earn +
                    ", earnRate=" + earnRate +
                    ", maxEarnPerOp=" + maxEarnPerOp +
                    ", maxLossPerOp=" + maxLossPerOp +
                    ", meanEarnPerOp=" + meanEarnPerOp +
                    ", benchmarkBenfit=" + benchmarkBenfit +
                    ", benchmarkBenfitPercent=" + benchmarkBenfitPercent +
                    ", max=" + max +
                    ", min=" + min +
                    ", drawdown=" + drawdown +
                    ", totalOperate=" + totalOperate +
                    ", earnOperate=" + earnOperate +
                    ", lossOperate=" + lossOperate +
                    '}';
        }
    }

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
        double price = 0;

        double stockStartPrice = stockDataList.get(60).get("close");
        double stockStopPrice = stockDataList.get(stockDataList.size() - 1).get("close");
        boolean isHold = false;

        for (int i = 60; i < stockDataList.size(); i++) {
            List<StockData> tmpList = stockDataList.subList(0, i);
            if (isHold) {
                if (strategy.isSell(tmpList)) {
                    double sellPrice = strategy.sell(tmpList);
                    double earn = (sellPrice - price) * account.amount;
                    account.earn += earn;
                    account.end += earn;
                    account.totalOperate++;
                    if (earn > 0) {
                        account.earnOperate++;
                        if (earn > account.maxEarnPerOp) {
                            account.maxEarnPerOp = earn;
                        }
                        if (account.end > account.max) {
                            account.max = account.end;
                        }
                    } else {
                        account.lossOperate++;
                        if (earn < account.maxLossPerOp) {
                            account.maxLossPerOp = earn;
                        }
                        if (account.end < account.min) {
                            account.min = account.end;
                        }
                    }
                    account.amount = 0;
                    isHold = false;
                    account.saveStatus(tmpList.get(tmpList.size() - 1).date);
//                }
                }else if(tmpList.get(i-1).get("close")-price<0 && Math.abs(tmpList.get(i-1).get("close")-price)>price*0.05){
                    double sellPrice = tmpList.get(i-1).get("close");
                    double earn = (sellPrice - price)*account.amount;
                    account.earn += earn;
                    account.end += earn;
                    account.totalOperate++;
                    if(earn>0){
                        account.earnOperate++;
                        if(earn>account.maxEarnPerOp){
                            account.maxEarnPerOp = earn;
                        }
                        if(account.end > account.max){
                            account.max = account.end;
                        }
                    }else{
                        account.lossOperate++;
                        if(earn<account.maxLossPerOp){
                            account.maxLossPerOp = earn;
                        }
                        if(account.end < account.min){
                            account.min = account.end;
                        }
                    }
                    account.amount = 0;
                    isHold = false;
                    account.saveStatus(tmpList.get(tmpList.size() - 1).date);
                }
                continue;
            } else {
                if (strategy.isBuy(tmpList)) {
                    price = strategy.buy(tmpList);
                    account.amount = new Double((Math.floor((account.end / price/100)))).longValue()*100;
                    if(account.amount>0){
                        isHold = true;
                    }
                }
            }
        }
        account.benchmarkBenfit = stockStopPrice - stockStartPrice;
        account.benchmarkBenfitPercent = (stockStopPrice - stockStartPrice) / stockStartPrice;
        account.earnRate = account.earn / account.end;
        account.accuracy = Double.parseDouble(String.valueOf(account.earnOperate)) / account.totalOperate;
        account.meanEarnPerOp = account.earn / account.totalOperate;
        account.drawdown = account.max - account.min;
        accountMap.put(symbol,account);
    }

    public void print(){
        for(Map.Entry<String,Account> stock:accountMap.entrySet()){
            System.out.println(stock.getKey()+":"+stock.getValue());
            List<Account> accounts = stock.getValue().status;
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
