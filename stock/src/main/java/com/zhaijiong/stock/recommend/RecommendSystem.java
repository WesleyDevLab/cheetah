package com.zhaijiong.stock.recommend;

import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.StockPool;
import com.zhaijiong.stock.tools.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
* author: eryk
* mail: xuqi86@gmail.com
* date: 15-11-27.
*/
@SpringBootApplication
@EnableScheduling
@ImportResource({"classpath:applicationContext.xml"})
public class RecommendSystem {
    private static final Logger LOG = LoggerFactory.getLogger(RecommendSystem.class);

    @Autowired
    public Context context;

    public static ApplicationContext applicationContext;

    @Autowired
    public StockPool stockPool;

    public RecommenderContext recommenderContext;
    public ScheduledExecutorService executorService;

    @PostConstruct
    public void init(){
        LOG.info("recommend system is init...");
        recommenderContext = new RecommenderContext(context.getMap("recommender"));
        LOG.info("recommender config count="+recommenderContext.getConfigList().size());
        executorService = Executors.newScheduledThreadPool(16);
    }

    public void process(){
        List<RecommenderContext.Config> configList = recommenderContext.getConfigList();
        for(RecommenderContext.Config config:configList){
            Recommender recommender = (Recommender) applicationContext.getBean(config.getName());
            List<String> stockList = stockPool.get(config.getStockPool());
            if(stockList==null || stockList.size()==0){
                Conditions conditions = new Conditions();
                conditions.addCondition("close", Conditions.Operation.LT, 30d);
                conditions.addCondition("PE", Conditions.Operation.LT, 200d);
                conditions.addCondition("marketValue", Conditions.Operation.LT, 200d);
                stockList = Provider.tradingStockList(conditions);
                stockPool.add(config.getStockPool(),stockList,43200);
            }
            final List<String> finalStockList = stockList;
            Runnable task = () -> recommender.process(finalStockList);
            executorService.scheduleAtFixedRate(task,0,config.getInterval(),TimeUnit.SECONDS);
        }
    }

    public StockPool getStockPool() {
        return stockPool;
    }

    public void setStockPool(StockPool stockPool) {
        this.stockPool = stockPool;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static void main(String[] args) {
        ThreadPool.init(16);
        applicationContext= SpringApplication.run(RecommendSystem.class);
        RecommendSystem recommendSystem = (RecommendSystem) applicationContext.getBean("recommendSystem");
        recommendSystem.process();
    }
}
