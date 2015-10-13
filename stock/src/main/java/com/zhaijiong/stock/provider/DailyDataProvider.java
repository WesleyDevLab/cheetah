package com.zhaijiong.stock.provider;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhaijiong.stock.common.*;
import com.zhaijiong.stock.download.Downloader;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Symbol;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.zhaijiong.stock.common.StockConstants.*;

/**
 * author: xuqi.xq
 * date: 15-8-4.
 * 注意：复权只对开盘价，收盘价，最高价，最低价复权
 * 昨日收盘价，
 */
public class DailyDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DailyDataProvider.class);

    public static final String DAILY_DATA_URL = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    public static final String DAILY_PRICE_HFQ_URl = "http://vip.stock.finance.sina.com.cn/api/json.php/BasicStockSrv.getStockFuQuanData?symbol=%s&type=hfq";

    public static final String DAILY_HFQ_URL = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/%s.phtml"; //?year=%s&jidu=%s

    /**
     * 参数1：6位代码
     * year：年份
     * jidu：季度，1，2，3，4
     */
    public static final String DAILY_HFQ_PARAM_URL = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/%s.phtml?year=%s&jidu=%s";

    /**
     * 获取前复权数据
     * @param symbol
     * @param startDate
     * @param stopDate
     * @return
     */
    private static Map<String,StockData> qfqData(String symbol, String startDate, String stopDate){
        Map<String,StockData> stockDataMap = Maps.newHashMap();

        String hfqURL = String.format(DAILY_HFQ_URL,symbol);
        String data = Downloader.download(hfqURL,"gb2312");
        Elements select = Jsoup.parse(data).getElementById("con02-4").getElementsByTag("select").get(0).getElementsByTag("option");
        List<String> pages = Lists.newArrayListWithCapacity(50);
        for(Element option:select){
            pages.add(option.text()+""+"1231");
            pages.add(option.text()+""+"0930");
            pages.add(option.text()+""+"0630");
            pages.add(option.text()+""+"0331");
        }
        Collections.reverse(pages);

        for(String page:pages){
            Date date = Utils.str2Date(page,"yyyyMMdd");
            //TODO date < stop
            if(date.getTime() > Utils.str2Date(startDate,"yyyyMMdd").getTime() /*&& date.getTime() < Utils.str2Date(stopDate,"yyyyMMdd").getTime()*/){
                String url = String.format(DAILY_HFQ_PARAM_URL,symbol,Utils.formatDate(date,"yyyy"),getQuarter(date));
                data = Downloader.download(url,"gb2312");
                Elements tr = Jsoup.parse(data).getElementById("FundHoldSharesTable").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                for(int i = 1;i<tr.size();i++){
                    Elements td = tr.get(i).getElementsByTag("td");
                    Date stockDate = Utils.str2Date(td.get(0).text(),"yyyy-MM-dd");
                    if(stockDate.getTime()>= Utils.str2Date(startDate,"yyyyMMdd").getTime() && stockDate.getTime() <= Utils.str2Date(stopDate,"yyyyMMdd").getTime()){
                        StockData stockData = new StockData(symbol);

                        stockData.date = Utils.str2Date(td.get(0).text(),"yyyy-MM-dd");
                        stockData.put(StockConstants.OPEN,Double.parseDouble(td.get(1).text()));
                        stockData.put(StockConstants.HIGH,Double.parseDouble(td.get(2).text()));
                        stockData.put(CLOSE,Double.parseDouble(td.get(3).text()));
                        stockData.put(StockConstants.LOW,Double.parseDouble(td.get(4).text()));
                        stockData.put(StockConstants.VOLUME,Double.parseDouble(td.get(5).text())/100);  //单位：手
                        stockData.put(StockConstants.AMOUNT,Double.parseDouble(td.get(6).text())/10000);//单位：万
                        stockData.put(StockConstants.FACTOR,Double.parseDouble(td.get(7).text()));
                        stockDataMap.put(td.get(0).text().replaceAll("-", ""), stockData);
                    }
                }
            }
        }
        return stockDataMap;
    }

    /**
     * 根据时间获取季度
     * @param date
     * @return
     */
    public static int getQuarter(Date date){
        return (date.getMonth()/3)+1;
    }

    private static List<StockData> getDailyDataWithOutFQ(String symbol, String startDate, String stopDate) {
        String url = getPath(symbol,startDate,stopDate);
        List<StockData> stocks = Lists.newLinkedList();
        try {
            String data = Downloader.download(url, "gb2312");
            String[] lines = data.split("\n");

            for(int i=1;i<lines.length;i++){    //第一行是标题，跳过
                String[] line = lines[i].split(",");
                if (line.length == 15 && !lines[i].contains("None")) {
                    try {
                        StockData stock = new StockData(line[1].replace("'", ""));

                        stock.date = Utils.str2Date(line[0], Constants.NETEASE_DATE_STYLE);
                        stock.name = line[2];
                        for(int j = 0 ;j<DAILY.size()-1;j++){
                            stock.put(DAILY.get(j),Utils.str2Double(line[j+3]));
                        }
                        stock.put("amplitude",Utils.formatDouble((stock.get("high") - stock.get("low")) / stock.get("lastClose")));
                        changeUnit(stock);
                        stocks.add(stock);
                    } catch (Exception e) {
                        LOG.warn(String.format("stock %s convert error", symbol) + lines[i]);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error(String.format("stock %s collect error", symbol), e);
        }
        //按照时间从最早到最新
        return Lists.reverse(stocks);
    }

    public static List<StockData> get(String symbol, String startDate, String stopDate){
        Map<String,StockData> stockDatas = qfqData(symbol, startDate, stopDate);

        List<StockData> stockDataList = getDailyDataWithOutFQ(symbol, startDate, stopDate);
//        String date = Utils.formatDate(stockDataList.get(stockDataList.size() - 1).date, "yyyyMMdd");
        ArrayList<String> dateList = Lists.newArrayList(stockDatas.keySet());
        Collections.sort(dateList);
        String date = dateList.get(dateList.size()-1);
        double factor = stockDatas.get(date).get(StockConstants.FACTOR);//复权因子
        for(int i =0;i<stockDataList.size();i++){
            StockData stockData = stockDataList.get(i);
            date = Utils.formatDate(stockData.date,"yyyyMMdd");
            StockData stockDataFQ = stockDatas.get(date);
            if(stockDataFQ!=null){
                stockData.put(OPEN,Utils.formatDouble(stockDataFQ.get(OPEN)/factor));
                stockData.put(CLOSE,Utils.formatDouble(stockDataFQ.get(CLOSE)/factor));
                stockData.put(HIGH,Utils.formatDouble(stockDataFQ.get(HIGH)/factor));
                stockData.put(LOW,Utils.formatDouble(stockDataFQ.get(LOW)/factor));
                if(i>0){
                    stockData.put(LAST_CLOSE,stockDataList.get(i-1).get(CLOSE));
                }
            }else{
                //有时daily数据已经更新到最新，而复权数据还没有更新，这时去掉daily最新日期的数据，以复权数据源的日期数据为准
                stockDataList.remove(i);
            }
        }
        return stockDataList;
    }

    private static void changeUnit(StockData stockData) {
        stockData.put(VOLUME,stockData.get(VOLUME)/100);    //成交量,单位：手
        stockData.put(AMOUNT,stockData.get(AMOUNT)/10000);  //成交金额,单位：万
        stockData.put(TOTAL_VALUE,stockData.get(TOTAL_VALUE)/100000000);    //总市值,单位:亿
        stockData.put(MARKET_VALUE, stockData.get(MARKET_VALUE) / 100000000);   //流通市值,单位:亿
    }

    /**
     *
     * @param symbol
     * @param startDate yyyyMMdd
     * @param stopDate  yyyyMMdd
     * @return
     */
    public static String getPath(String symbol,String startDate,String stopDate) {
        return String.format(DAILY_DATA_URL, Symbol.getSymbol(symbol, DAILY_DATA_URL), startDate, stopDate);
    }

    public static void main(String[] args) {
        DateRange range = DateRange.getRange(5);

        Map<String, StockData> stockDataList1 = DailyDataProvider.qfqData("000008", range.start(), range.stop());
        for(Map.Entry<String,StockData> stockDataEntry:stockDataList1.entrySet()){
            System.out.println(stockDataEntry.getKey()+":"+stockDataEntry.getValue());
        }
    }
}
