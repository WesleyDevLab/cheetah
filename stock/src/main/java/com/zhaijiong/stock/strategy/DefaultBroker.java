package com.zhaijiong.stock.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.DataCenter;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.tools.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class DefaultBroker implements Runnable{
    protected static Logger LOG = LoggerFactory.getLogger(DefaultBroker.class);

    protected Map<String,Account> accountMap = Maps.newConcurrentMap();
    protected DataCenter dataCenter;
    protected ScheduledExecutorService executorService;
    protected int checkInterval = 1000;    //单位是毫秒
    protected volatile boolean isTrading = false;   //当前是否是交易时间
    protected volatile boolean isWorking = false;   //是否继续执行策略
    protected BlockingQueue<Order> requests = new LinkedBlockingQueue<Order>();
    /**
     * key=traderId
     * 操作每个股票记录
     */
    protected Map<String,List<Execution>> operations = Maps.newConcurrentMap();

    public DefaultBroker(Context context, DataCenter dataCenter){
        this.dataCenter = dataCenter;
        this.accountMap = Maps.newConcurrentMap();
        this.checkInterval = context.getInt(Constants.BROKER_CHECK_INTERVAL,1000);
        isWorking = true;
    }

    public Account getAccount(String traderId){
        return accountMap.get(traderId);
    }

    public DataCenter getDataCenter(){
        return dataCenter;
    }

    /**
     * 获取指定traderId股票账户下的股票持仓量情况
     * @param traderId
     * @return
     */
    public Map<String,Account.Position> getPositions(String traderId){
        Map<String, Account.Position> positions = Maps.newHashMap(getAccount(traderId).getPositions());
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
            Account account = getAccount(traderId);
            Map<String, Account.Position> positions = account.getPositions();
            if(positions==null){
                positions = Maps.newHashMap();
            }
            //trader通过broker来获取postions，在下单前要保证下单数据合理
            Account.Position position = positions.get(symbol);
            //如果是买入操作，直接增加相应traderId的股票持仓量
            if(execution.getType().equals("buy")){
                if(position==null){
                    position = account.new Position();
                    position.symbol = symbol;
                }
            }else if(execution.getType().equals("sell")){//如果是卖出操作，则检查当前持仓量

            }
            positions.put(symbol, position);
            account.setPositions(positions);

            //交易后更新股票账户
            if(account ==null){
                account = new Account(traderId);
            }
            if(execution.getType().equals("buy")){

            }else if(execution.getType().equals("sell")){

            }
            accountMap.put(traderId, account);
            order.done();
        }
        Utils.closeThreadPool(executorService);
        LOG.info("broker is stopped");
    }
}

