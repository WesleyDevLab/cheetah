package com.zhaijiong.stock.collect;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.zhaijiong.stock.download.Downloader;
import com.zhaijiong.stock.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-14.
 */
public class RealtimeDataCollecter implements Collecter<String, List<String>> {
    private static final Logger LOG = LoggerFactory.getLogger(RealtimeDataCollecter.class);

    private String realTimeDateURL = "http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=%s&_=%s";

    @Override
    public Map<String, List<String>> collect(String symbol) {
        String data = Downloader.download(getPath(symbol));
        if (Strings.isNullOrEmpty(data)) {
            LOG.error("fail to get real time data from " + getPath(symbol));
        }
        data = data.substring(9, data.length() - 1);
        Gson gson = new Gson();
        Map<String, List<String>> map = gson.fromJson(data, Map.class);
        return map;
    }

    @Override
    public String getPath(String symbol) {
        Date date = new Date();
        return String.format(realTimeDateURL, Symbol.getSymbol(symbol, realTimeDateURL), date.getTime());
    }

    public static void main(String[] args) throws IOException {
        RealtimeDataCollecter collecter = new RealtimeDataCollecter();
        Map<String, List<String>> data = collecter.collect("600376");
        List<String> list = data.get("Value");
        for (String value : list) {
            System.out.println(value);
        }
        System.out.println("list:" + list.size());
    }

}
