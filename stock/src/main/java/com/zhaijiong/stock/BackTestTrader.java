package com.zhaijiong.stock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.Account;
import com.zhaijiong.stock.strategy.DefaultStrategy;
import com.zhaijiong.stock.tools.ExcelExportHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zhaijiong.stock.common.Constants.*;

/**
 * 策略回测系统
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
    private DefaultStrategy strategy;

    public BackTestTrader(Context context, DefaultStrategy strategy) {
        this.context = context;
        this.strategy = strategy;

        this.tradingDayCount = this.context.getInt(TRADER_TRADING_DAY_COUNT,DEFAULT_TRADING_DAY_COUNT);
        this.excelBaseDir = this.context.getStr(TRADER_EXCEL_BASE_DIR);
        this.executorService = Executors.newFixedThreadPool(this.context.getInt(TRADER_POOL_SIZE,DEFAULT_TRADER_POOL_SIZE));

        LOG.info("startup:");
        LOG.info("\t"+TRADER_TRADING_DAY_COUNT+":"+tradingDayCount);
        LOG.info("\t"+TRADER_EXCEL_BASE_DIR+":"+excelBaseDir);
        LOG.info("\t"+TRADER_POOL_SIZE+":"+this.context.getInt(TRADER_POOL_SIZE,DEFAULT_TRADER_POOL_SIZE));
    }

    public void cleanup(){
        LOG.info("cleanup:");
        LOG.info("total account:"+accountMap.size());
        Utils.closeThreadPool(executorService);
    }

    public void test(List<String> symbols) {
        CountDownLatch countDownLatch = new CountDownLatch(symbols.size());
        for (String symbol : symbols) {
            executorService.execute(() -> {
                LOG.info(String.format("start test %s with strategy %s", symbol, strategy.getName()));
                try {
                    test(symbol);
                    countDownLatch.countDown();
                } catch (Exception e) {
                    LOG.error(String.format("fail to test symbol %s", symbol), e);
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void test(String symbol) {
        Account account = new Account();
        List<StockData> stockDataList = Provider.computeDailyAll(symbol, tradingDayCount);
        for (int i = 200; i < stockDataList.size(); i++) {
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

    /**
     * 输出当前股票的所有卖出情况以账户状态
     * @param symbol
     * @param account
     */
    public void export(String symbol,Account account){
        List accounts = account.getStatus();
        ExcelExportHelper excelExportHelper = new ExcelExportHelper();
        HSSFWorkbook excel = excelExportHelper.exportExcel(EXCEL_HEADER, EXCEL_COLUMN, accounts, "account");
        //TODO 添加sheet，增加汇总信息
        excelExportHelper.saveExcel(excel,excelBaseDir,symbol);
    }

    public void print(){
        List accountList = Lists.newLinkedList();
        for(Map.Entry<String,Account> accountEntry:accountMap.entrySet()){
            LOG.info("summary add stock:"+accountEntry.getKey());
            accountList.add(accountEntry.getValue());
        }
        ExcelExportHelper excelExportHelper = new ExcelExportHelper();
        HSSFWorkbook excel = excelExportHelper.exportExcel(EXCEL_HEADER, EXCEL_COLUMN, accountList, "total");
        excelExportHelper.saveExcel(excel,excelBaseDir,"summary");
        //TODO 输入所有股票的平均状态
    }

}
