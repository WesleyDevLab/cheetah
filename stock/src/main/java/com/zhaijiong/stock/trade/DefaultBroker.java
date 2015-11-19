package com.zhaijiong.stock.trade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.strategy.Account2;
import com.zhaijiong.stock.tools.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class DefaultBroker implements Runnable{
    protected static Logger LOG = LoggerFactory.getLogger(DefaultBroker.class);

    protected Map<String,Account2> accounts = Maps.newConcurrentMap();
    protected DataCenter dataCenter;
    private Context context;
    protected ScheduledExecutorService executorService;
    protected int checkInterval;    //单位是毫秒
    protected volatile boolean isTrading = false;   //当前是否是交易时间
    protected volatile boolean isWorking = false;   //是否继续执行策略
    protected BlockingQueue<Order> requests = new LinkedBlockingQueue<Order>();
    /**
     * key=traderId
     * 操作每个股票记录
     */
    protected Map<String,List<Execution>> operations = Maps.newConcurrentMap();

    public DefaultBroker(Context context){
        this.context = context;
    }

    public void init(DataCenter dataCenter){
        this.dataCenter = dataCenter;
        executorService = Executors.newScheduledThreadPool(10);
        this.checkInterval = context.getInt(Constants.BROKER_CHECK_INTERVAL, 1000);
        isWorking = true;
    }

    /**
     * trader在启动时需要注册在broker中
     * @param traderID
     */
    public void registerAccount(String traderID){
        Account2 account2 = accounts.get(traderID);
        if(account2 ==null){
            account2 = new Account2(traderID);
            accounts.put(traderID, account2);
            LOG.info("register Account="+traderID);
        }else{
            LOG.warn(String.format("Account [%s] is registered. Account=%s",traderID, account2));
        }
    }

    public Account2 getAccount(String traderID){
        Account2 account2 = accounts.get(traderID);
        if(account2 ==null){
            account2 = new Account2(traderID);
            accounts.put(traderID, account2);
        }
        return account2;
    }

    public DataCenter getDataCenter(){
        return dataCenter;
    }

    /**
     * 获取指定traderId股票账户下的股票持仓量情况
     * @param traderId
     * @return
     */
    public Map<String,Account2.Position> getPositions(String traderId){
        Map<String, Account2.Position> positions = Maps.newHashMap(getAccount(traderId).getPositions());
        return positions;
    }

    /**
     * trader关闭broker程序
     */
    public void stop(){
        this.isWorking = false;
    }

    /**
     * trader执行strategy发出买卖指令
     * @param order
     */
    //order需要在trader段check后再执行此方法
    public void addOrder(Order order) {
        requests.add(order);
    }

    public boolean isTrading() {
        isTrading = Utils.isTradingTime();
        return isTrading;
    }

    @Override
    public void run() {
        while(isWorking){
            //非交易时间，等待1秒再次检查
            if(!isTrading()){
                Sleeper.sleep(checkInterval);
                continue;
            }
            Order order = requests.poll();
            Execution execution = order.getExecution();
            String traderId = order.getTraderId();
            List<Execution> executions = operations.get(traderId);
            if(executions==null){
                executions = Lists.newLinkedList();
            }
            executions.add(execution);
            operations.put(traderId,executions);//记录traderId下的操作记录

            //更新traderId的股票仓位
            String symbol = execution.getSymbol();
            Account2 account2 = getAccount(traderId);
            if(account2 ==null){
                account2 = new Account2(traderId);
            }
            Map<String, Account2.Position> positions = account2.getPositions();
            if(positions==null){
                positions = Maps.newHashMap();
            }
            //trader通过broker来获取postions，在下单前要保证下单数据合理
            Account2.Position position = positions.get(symbol);
            if(position==null){
                position = account2.new Position();
                position.symbol = symbol;
                position.ts = order.getExecution().getDate();
            }
            //如果是买入操作，直接增加相应traderId的股票持仓量
            if(execution.getType().compareTo(Execution.Type.BUY)==0){
                position.amount += 1;
                position.buyAmount +=1;
                position.canSell +=1;
                position.costPrice = execution.getPrice();
                position.floatPnl = 0;
            }else if(execution.getType().compareTo(Execution.Type.SELL)==0){//如果是卖出操作，则检查当前持仓量
                position.amount += 1;
            }
            positions.put(symbol, position);
            account2.setPositions(positions);

            accounts.put(traderId, account2);
            order.done();
        }
        Utils.closeThreadPool(executorService);
        LOG.info("broker is stopped");
    }
}

