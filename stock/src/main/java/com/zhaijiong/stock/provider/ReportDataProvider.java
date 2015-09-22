package com.zhaijiong.stock.provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.download.Downloader;
import com.zhaijiong.stock.model.StockData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * date: 15-9-22.
 * 分析师研究报告数据，数据来源
 *  http://data.eastmoney.com/report/
 */
public class ReportDataProvider {

    /**
     * 个股研究报告地址
     * p=页数，一页200条
     */
    private static String STOCK_REPORT_URL = "http://datainterface.eastmoney.com//EM_DataCenter/js.aspx?type=SR&sty=GGSR&ps=200&p=%s&mkt=0&stat=0&cmd=2&rt=48097671";

    /**
     *
     * @param startDate 报告起始时间，格式:yyyyMMdd
     * @return
     */
    public static List<StockData> getStockReportData(String startDate){
        Date start = Utils.str2Date(startDate,"yyyyMMdd");

        List<StockData> stockDataList = Lists.newLinkedList();
        int pageCount = 1;
        while(pageCount<2){
            String url = String.format(STOCK_REPORT_URL, pageCount);
            String data = Downloader.download(url);
            Gson gson = new Gson();
            List<Map<String,Object>> list = gson.fromJson(data.substring(1, data.length() - 1), List.class);
            for(Map<String,Object> record:list){
                Date date = Utils.str2Date(String.valueOf(record.get("datetime")).replaceAll("T", " "), "yyyy-MM-dd HH:mm:ss");
                if(date.getTime() < start.getTime()){
                    break;
                }
                StockData stockData = new StockData(String.valueOf(record.get("secuFullCode")).substring(0,6));
                stockData.name = String.valueOf(record.get("secuName"));
                stockData.date = date;
                stockData.attr("上次评级",String.valueOf(record.get("sratingName")));
                stockData.attr("评级",String.valueOf(record.get("rate")));
                stockData.attr("评级变动",String.valueOf(record.get("change")));
                stockData.attr("title",String.valueOf(record.get("title")));
                stockData.attr("机构名称",String.valueOf(record.get("insName")));
                stockData.attr("机构评级",String.valueOf(record.get("insStar")));   //1-5，5是最好
                stockData.attr("author",String.valueOf(record.get("author")));

                fillRecord(record, stockData ,"jlrs","净利润");
                fillRecord(record, stockData ,"sys","每股收益");
                fillRecord(record, stockData ,"syls","市盈率");

                stockDataList.add(stockData);
            }
            pageCount++;
        }
        return stockDataList;
    }

    private static void fillRecord(Map<String, Object> record, StockData stockData,String recordKeyName,String stockAttrName) {
        List<String> list = (List<String>) record.get(recordKeyName);
        for(int i =0;i<list.size();i++){
            if(!Strings.isNullOrEmpty(list.get(i))){
                String year = Utils.getYear(i);
                stockData.attr(year + stockAttrName,list.get(i));
            }
        }
    }
}
