package com.zhaijiong.stock.strategy;

import java.time.LocalDateTime;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-21.
 */
public interface IBroker {

    /**
     * 交易时间开始
     */
    public void start();

    /**
     * 交易时间结束
     */
    public void stop();

    /**
     * 执行交易策略
     */
    public void execute(Execution execution);

    /**
     * 按照交易策略买入股票
     * @param symbol
     */
    public void buy(String symbol);

    /**
     * 按照交易策略卖出股票
     * @param symbol
     */
    public void sell(String symbol);

    /**
     * 更新交易账户，完成一笔卖出后更新交易账户
     */
    public void updateAccount(LocalDateTime ts,double earn);

}
