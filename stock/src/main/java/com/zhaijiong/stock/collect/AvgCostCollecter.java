package com.zhaijiong.stock.collect;

import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.Symbol;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * 获取股票每天的平均成本
 */
@Deprecated
public class AvgCostCollecter implements Collecter<String,Double>{
    private static final Logger LOG= LoggerFactory.getLogger(AvgCostCollecter.class);

    //http://app.finance.ifeng.com/data/stock/tab_cccb.php?code=sh600376&begin_day=2015-08-04&end_day=2015-08-11
    private static String costStatisticsURL = "http://app.finance.ifeng.com/data/stock/tab_cccb.php?code=%s&begin_day=%s&end_day=%s";

    public static double getAvgCost(String symbol,String startDate,String stopDate){
        String url = String.format(costStatisticsURL, Symbol.getSymbol(symbol,costStatisticsURL),startDate,stopDate);
        System.out.println("url:"+url);
        try {
            Document doc = Jsoup.connect(url).get();
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
        String url =String.format(costStatisticsURL, Symbol.getSymbol(symbol, costStatisticsURL), startDate, stopDate);
        System.out.println("url:"+url);
        try {
            Document doc = Jsoup.connect(url).get();
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
            LOG.error("fail to get avg cost from " + url);
        }
        return results;
    }

    @Override
    public Map<String, Double> collect(String symbol) {
        String now = Utils.getNow("yyyy-MM-dd");
        Map<String, Double> avgCost = AvgCostCollecter.getEveryDayAvgCost(symbol, now, now);
        return avgCost;
    }

    @Override
    public String getPath(String symbol) {
        String now = Utils.getNow("yyyy-MM-dd");
        return String.format(costStatisticsURL, Symbol.getSymbol(symbol,costStatisticsURL),now,now);
    }

    public static void main(String[] args) {
        double avgCost = AvgCostCollecter.getAvgCost("002104", "2015-08-01", "2015-08-12");
        System.out.println(avgCost);

        Map<String, Double> everyDayAvgCost = AvgCostCollecter.getEveryDayAvgCost("002104", "2015-08-12", "2015-08-13");
        for(Map.Entry<String,Double> entry:everyDayAvgCost.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

}
