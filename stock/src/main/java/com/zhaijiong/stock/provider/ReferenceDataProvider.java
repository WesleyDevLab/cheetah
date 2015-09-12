package com.zhaijiong.stock.provider;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.download.BasicDownloader;
import com.zhaijiong.stock.model.StockData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-12.
 */
public class ReferenceDataProvider {

    /**
     * 分配预案数据源
     */
    private static String FPYA = "http://quotes.money.163.com/data/caibao/fpyg.html?reportdate=%s&sort=declaredate&order=desc&page=%s";

    /**
     * 获取分配预案数据
     *
     * @param year 年份
     * @return
     */
    public static List<StockData> getFPYA(String year) {
        Integer pageCount = getPageCount(year);
        List<StockData> stockDataList = Lists.newLinkedList();
        for (int i = 0; i < pageCount; i++) {
            String url = String.format(FPYA, year, i);
            stockDataList.addAll(collectFPYA(url));
        }
        return stockDataList;
    }

    private static Integer getPageCount(String year) {
        String url = String.format(FPYA, year, 0);
        String data = BasicDownloader.download(url);
        Elements pageURLs = Jsoup.parse(data).select("div[class=mod_pages] a");
        Element lastPageURL = pageURLs.get(pageURLs.size() - 2);
        int pageCount = Integer.parseInt(lastPageURL.text());
        return pageCount;
    }

    public static List<StockData> collectFPYA(String url) {
        List<StockData> stockDataList = Lists.newLinkedList();

        String data = BasicDownloader.download(url);
        Elements table = Jsoup.parse(data).getElementById("plate_performance").getElementsByTag("tbody").get(0).getElementsByTag("tr");
        for(Element tr:table){
            Elements td = tr.getElementsByTag("td");
            StockData stockData = new StockData(td.get(1).text());
            stockData.name = td.get(2).text();
            stockData.date = Utils.str2Date(td.get(5).text(),"yyyy-MM-dd");

            stockData.put("dividend",getDividend(td.get(4).text().trim()));//分红
            stockData.put("shares",getShares(td.get(4).text().trim()));//转增和送股
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    /**
     * 获取分红数据
     * @param plan
     * @return
     */
    private static Double getDividend(String plan) {
        Pattern pattern = Pattern.compile("分红(.*?)元");
        Matcher matcher = pattern.matcher(plan);
        if(matcher.find()){
            String group = matcher.group(1);
            return Double.parseDouble(group);
        }
        return 0d;
    }

    /**
     * 获取转增和送股数
     * @return
     */
    private static Double getShares(String plan){
        Pattern pattern = Pattern.compile("[增|股](.*?)股");
        Matcher matcher = pattern.matcher(plan);
        Double count = 0d;
        while(matcher.find()){
            String group = matcher.group(1);
            count += Double.parseDouble(group);
        }
        return count;
    }

    public static void main(String[] args) {
        List<StockData> fpya = ReferenceDataProvider.getFPYA("2014");
        fpya.forEach((StockData stockData) ->
            System.out.println(stockData.toString() + "\t" +
                    stockData.get("dividend") + "\t" +
                    stockData.get("shares")
            ));
    }
}
