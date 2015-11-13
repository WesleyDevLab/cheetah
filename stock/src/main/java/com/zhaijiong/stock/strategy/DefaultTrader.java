package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
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
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTrader.class);
    private static String DEFAULT_TRADER_ID = "cheetah"; //需要根Account帐户名称对应

    protected final String id;
    private Context context;
    private Strategy strategy;
    private DataCenter dataCenter;
    private List<String> tradingStockList;
    //关注已经购买的股票
    private Set<String> symbolSet = Sets.newConcurrentHashSet();
    private ScheduledExecutorService executorService;
    private int checkInterval = 60 * 1000;    //单位是毫秒
    private volatile boolean isTrading;   //当前是否是交易时间
    private volatile boolean isWorking = false;   //是否继续执行策略
    private DefaultBroker broker;
    /**
     * key=symbol
     * 操作每个股票记录
     */
    protected Map<String,List<Execution>> operations = Maps.newConcurrentMap();

    public DefaultTrader(Context context){
        this.context = context;
        id = context.getStr(Constants.TRADER_ACCOUNT_ID, DEFAULT_TRADER_ID);
    }
    //TODO load hold stock status from db;
    public void init(DataCenter dataCenter,DefaultBroker broker){
        this.dataCenter = dataCenter;
        this.broker = broker;
        executorService = Executors.newScheduledThreadPool(10);
        //TODO load condition or stock list from conf
        this.tradingStockList = Provider.tradingStockList();
    }

    public void start() {
        //将traderID注册到broker，并在broker中创建Account
        broker.registerAccount(getTraderId());
        LOG.info(String.format("trader [%s] is working", id));
        while(isWorking){
            if(isTrading()){
                for(String symbol:tradingStockList){
                    if(isHold(symbol)){
                        if(strategy.isSell(symbol)){
                            double price = strategy.sell(symbol);
                            //TODO 数据中心获取ts和更新策略
                            Execution execution = new Execution(symbol, dataCenter.getTimeStamp(),price, Execution.Type.SELL);
                            saveExecution(symbol, execution);
                            Order order = new Order(getTraderId(),execution);
                            broker.addOrder(order);
                            symbolSet.remove(symbol);   //从已购买股票列表中删除，如果加入买卖手数，这里需要计算是否已经清仓
                        }
                        continue;
                    }else{
                        if(strategy.isBuy(symbol)){
                            double price = strategy.buy(symbol);
                            Execution execution = new Execution(symbol,dataCenter.getTimeStamp(),price, Execution.Type.BUY);
                            saveExecution(symbol, execution);
                            Order order = new Order(getTraderId(),execution);
                            broker.addOrder(order);
                            symbolSet.add(symbol);  //加入已购买股票列表
                        }
                    }
                }
            }
            Sleeper.sleep(checkInterval);
        }
    }

    /**
     * 从broker中获取traderID的最新account状态
     * @return
     */
    public Account2 getAccount(){
        return broker.getAccount(getTraderId());
    }

    private void saveExecution(String symbol, Execution execution) {
        List<Execution> executionList = operations.get(symbol);
        if(executionList ==null){
            executionList = Lists.newArrayList();
        }
        executionList.add(execution);
        operations.put(symbol, executionList);
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

    /**
     *
     */
    public void export(){

    }
}
