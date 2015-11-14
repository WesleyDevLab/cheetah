package com.zhaijiong.stock.strategy;

import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.PeriodType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import com.zhaijiong.stock.strategy.sell.MACDSellStrategy;
import com.zhaijiong.stock.tools.ExcelExportHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zhaijiong.stock.common.Constants.*;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-22.
 */
public class BackTestTrader {
    private static final Logger LOG = LoggerFactory.getLogger(BackTestTrader.class);

    private final Integer DEFAULT_TRADING_DAY_COUNT= 1 * 250; //策略执行时长默认250个周期
    private Integer tradingDayCount;
    private final Integer DEFAULT_TRADER_POOL_SIZE = 1;
    private ExecutorService executorService;
    //存储每个股票的交易状态
    private Map<String, Account> accountMap = Maps.newConcurrentMap();
    private String excelBaseDir = "";

    private Context context;
    private BaseStrategy strategy;

    public BackTestTrader(Context context, BaseStrategy strategy) {
        this.context = context;
        this.strategy = strategy;

        this.tradingDayCount = context.getInt(TRADER_TRADING_DAY_COUNT,DEFAULT_TRADING_DAY_COUNT);
        this.excelBaseDir = context.getStr(TRADER_EXCEL_BASE_DIR);
        this.executorService = Executors.newFixedThreadPool(context.getInt(TRADER_POOL_SIZE,DEFAULT_TRADER_POOL_SIZE));
    }

    public void cleanup(){
        Utils.closeThreadPool(executorService);
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
        List<StockData> stockDataList = Provider.dailyData(symbol, tradingDayCount, false);

        for (int i = 60; i < stockDataList.size(); i++) {
            List<StockData> tmpList = stockDataList.subList(0, i);
            Date date = tmpList.get(tmpList.size() - 1).date;
            if (account.isHold(symbol)) {
                if (strategy.isSell(tmpList)) {
                    double stockStartPrice = tmpList.get(0).get("close");
                    double stockStopPrice = tmpList.get(tmpList.size() - 1).get("close");

                    double sellPrice = strategy.sell(tmpList);
                    account.sell(symbol,date,sellPrice);
                    account.benchmarkBenfit = stockStopPrice - stockStartPrice;
                    account.benchmarkBenfitPercent = (stockStopPrice - stockStartPrice) / stockStartPrice;
                    account.saveStatus(date);
                }
//              else if(tmpList.get(i-1).get("close")-price<0 && Math.abs(tmpList.get(i-1).get("close")-price)>price*0.05){
            } else {
                if (strategy.isBuy(tmpList)) {
                    double price = strategy.buy(tmpList);
                    account.buy(symbol,date,price);
                }
            }
        }
        export(symbol,account);
        accountMap.put(symbol,account);
    }

    public void export(String symbol,Account account){
        List accounts = account.getStatus();
        ExcelExportHelper excelExportHelper = new ExcelExportHelper();
        HSSFWorkbook excel = excelExportHelper.exportExcel(EXCEL_HEADER, EXCEL_COLUMN, accounts, "account");
        excelExportHelper.saveExcel(excel,excelBaseDir,symbol);
    }

    public void print(){
        for(Map.Entry<String,Account> stock:accountMap.entrySet()){
            System.out.println(stock.getKey()+":"+stock.getValue());
            List<Account> accounts = stock.getValue().getStatus();
            for(Account account:accounts){
                System.out.println(Utils.formatDate(account.date,"yyyyMMdd")+account);
            }
        }
    }

    public static void main(String[] args) {
        Context context = new Context();
        BaseStrategy strategy = new BaseStrategy();
        MACDBuyStrategy macdBuyStrategy = new MACDBuyStrategy(1, PeriodType.DAY);
        strategy.setBuyStrategy(macdBuyStrategy);
        MACDSellStrategy macdSellStrategy = new MACDSellStrategy(1, PeriodType.DAY);
        strategy.setSellStrategy(macdSellStrategy);
        BackTestTrader backTestTrader = new BackTestTrader(context,strategy);
//        List<String> stockDatalist = Provider.stockList();
//        backTestTrader.test(stockDatalist);
        backTestTrader.test("600030");
        backTestTrader.print();
    }
}
