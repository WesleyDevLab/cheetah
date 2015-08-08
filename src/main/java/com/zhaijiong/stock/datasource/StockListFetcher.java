package com.zhaijiong.stock.datasource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by eryk on 15-4-8.
 */
public class StockListFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(StockListFetcher.class);

    private String tagsURL = "http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0";
    private String stockURL = "http://quote.eastmoney.com/stocklist.html";
    Map<String, List<String>> tags = Maps.newHashMap();

    //获取概念版块、行业版块、地域版块分类
    Map<String, List<String>> getStockCategory() throws IOException {
        Connection connect = Jsoup.connect(tagsURL);
        Document document = connect.get();
        Elements elements = document.select("li[class=node-sub-sub]");
        for (Element element : elements) {
            String html = new String(element.html().getBytes(Charset.forName("utf8")));
            Elements items = element.select("li span[class=text]");
            List<String> list = Lists.newArrayListWithCapacity(200);
            for (Element item : items) {
                String word = new String(item.text().getBytes(Charset.forName("utf8"))).replaceAll("_","");
                word = word.replaceAll("板块","");
                list.add(word);
            }
            if (html.contains("概念板块")) {
                tags.put("概念板块", list);
            } else if (html.contains("行业板块")) {
                tags.put("行业板块", list);
            } else if (html.contains("地域板块")) {
                tags.put("地域板块", list);
            }
        }
        LOG.info("概念:" + tags.get("概念板块").size() + ",行业:" + tags.get("行业板块").size() + ",地域:" + tags.get("地域板块").size());
        return tags;
    }

    //获取沪深股票（名称：代码）
    public List<Pair<String,String>> getStockList() throws IOException {
        int zxb = 0;
        int sh = 0;
        int sz = 0;
        int cyb = 0;
        int other = 0;

        List<Pair<String,String>> stockList = Lists.newArrayList();
        Document doc = Jsoup.connect(stockURL).get();
        Elements stocks = doc.select("div[id=quotesearch] li a");
        for(Element stock :stocks){
            String url = stock.attr("href");
            if(url.contains("sh600")){
                ++sh;
            }else if(url.contains("sz000")){
                ++sz;
            }else if(url.contains("sz002")){
                ++zxb;
            }else if(url.contains("sz300")){
                ++cyb;
            }else {
                ++other;
                continue;
            }
            String[] stockArr = stock.text().split("\\(");
            stockList.add(new Pair(stockArr[0], stockArr[1].replaceAll("\\)","")));
        }
        LOG.info("600:"+sh+",000:"+sz+",002:"+zxb+",300:"+cyb+",other:"+other);
        LOG.info("total:"+(sh+sz+zxb+cyb));
        return stockList;
    }

    public static void main(String[] args) throws IOException {
        StockListFetcher fetcher = new StockListFetcher();
        List<Pair<String, String>> stockList = fetcher.getStockList();
        for(Pair<String,String> stock:stockList){
            System.out.println(stock);
        }
        System.out.println("stock:"+stockList.size());

        Map<String, List<String>> build = fetcher.getStockCategory();
        for(Map.Entry<String,List<String>> entry:build.entrySet()){
            for(String val:entry.getValue()){
                System.out.println(entry.getKey()+":"+val);
            }
            System.out.println(entry.getKey()+":"+entry.getValue().size());
        }
    }

}
