package com.zhaijiong.stock.datasource;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Pair;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.Stock;
import com.zhaijiong.stock.model.Symbol;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zhaijiong.stock.common.Constants.*;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-9.
 */
public class MinuteStockDataCollecter implements Collecter {
    private static final Logger LOG = LoggerFactory.getLogger(MinuteStockDataCollecter.class);

    public final String minuteDataURL = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=%s&scale=%s&ma=no&datalen=1023";

    public final String type;

    public Date startDate;

    public long startDateLong;

    public Date stopDate;

    public long stopDateLong;

    public MinuteStockDataCollecter(String startDate,String stopDate,String type) {
        this.startDate = Utils.parseDate(startDate,Constants.BISNESS_DATA_FORMAT);
        this.startDateLong = this.startDate.getTime();
        this.stopDate = Utils.parseDate(stopDate, Constants.BISNESS_DATA_FORMAT);
        this.stopDateLong = this.stopDate.getTime();
        this.type = type;
    }

    @Override
    public List<Stock> collect(String symbol) {
        String url = String.format(minuteDataURL, Symbol.getSymbol(symbol,minuteDataURL), type);
        LOG.info("collect:" + url);
        List<Stock> stocks = Lists.newLinkedList();
        try {
            URL sinaFin = new URL(url);
            URLConnection data = sinaFin.openConnection();
            data.setConnectTimeout(60000);

            Scanner input = new Scanner(data.getInputStream());
            String record = input.nextLine();
            Pattern pattern = Pattern.compile("\\{([\\w|\"|,|:|\\s|.|-]*)\\}");
            Matcher matcher = pattern.matcher(record);
            Gson gson = new Gson();
            //start reading data
            while (matcher.find()) {
                Map<String,String> map = gson.fromJson(matcher.group(), Map.class);
                Date date = Utils.parseDate((String) map.get("day"), "yyyy-MM-dd HH:mm:ss");
                if(date.getTime() >= startDateLong){
                    Stock stock = new Stock();
                    stock.date = date;
                    stock.open = Double.parseDouble(map.get(Bytes.toString(OPEN)));
                    stock.high = Double.parseDouble(map.get(Bytes.toString(HIGH)));
                    stock.low = Double.parseDouble(map.get(Bytes.toString(LOW)));
                    stock.close = Double.parseDouble(map.get(Bytes.toString(CLOSE)));
                    stock.volume = Double.parseDouble(map.get(Bytes.toString(VOLUME)));

                    stock.symbol = symbol;
                    stocks.add(stock);
                }
            }
            input.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        StockListFetcher stockListFetcher = new StockListFetcher();
        List<Pair<String, String>> stockList = stockListFetcher.getStockList();

        ExecutorService service = Executors.newFixedThreadPool(10);
        final String startDate = "20150801";
        final String stopDate = "20150811";
        Stopwatch stopwatch = Stopwatch.createStarted();
        final CountDownLatch latch = new CountDownLatch(stockList.size());
        final Collecter collecter = new MinuteStockDataCollecter(startDate,stopDate,"5");
        for(final Pair<String,String> pair: stockList){
            service.execute(new Runnable() {
                @Override
                public void run() {
                    List<Stock> stocks = collecter.collect(pair.getVal());
//                    for(Stock stock:stocks){
//                        System.out.println(stock);
//                    }
                    stocks.size();
                    latch.countDown();
                }
            });
        }
        latch.await();
        System.out.println("ts:"+stopwatch.toString());
        service.shutdownNow();
    }
}