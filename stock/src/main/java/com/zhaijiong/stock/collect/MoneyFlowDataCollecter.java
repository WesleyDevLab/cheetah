package com.zhaijiong.stock.collect;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhaijiong.stock.download.Downloader;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-18.
 */
public class MoneyFlowDataCollecter implements Collecter<String,String>{
    //第二个为6位随机数
    private String moneyFlowURL = "http://hqchart.eastmoney.com/hq20/js/%s.js?%s";

    @Override
    public Map<String, String> collect(String symbol) {
        Map<String, String> map = Maps.newLinkedHashMap();
        String data = Downloader.download(getPath(symbol));
        Pattern pattern = Pattern.compile("(\\{.*})");
        Matcher matcher = pattern.matcher(data);
        if(matcher.find()){
            Gson gson = new Gson();
            map.putAll(gson.fromJson(matcher.group(), Map.class));
        }
        return map;
    }

    @Override
    public String getPath(String symbol) {
        Random random = new Random();
        int i = random.nextInt(999999);
        return String.format(moneyFlowURL,symbol,i);
    }

    public static void main(String[] args) {
        MoneyFlowDataCollecter collecter = new MoneyFlowDataCollecter();
        Map<String, String> collect = collecter.collect("600376");
    }
}
