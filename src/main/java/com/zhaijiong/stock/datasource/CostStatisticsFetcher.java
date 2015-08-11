package com.zhaijiong.stock.datasource;

import com.google.common.collect.Maps;
import com.zhaijiong.stock.Symbol;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-11.
 */
public class CostStatisticsFetcher {
    //http://app.finance.ifeng.com/data/stock/tab_cccb.php?code=sh600376&begin_day=2015-08-04&end_day=2015-08-11
    private static String costStatisticsURL = "http://app.finance.ifeng.com/data/stock/tab_cccb.php?code=%s&begin_day=%s&end_day=%s";

    public static double getAvgCost(String symbol,String startDate,String stopDate){
        String url = String.format(costStatisticsURL, Symbol.getSymbol(symbol,costStatisticsURL),startDate,stopDate);
        System.out.println("url:"+url);
        try {
            Document doc = Jsoup.connect(url).get();
//            System.out.println(doc.html());
            Elements select = doc.select("table[class=lable_tab01] > tbody > tr");
            if(select.size()<=2){
                return 0;
            }
            String avg = select.get(select.size()-1).getElementsByTag("td").get(1).text();
            return Double.parseDouble(avg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Map<String,Double> getEveryDayAvgCost(String symbol,String startDate,String stopDate){
        Map<String,Double> results = Maps.newLinkedHashMap();
        String url = String.format(costStatisticsURL, Symbol.getSymbol(symbol,costStatisticsURL),startDate,stopDate);
        System.out.println("url:"+url);
        try {
            Document doc = Jsoup.connect(url).get();
//            System.out.println(doc.html());
            Elements select = doc.select("table[class=lable_tab01] > tbody > tr");
            if(select.size()<=2){
                return results;
            }
            for(int i=1;i<select.size()-1;i++){
                String date = select.get(i).getElementsByTag("td").get(0).text().replaceAll("-","");
                String avg = select.get(i).getElementsByTag("td").get(1).text();
                results.put(date,Double.parseDouble(avg));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static void main(String[] args) {
        double avgCost = CostStatisticsFetcher.getAvgCost("002104", "2015-08-01", "2015-08-12");
        System.out.println(avgCost);

        Map<String, Double> everyDayAvgCost = CostStatisticsFetcher.getEveryDayAvgCost("002104", "2015-08-12", "2015-08-13");
        for(Map.Entry<String,Double> entry:everyDayAvgCost.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }
}
