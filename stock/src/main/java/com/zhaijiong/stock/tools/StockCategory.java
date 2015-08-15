package com.zhaijiong.stock.tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-16.
 */
public class StockCategory {
    private static final Logger LOG = LoggerFactory.getLogger(StockCategory.class);

    private static String tagsURL = "http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0";

    private static int RETRY_TIMES = 3;
    private static int SLEEP_INTERVAL_MS = 3000;

    //获取概念版块、行业版块、地域版块分类
    public static Map<String, List<String>> getMap() {
        Map<String, List<String>> tags = Maps.newHashMap();

        int retryTimes = 0;
        while (retryTimes < RETRY_TIMES) {

            try {
                Document document = Jsoup.connect(tagsURL).get();
                Elements elements = document.select("li[class=node-sub-sub]");
                for (Element element : elements) {
                    String html = new String(element.html().getBytes(Charset.forName("utf8")));
                    Elements items = element.select("li span[class=text]");
                    List<String> list = Lists.newArrayListWithCapacity(200);
                    for (Element item : items) {
                        String word = new String(item.text().getBytes(Charset.forName("utf8"))).replaceAll("_", "");
                        word = word.replaceAll("板块", "");
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
                return tags;
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
        LOG.info("概念:" + tags.get("概念板块").size() + ",行业:" + tags.get("行业板块").size() + ",地域:" + tags.get("地域板块").size());
        return tags;
    }

    public static void main(String[] args) {
        Map<String, List<String>> build = StockCategory.getMap();
        for (Map.Entry<String, List<String>> entry : build.entrySet()) {
            for (String val : entry.getValue()) {
                System.out.println(entry.getKey() + ":" + val);
            }
            System.out.println(entry.getKey() + ":" + entry.getValue().size());
        }
    }
}
