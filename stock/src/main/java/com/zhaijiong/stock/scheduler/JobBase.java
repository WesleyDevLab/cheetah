package com.zhaijiong.stock.scheduler;

import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.tools.StockPool;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zhaijiong.stock.common.Constants.DATABASE_POOL_SIZE;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-12.
 */
public abstract class JobBase implements Job {
    protected static final Logger LOG = LoggerFactory.getLogger(JobBase.class);

    @Autowired
    protected Context context;
    @Autowired
    protected StockDB stockDB;
    @Autowired
    protected StockPool stockPool;

    public JobBase(){
    }

    public List<String> getSymbolList(){
        return stockPool.tradingStock();
    }

    public void close(){

    }
}
