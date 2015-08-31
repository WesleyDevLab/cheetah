package com.zhaijiong.stock;

import com.zhaijiong.stock.download.AjaxDownloader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Test;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-31.
 */
public class TestSelenium {

    @Test
    public void test() {
        String url = "http://data.eastmoney.com/zjlx/dpzjlx.html";
        String s = AjaxDownloader.download(url);
        Element dt_1 = Jsoup.parse(s).getElementById("dt_1").getElementsByTag("tbody").first();
        System.out.println(dt_1);
    }

}
