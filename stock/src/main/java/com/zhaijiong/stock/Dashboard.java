package com.zhaijiong.stock;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.RealTimeDataProvider;
import com.zhaijiong.stock.tools.StockList;
import com.zhaijiong.stock.tools.tableformat.SimpleTableFormatter;
import com.zhaijiong.stock.tools.tableformat.TableFormatter;
import org.HdrHistogram.ConcurrentDoubleHistogram;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-1.
 */
public class Dashboard {

    private static ConcurrentDoubleHistogram histogram = new ConcurrentDoubleHistogram(2);

    private AtomicInteger stockCount = new AtomicInteger(0);
    private AtomicInteger delistedCount = new AtomicInteger(0);
    private AtomicInteger suspendedCount = new AtomicInteger(0);
    private AtomicInteger tradingCount = new AtomicInteger(0);

    ExecutorService threadPool = Executors.newFixedThreadPool(20);
    CountDownLatch latch;

    public void overview() throws IOException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> stockList = StockList.getList();
        stockCount.addAndGet(stockList.size());
        latch = new CountDownLatch(stockList.size());
        for (final String symbol : stockList) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String stockStatus = StockList.getStockStatus(symbol);
                        if (stockStatus.equals("delisted")) {
                            delistedCount.incrementAndGet();
                            latch.countDown();
                            return;
                        } else if (stockStatus.equals("suspended")) {
                            suspendedCount.incrementAndGet();
                            latch.countDown();
                            return;
                        } else if (stockStatus.equals("trading")) {
                            tradingCount.incrementAndGet();
                        } else {
                            latch.countDown();
                            return;
                        }

                        StockData stockData = RealTimeDataProvider.get(symbol);
                        Double change = stockData.get("change");
                        if (change != null) {
                            histogram.recordValue(change + 11);
                        } else {
                            System.out.println("can't find " + symbol);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }
            });
        }
        latch.await();
        System.out.println("股票数量:" + stockCount);
        System.out.println("退市:" + delistedCount);
        System.out.println("停牌:" + suspendedCount);
        System.out.println("交易中:" + tradingCount);
//        System.out.println("最大值:"+Utils.formatDouble((histogram.getMaxValue()-11)));
        System.out.println("平均涨跌幅:" + Utils.formatDouble((histogram.getMean() - 11)));
//        System.out.println("最小值:"+Utils.formatDouble((histogram.getMinValue()-11)));
        System.out.println("涨跌幅标准差:" + Utils.formatDouble((histogram.getStdDeviation())));
        System.out.println("涨停家数:" + ((Double) histogram.getCountBetweenValues(19.5, 100)).intValue());
        System.out.println("跌停家数:" + ((Double) histogram.getCountBetweenValues(0, 1.5)).intValue());

        System.out.println("上涨家数:" + ((Double) histogram.getCountBetweenValues(10.1, 100)).intValue());
        System.out.println("下跌家数:" + ((Double) histogram.getCountBetweenValues(0, 10.1)).intValue());
        System.out.println("----涨跌幅分布统计----");
        System.out.println("   < -8:" + getPercent(0, 2) + "%");
        System.out.println("-8 ~ -6:" + getPercent(2, 4) + "%");
        System.out.println("-6 ~ -4:" + getPercent(4, 6) + "%");
        System.out.println("-4 ~  2:" + getPercent(6, 8) + "%");
        System.out.println("-2 ~  0:" + getPercent(8, 10) + "%");
        System.out.println(" 0 ~  2:" + getPercent(10, 12) + "%");
        System.out.println(" 2 ~  4:" + getPercent(12, 14) + "%");
        System.out.println(" 4 ~  6:" + getPercent(14, 16) + "%");
        System.out.println(" 6 ~  8:" + getPercent(16, 18) + "%");
        System.out.println("   >  8:" + getPercent(18, 100) + "%");

        System.out.println("耗时:" + stopwatch.elapsed(TimeUnit.SECONDS) + "s");

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            System.out.println("shutdown...");
            threadPool.shutdownNow();
            Thread.sleep(1000);
        }
    }

    private double getPercent(double start, double stop) {
        return Utils.formatDouble(histogram.getCountBetweenValues(start, stop) / histogram.getTotalCount() * 100, "#.##");
    }

    public void list(List<String> symbols) {
        TableFormatter tf = new SimpleTableFormatter(true);
        tf.nextRow()
                .nextCell().addLine("  namename  ")
                .nextCell().addLine("  close  ")
                .nextCell().addLine("  change  ")
                .nextCell().addLine("  open  ")
                .nextCell().addLine("  low  ")
                .nextCell().addLine("  high  ")
                .nextCell().addLine("  amount  ")
                .nextCell().addLine("turnoverRate")
                .nextCell().addLine("quantityRelative")
                .nextCell().addLine("avgCost");

        for (String symbol : symbols) {
            StockData stockData = RealTimeDataProvider.get(symbol);
                tf.nextRow()
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.name)
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("close")+"")
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("change")+"")
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("open")+"")
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("low")+"")
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("high")+"")
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("amount")+"")
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("turnoverRate")+"")
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("quantityRelative")+"")
                        .nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER).addLine(stockData.get("avgCost")+"");

        }

        String[] table = tf.getFormattedTable();
        for (int i = 0, size = table.length; i < size; i++){
            System.out.println(table[i]);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        reporter.start(3, TimeUnit.SECONDS);
        Dashboard dashboard = new Dashboard();
//        dashboard.overview();
//        reporter.report();

        List<String> symbols = Lists.newArrayList(
                "601886",
                "600376",
                "600232",
                "002295"

        );

        while(true){
            dashboard.list(symbols);
            TimeUnit.SECONDS.sleep(30);
        }
    }
}
