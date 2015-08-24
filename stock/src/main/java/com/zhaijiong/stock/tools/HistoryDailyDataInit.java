package com.zhaijiong.stock.tools;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-4.
 */
public class HistoryDailyDataInit {
    private static final Logger LOG = LoggerFactory.getLogger(HistoryDailyDataInit.class);

    public final String dailyDataUrl = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    public List<StockData> collect(String symbol,String startDate, String stopDate) {
        String url = String.format(dailyDataUrl, Symbol.getSymbol(symbol,dailyDataUrl), startDate, stopDate);
        LOG.info("collect:"+url);
        List<StockData> stocks = Lists.newLinkedList();
        try {
            URL netEaseFin = new URL(url);
            URLConnection data = netEaseFin.openConnection();
            data.setConnectTimeout(60000);
            Scanner input = new Scanner(data.getInputStream());
            if (input.hasNext()) { // skip line (header)
                input.nextLine();
            }

            while (input.hasNextLine()) {
                String record = input.nextLine();
                String[] line = record.split(",");
                if (line.length == 15 && !record.contains("None")) {
                    try {
                        StockData stock = new StockData();

                        stock.date = Utils.parseDate(line[0],Constants.NETEASE_DATE_STYLE);
                        stock.symbol = line[1].replace("'", "");
                        stock.name = line[2];
                        stock.put("close",Utils.parseDouble(line[3]));
                        stock.put("high",Utils.parseDouble(line[4]));
                        stock.put("low",Utils.parseDouble(line[5]));
                        stock.put("open",Utils.parseDouble(line[6]));
                        stock.put("lastClose",Utils.parseDouble(line[7]));
                        stock.put("changeAmount",Utils.parseDouble(line[8]));
                        stock.put("change",Utils.parseDouble(line[9]));
                        stock.put("turnoverRate",Utils.parseDouble(line[10]));
                        stock.put("volume",Utils.parseDouble(line[11]));
                        stock.put("amount",Utils.parseDouble(line[12]));
                        stock.put("totalValue",Utils.parseDouble(line[13]));
                        stock.put("marketValue",Utils.formatDouble(Utils.parseDouble(line[14])));
                        stock.put("amplitude",Utils.formatDouble((stock.get("high") - stock.get("low")) / stock.get("lastClose")));
                        stocks.add(stock);
                    } catch (Exception e) {
                        LOG.warn(String.format("stock %s convert error", symbol) + record);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error(String.format("stock %s collect error", symbol), e);
        }
        return stocks;
    }

    public static void main(String[] args) {
        HistoryDailyDataInit dailyDataInit = new HistoryDailyDataInit();
        List<String> symbols = StockMap.getList();
        for(String symbol:symbols){
            List<StockData> collect = dailyDataInit.collect(symbol, "20150823", "20150825");
            Context context= new Context();
            StockDB stockDB = new StockDB(context);
            stockDB.saveStockDailyData(collect);
        }
    }
}
