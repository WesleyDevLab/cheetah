package com.zhaijiong.stock.tools;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.model.StockBlock;
import com.zhaijiong.stock.provider.Provider;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-21.
 */
public class StockPool {

    /**
     * 获取股票列表中交易中的股票列表
     * @param stockList
     * @return
     */
    public static List<String> tradingStock(List<String> stockList){
        return Provider.tradingStockList(stockList);
    }

    /**
     * 获取某个大分类下的版块名称
     * @param category  概念，行业，地域
     * @param name
     * @return
     */
    public static List<String> listByCategory(String category,String name){
        List<StockBlock> stockBlocks = StockCategory.getCategory().get(category);
        for(StockBlock stockBlock:stockBlocks){
            if(stockBlock.name.equals(name)){
                return tradingStock(stockBlock.symbolList);
            }
        }
        return Lists.newArrayList();
    }

    public static List<String> listByConditions(Conditions conditions){
        return Provider.tradingStockList(conditions);
    }

    public static List<String> marginTradingStock(List<String> stockList){
        return tradingStock(Provider.marginTradingStockList(stockList));
    }
}
