package com.zhaijiong.stock.datasource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.Stock;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-10.
 */
public class BasicInfoFetcher{

    private static   String basicInfoURL = "http://tudata.oss-cn-beijing.aliyuncs.com/all.csv";

//    code,代码
//    name,名称
//    industry,所属行业
//    area,地区
//    pe,市盈率
//    outstanding,流通股本
//    totals,总股本(万)
//    totalAssets,总资产(万)
//    liquidAssets,流动资产
//    fixedAssets,固定资产
//    reserved,公积金
//    reservedPerShare,每股公积金
//    eps,每股收益
//    bvps,每股净资
//    pb,市净率
//    timeToMarket,上市日期

    public static List<Map<String,String>> getStockBasicInfo() throws IOException {
        URL sinaFin = new URL(basicInfoURL);
        URLConnection data = sinaFin.openConnection();
        data.setConnectTimeout(60000);
        List<Map<String,String>> stocks = Lists.newArrayList();
        Scanner input = new Scanner(data.getInputStream());
        while (input.hasNextLine()) {
            String[] columns = input.nextLine().split(",");
            Map<String,String> stock = Maps.newLinkedHashMap();
            stock.put("symbol",columns[0]);
            stock.put("name",columns[1]);
            stock.put("industry",columns[2]);
            stock.put("area",columns[3]);
            stock.put("pe",columns[4]);
            stock.put("outstanding",columns[5]);
            stock.put("totals",columns[6]);
            stock.put("totalAssets",columns[7]);
            stock.put("liquidAssets",columns[8]);
            stock.put("fixedAssets",columns[9]);
            stock.put("reserved",columns[10]);
            stock.put("reservedPerShare",columns[11]);
            stock.put("eps",columns[12]);
            stock.put("bvps",columns[13]);
            stock.put("pb",columns[14]);
            stock.put("timeToMarket",columns[15]);
            stocks.add(stock);
        }
        return stocks;
    }

    public static void main(String[] args) throws IOException {
        List<Map<String,String>> stocks = BasicInfoFetcher.getStockBasicInfo();
        for(Map<String,String> stock:stocks){
            System.out.println(stock.get("symbol"));
            System.out.println(stock.get("totals"));
            System.out.println(stock.get("liquidAssets"));
        }
    }
}
