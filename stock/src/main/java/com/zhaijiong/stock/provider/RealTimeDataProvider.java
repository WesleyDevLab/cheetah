package com.zhaijiong.stock.provider;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhaijiong.stock.common.StockConstants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.download.Downloader;
import com.zhaijiong.stock.model.BoardType;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.model.StockMarketType;
import com.zhaijiong.stock.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi86@gmail.com
 * date: 15-8-30.
 */
public class RealTimeDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(RealTimeDataProvider.class);

    private static String realTimeDateURL = "http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=%s&_=%s";

    private static Map<String, List<String>> collect(String symbol) {
        String data = Downloader.download(getPath(symbol));
        if (Strings.isNullOrEmpty(data)) {
            LOG.error("fail to get real time data from " + getPath(symbol));
            return Maps.newHashMap();
        }
        data = data.substring(9, data.length() - 1);
        Gson gson = new Gson();
        Map<String, List<String>> map = gson.fromJson(data, Map.class);
        return map;
    }

    /**
     * 获取实时股票交易数据
     * @param symbol
     * @return
     */
    public static StockData get(String symbol){

        Map<String, List<String>> map = collect(symbol);

        if(map.size()==0){
            return null;
        }
        List<String> columns = map.get("Value");
        if (columns.size() != 50) {
            return null;
        }

        StockData stockData = new StockData();
        stockData.symbol = symbol;
        stockData.name = columns.get(2);
        stockData.stockMarketType = StockMarketType.getType(symbol);
        stockData.boardType = BoardType.getType(symbol);
        stockData.date = Utils.str2Date(columns.get(49), "yyyy-MM-dd HH:mm:ss");

        for (int i = 3; i < columns.size() - 1; i++) {
            if (getColumnName(i).equals("amount")) {
                stockData.put(getColumnName(i), Utils.getAmount(columns.get(i)));
            } else if (!getColumnName(i).equals("")) {
                if (Utils.isDouble(columns.get(i))) {
                    stockData.put(getColumnName(i), Double.parseDouble(columns.get(i)));
                }
            }
        }
        return stockData;
    }

    /**
     * 获取原始数据对应的列名称
     * @param i
     * @return
     */
    private static String getColumnName(int i) {
        return StockConstants.REALTIME.get(i);
    }

    public static String getPath(String symbol) {
        Date date = new Date();
        return String.format(realTimeDateURL, Symbol.getSymbol(symbol, realTimeDateURL), date.getTime());
    }
}
