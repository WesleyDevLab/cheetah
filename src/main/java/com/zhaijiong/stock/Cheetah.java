package com.zhaijiong.stock;

import com.zhaijiong.stock.dao.StockDAO;
import com.zhaijiong.stock.datasource.NetEaseDailyHistoryStockDataCollecter;
import com.zhaijiong.stock.datasource.StockListFether;

import java.io.IOException;
import java.util.List;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-5.
 */
public class Cheetah {
    public static void main(String[] args) throws IOException {
        Context context = new Context();

        StockListFether stockListFether = new StockListFether();
        List<Pair<String, String>> stockList = stockListFether.getStockList();
        for (Pair<String, String> stock : stockList) {
            NetEaseDailyHistoryStockDataCollecter collecter = new NetEaseDailyHistoryStockDataCollecter();
            List<Stock> stocks = collecter.collect(stock.getVal(), args[0], args[1]);

            StockDAO stockDAO = new StockDAO("stocks_day", context);
            stockDAO.save(stocks);
        }
        context.close();
    }
}
