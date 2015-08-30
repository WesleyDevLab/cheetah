package com.zhaijiong.stock.provider;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.download.Downloader;
import com.zhaijiong.stock.model.BoardType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.StockMarketType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-30.
 */
public class MoneyFlowDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MoneyFlowDataProvider.class);

    //第二个为6位随机数
    private static String moneyFlowURL = "http://hqchart.eastmoney.com/hq20/js/%s.js?%s";

    private static String moneyFlowHisURL = "http://data.eastmoney.com/zjlx/%s.html";

    public static StockData get(String symbol){
        Map<String, String> map = collect(symbol);

        StockData stockData = new StockData();
        stockData.symbol = symbol;
        stockData.stockMarketType = StockMarketType.getType(symbol);
        stockData.boardType = BoardType.getType(symbol);

        String data = map.get("data");
        if(Strings.isNullOrEmpty(data)){
            LOG.error("fail to get money flow data from "+symbol);
            return null;
        }
        String[] values = data.split(",");
        for(int i=0;i<values.length;i++){
            stockData.put(StockConstants.MONEYFLOW.get(i), java.lang.Double.parseDouble(values[i]));
        }
        return stockData;
    }

    public static List<StockData> get(String symbol,String startDate,String stopDate){
        List<String[]> list = collectHis(symbol);
        List<StockData> stockDataList = Lists.newLinkedList();
        for(String[] columns:list){
            StockData stockData = new StockData();
            stockData.symbol = symbol;
            stockData.stockMarketType = StockMarketType.getType(symbol);
            stockData.boardType = BoardType.getType(symbol);
            stockData.date = Utils.str2Date(columns[0],"yyyy-MM-dd");

            if(stockData.date.getTime() >= Utils.str2Date(startDate,"yyyyMMdd").getTime()
                    && stockData.date.getTime() <= Utils.str2Date(stopDate,"yyyyMMdd").getTime()){
                for(int i=1;i<columns.length;i++){
                    double val = 0;
                    if(columns[i].contains("%")){
                        val = Double.parseDouble(columns[i].replace("%",""));
                    }else{
                        val = Utils.getAmount(columns[i]);
                    }
                    stockData.put(StockConstants.MONEYFLOW_HIS.get(i),val);
                }
                stockDataList.add(stockData);
            }
        }
        return stockDataList;
    }

    private static List<String[]> collectHis(String symbol){
        String url = String.format(moneyFlowHisURL,symbol);
        String data = Downloader.downloadStr(url);
        Elements doc = Jsoup.parse(data).getElementById("dt_1").getElementsByTag("tbody").get(0).getElementsByTag("tr");

        List<String[]> stockDataList = Lists.newLinkedList();
        for(Element tr:doc){
            Elements tds = tr.getElementsByTag("td");
            String[] columnValues = new String[13];
            for(int i=0;i<13;i++){
                columnValues[i] = tds.get(i).text();
            }
            stockDataList.add(columnValues);
        }
        return Lists.reverse(stockDataList);
    }

    private static Map<String, String> collect(String symbol) {
        Map<String, String> map = Maps.newLinkedHashMap();
        String data = Downloader.downloadStr(getPath(symbol));
        Pattern pattern = Pattern.compile("(\\{.*})");
        Matcher matcher = pattern.matcher(data);
        if(matcher.find()){
            Gson gson = new Gson();
            map.putAll(gson.fromJson(matcher.group(), Map.class));
        }
        return map;
    }

    public static String getPath(String symbol) {
        Random random = new Random();
        int i = random.nextInt(999999);
        return String.format(moneyFlowURL,symbol,i);
    }

    public static void main(String[] args) {
        List<StockData> stockDataList = MoneyFlowDataProvider.get("600376", "20150801", "20150830");
        for(StockData stockData :stockDataList){
            System.out.println(stockData);
            Utils.printMap(stockData);
        }
    }
}
