package com.zhaijiong.stock.tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.model.StockBlock;
import com.zhaijiong.stock.provider.Provider;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static com.zhaijiong.stock.common.StockConstants.*;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-21.
 */
public class StockPool {

    public static Map<String,List<String>> stockPool = Maps.newConcurrentMap();

    public StockPool(){}

    public synchronized boolean add(String name,List<String> stockList){
        if(stockPool.containsKey(name)){
            return false;
        }else{
            stockPool.put(name,stockList);
        }
        return true;
    }

    public List<String> get(String key){
        return stockPool.get(key);
    }

    public int size(){
        return stockPool.size();
    }

    @PostConstruct
    public static synchronized void build(){
        if(stockPool.size()==0){
            stockPool.put("trading",Provider.tradingStockList());
            stockPool.put("margin",Provider.marginTradingStockList());
            Conditions conditions = new Conditions();
            conditions.addCondition(CLOSE, Conditions.Operation.LT, 20d);
            conditions.addCondition(PE, Conditions.Operation.LT, 200d);
            conditions.addCondition(MARKET_VALUE, Conditions.Operation.LT, 100d);
            stockPool.put("small",listByConditions(conditions));
        }
    }

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
