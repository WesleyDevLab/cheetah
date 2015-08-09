package com.zhaijiong.stock.datasource;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.Stock;
import com.zhaijiong.stock.common.Utils;
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
public class NetEaseDailyHistoryStockDataCollecter implements StockDataCollecter {
    private static final Logger LOG = LoggerFactory.getLogger(NetEaseDailyHistoryStockDataCollecter.class);

    public final String historyDataUrl = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    @Override
    public List<Stock> collect(String symbol, String start, String stop) {
        String url = String.format(historyDataUrl, Utils.netEaseSymbol(symbol), start, stop);
        LOG.info("collect:"+url);
        List<Stock> stocks = Lists.newLinkedList();
        try {
            URL netEaseFin = new URL(url);
            URLConnection data = netEaseFin.openConnection();
            data.setConnectTimeout(60000);
            Scanner input = new Scanner(data.getInputStream());
            if (input.hasNext()) { // skip line (header)
                input.nextLine();
            }

            //start reading data
            while (input.hasNextLine()) {
                String record = input.nextLine();
                String[] line = record.split(",");
                if (line.length == 15 && !record.contains("None")) {
                    try {
                        Stock stock = new Stock();

                        stock.date = Utils.parseDate(line[0]);
                        stock.symbol = line[1].replace("'", "");
                        stock.name = line[2];
                        stock.close = Utils.parseDouble(line[3]);
                        stock.high = Utils.parseDouble(line[4]);
                        stock.low = Utils.parseDouble(line[5]);
                        stock.open = Utils.parseDouble(line[6]);
                        stock.lastClose = Utils.parseDouble(line[7]);
                        stock.changeAmount = Utils.parseDouble(line[8]);
                        stock.change = Utils.parseDouble(line[9]);
                        stock.turnoverRate = Utils.parseDouble(line[10]);
                        stock.volume = Utils.parseDouble(line[11]);
                        stock.amount = Utils.parseDouble(line[12]);
                        stock.totalValue = Utils.parseDouble(line[13]);
                        stock.marketValue = Utils.parseDouble(line[14]);
                        stock.marketValue = Utils.formatDouble(Utils.parseDouble(line[14]));
                        stock.amplitude = Utils.formatDouble((stock.high - stock.low) / stock.lastClose);
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
}
