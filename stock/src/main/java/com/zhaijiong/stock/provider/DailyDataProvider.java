package com.zhaijiong.stock.provider;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.DateRange;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.download.Downloader;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zhaijiong.stock.common.StockConstants.*;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-4.
 */
public class DailyDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DailyDataProvider.class);

    public static final String dailyDataUrl = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    public static List<StockData> get(String symbol, String startDate, String stopDate) {
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
        return String.format(dailyDataUrl, Symbol.getSymbol(symbol,dailyDataUrl), startDate, stopDate);
    }

    public static void main(String[] args) {
        DateRange range = DateRange.getRange(10);
        List<StockData> stockDataList = DailyDataProvider.get("600376", range.start(), range.stop());
        stockDataList.forEach(stockData -> System.out.println(stockData));
    }
}
