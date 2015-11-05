package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-4.
 */
public class DefaultTrader {
    protected static Logger LOG = LoggerFactory.getLogger(DefaultTrader.class);
    protected static String DEFAULT_ID = "cheetah"; //需要根Account帐户名称对应

    protected String id;
    protected Strategy strategy;
    protected DataCenter dataCenter;
    protected List<String> tradingStockList;
    //关注已经购买的股票
    protected Set<String> symbolSet = Sets.newConcurrentHashSet();
    protected ScheduledExecutorService executorService;
    protected int checkInterval = 60 * 1000;    //单位是毫秒
    protected volatile boolean isTrading;   //当前是否是交易时间
    protected volatile boolean isWorking = false;   //是否继续执行策略
    protected DefaultBroker broker;
    /**
     * key=symbol
     * 操作每个股票记录
     */
    protected Map<String,List<Execution>> operations = Maps.newConcurrentMap();


    public DefaultTrader(){
        this(DEFAULT_ID);
    }

    public DefaultTrader(String traderId){
        this.id = traderId;
        //TODO load hold stock status from db;
        executorService = Executors.newScheduledThreadPool(10);
        this.dataCenter = broker.getDataCenter();
        //TODO load condition or stock list from conf
        this.tradingStockList = Provider.tradingStockList();
    }

    public void start() {
        LOG.info(String.format("trader [%s] is working",id));
        while(isWorking){
            if(isTrading()){
                for(String symbol:tradingStockList){
                    if(isHold(symbol)){
                        if(strategy.isDroped(symbol)){

                        }
                    }
                    if(strategy.isPicked(symbol)){
                        double buy = strategy.buy(symbol);
                        Execution execution = new Execution(symbol,strategy.getTimeStamp(),buy,1000,"buy");
                        List<Execution> executionList = operations.get(symbol);
                        if(executionList ==null){
                            executionList = Lists.newArrayList();
                        }
                        executionList.add(execution);
                        operations.put(symbol, executionList);
                    }
                }
            }
            Sleeper.sleep(checkInterval);
        }
    }

    /**
     * 判断股票是否持有
     * @param symbol
     * @return
     */
    public boolean isHold(String symbol){
        return symbolSet.contains(symbol);
    }


    public boolean isTrading() {
        isTrading = Utils.isTradingTime();
        return isTrading;
    }

    public String getTraderId(){
        return id;
    }
}
