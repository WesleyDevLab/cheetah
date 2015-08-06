//package com.zhaijiong.stock.datasource;
//
//import com.google.common.collect.Lists;
//import com.zhaijiong.stock.Stock;
//import com.zhaijiong.stock.Utils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.List;
//import java.util.Scanner;
//
///**
// * Created by eryk on 2015/7/4.
// */
//public class YahooDailyHistoryStockDataCollector implements StockDataCollecter {
//    private static final Logger LOG = LoggerFactory.getLogger(YahooDailyHistoryStockDataCollector.class);
//
//    public final String realDataUrl = "http://finance.yahoo.com/d/quotes.csv?s=%s";
//
//    public final String historyDataUrl = "http://ichart.yahoo.com/table.csv?s=%s&a=int&b=int&c=int&d=int&e=int&f=int&g=d&ignore=.csv";
//
//    @Override
//    public List<Stock> collect(String symbol) {
//        String url = String.format(historyDataUrl,Utils.yahooSymbol(symbol));
//        List<Stock> stocks = Lists.newLinkedList();
//        try {
//            URL yahoofin = new URL(url);
//            URLConnection data = yahoofin.openConnection();
//            data.setConnectTimeout(60000);
//            Scanner input = new Scanner(data.getInputStream());
//            if (input.hasNext()) { // skip line (header)
//                input.nextLine();
//            }
//
//            //start reading data
//            while (input.hasNextLine()) {
//                String[] line = input.nextLine().split(",");
//                if(line.length == 7){
////                    Stock stock = new Stock();
////                    stock.setSymbol(symbol);
////                    stock.setDate(Utils.parseDate(line[0]));
////                    stock.setOpen(Utils.parseDouble(line[1]));
////                    stock.setHigh(Utils.parseDouble(line[2]));
////                    stock.setLow(Utils.parseDouble(line[3]));
////                    stock.setClose(Utils.parseDouble(line[4]));
////                    stock.setVolume(Utils.parseLong(line[5]) / 100);
////                    stock.setAdjClose(Utils.parseDouble(line[6]));
////                    stocks.add(stock);
//                }else{
//                    LOG.warn(String.format("stock %s convert error",symbol));
//                }
//            }
//
//        } catch (Exception e) {
//            LOG.error(String.format("stock %s collect error",symbol),e);
//        }
//        return stocks;
//    }
//}
