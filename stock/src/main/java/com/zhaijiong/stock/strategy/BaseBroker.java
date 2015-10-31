package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.Sleeper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BaseBroker implements IBroker{
    protected static Logger LOG = LoggerFactory.getLogger(BaseBroker.class);

    protected String name; //策略名称
    protected Account account;
    protected Strategy strategy;
    protected DataCenter dataCenter;
    protected List<String> tradingStockList;
    protected ScheduledExecutorService executorService;
    protected int checkInterval = 60 * 1000;    //单位是毫秒
    protected volatile boolean isTrading;   //当前是否是交易时间
    protected volatile boolean isWorking = false;   //是否继续执行策略

    /**
     * key=symbol
     * 操作每个股票记录
     */
    protected Map<String,List<Operation>> operations = Maps.newConcurrentMap();
    //关注已经购买的股票
    protected Set<String> symbolSet = Sets.newConcurrentHashSet();

    public BaseBroker(Context context,DataCenter dataCenter,Strategy strategy){
        this.dataCenter = dataCenter;
        this.strategy = strategy;
        this.account = new Account();
        tradingStockList = Provider.tradingStockList();
        executorService = Executors.newScheduledThreadPool(context.getInt(Constants.BROKER_POOL_SIZE, 1));
        this.checkInterval = context.getInt(Constants.BROKER_CHECK_INTERVAL,300*1000);
        isWorking = true;
    }

    @Override
    public void start() {
        LOG.info(String.format("broker [%s] is working",name));
        while(isWorking){
            if(isTrading()){
                for(String symbol:tradingStockList){
                    if(strategy.isPicked(symbol)){
                        double buy = strategy.buy(symbol);
                        Operation operation = new Operation(symbol,strategy.getTimeStamp(),buy,1000,"buy");
                        List<Operation> operationList = operations.get(symbol);
                        if(operationList==null){
                            operationList = Lists.newArrayList();
                        }
                        operationList.add(operation);
                        operations.put(symbol,operationList);
                    }
                }
            }
            Sleeper.sleep(checkInterval);
        }
    }

    @Override
    public void stop(){
        this.isWorking = false;
        Utils.closeThreadPool(executorService);
    }

    @Override
    public void buy(String symbol) {
        symbolSet.add(symbol);
    }

    @Override
    public void sell(String symbol) {
        symbolSet.remove(symbol);
    }

    /**
     * 判断股票是否持有
     * @param symbol
     * @return
     */
    public boolean isHold(String symbol){
        return symbolSet.contains(symbol);
    }

    @Override
    public void execute(Operation operation){
        String symbol = operation.getSymbol();
        if(operation.getType().equals("b") && !symbolSet.contains(symbol))
            symbolSet.add(symbol);
        List<Operation> operationList = operations.get(operation.getSymbol());
        if(operationList==null){
            operationList = Lists.newLinkedList();
        }
        operationList.add(operation);
    }

    @Override
    public void updateAccount(LocalDateTime ts,double earn){
        account.record(ts,earn);
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public boolean isTrading() {
        isTrading = Utils.isTradingTime();
        return isTrading;
    }

    public boolean isWorking(){
        DateTime time = new DateTime();
        if(time.getHourOfDay() > 15){
            isWorking = false;
        }else{
            isWorking = true;
        }
        return isWorking;
    }
}

