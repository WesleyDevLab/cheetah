package com.zhaijiong.stock.tools;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhaijiong.stock.download.Downloader;
import com.zhaijiong.stock.model.StockBlock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-16.
 */
public class StockCategory {
    private static final Logger LOG = LoggerFactory.getLogger(StockCategory.class);

    private static String tagsURL = "http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0";

    private static String blockBaseURL = "http://quote.eastmoney.com/center/";

    private static String blockStockListURL = "http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/index.aspx?type=s&sortType=C&sortRule=-1&pageSize=500&page=1&style=%s&token=44c9d251add88e27b65ed86506f6e5da";

    private static int RETRY_TIMES = 3;
    private static int SLEEP_INTERVAL_MS = 3000;

    //获取概念版块、行业版块、地域版块分类
    //key:版块分类：概念，行业，地域
    //value：List<StockBlock>具体版块信息
    public static Map<String,List<StockBlock>> getCategory() {
        Map<String,List<StockBlock>> blockMap = Maps.newHashMap();

        int retryTimes = 0;
        while (retryTimes < RETRY_TIMES) {

            try {
                Document document = Jsoup.connect(tagsURL).get();
                Elements elements = document.select("li[class=node-sub-sub]");
                for (Element element : elements) {
                    String html = new String(element.html().getBytes(Charset.forName("utf8")));
                    Elements items = element.select("ul li");

                    String type ="";
                    if (html.contains("概念板块")) {
                        type = "概念";
                    } else if (html.contains("行业板块")) {
                        type = "行业";
                    } else if (html.contains("地域板块")) {
                        type = "地域";
                    }

                    List<StockBlock> list = Lists.newArrayListWithCapacity(200);
                    for (Element item : items) {
                        StockBlock stockBlock = new StockBlock();
                        stockBlock.name = new String(item.select("span[class=text]").text().getBytes(Charset.forName("utf8")));
                        stockBlock.url = blockBaseURL+item.select("a").attr("href");
                        String _id = CharMatcher.DIGIT.retainFrom(stockBlock.url);
                        stockBlock.id = _id.substring(0,_id.length()-2);
                        stockBlock.symbolList.addAll(getBlockStockList(stockBlock.id));
                        stockBlock.type = type;
                        list.add(stockBlock);
                    }
                    blockMap.put(type,list);

                }
                return blockMap;
            } catch (IOException e) {
                LOG.error("fail to get stock list",e);
                retryTimes++;
                try {
                    Thread.sleep(SLEEP_INTERVAL_MS);
                } catch (InterruptedException e1) {
                    LOG.error("fail to sleep "+ SLEEP_INTERVAL_MS + "ms");
                }
            }
        }
        LOG.info("概念:" + blockMap.get("概念板块").size() + ",行业:" + blockMap.get("行业板块").size() + ",地域:" + blockMap.get("地域板块").size());
        return blockMap;
    }

    private static List<String> getBlockStockList(String id) {
        String url = String.format(blockStockListURL,id);
        String data = Downloader.download(url);
        Pattern pattern = Pattern.compile("(\\[.*\\])");
        Matcher matcher = pattern.matcher(data);
        if(matcher.find()){
            List<String> blockStockList = Lists.newLinkedList();
            String group = matcher.group();
            List<String> lines = new Gson().fromJson(group,List.class);
            for(String line:lines){
                String[] fields = line.split(",");
                blockStockList.add(fields[1]);
            }
            return blockStockList;
        }
        return Lists.newLinkedList();
    }

    public static void main(String[] args) {
        Map<String, List<StockBlock>> stockBlocks = StockCategory.getCategory();
        for (Map.Entry<String, List<StockBlock>> entry : stockBlocks.entrySet()) {
            for (StockBlock stockBlock : entry.getValue()) {
                System.out.println(entry.getKey() + ":" + stockBlock);
                List<String> symbolList = stockBlock.symbolList;
                for(String symbol:symbolList){
                    System.out.println(symbol);
                }
            }
            System.out.println(entry.getKey() + ":" + entry.getValue().size());
        }
    }
}
