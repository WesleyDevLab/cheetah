package com.zhaijiong.stock.recommend;

import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.tools.StockPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
@Component()
public class RecommendSystem {
    private static final Logger LOG = LoggerFactory.getLogger(RecommendSystem.class);

    @Autowired
    public Context context;

    public static ApplicationContext applicationContext;

    @Autowired
    public StockPool stockPool;

//    @Autowired
//    public RedisTemplate<String, String> redisTemplate;

    public RecommenderContext recommenderContext;

    @PostConstruct
    public void init(){
        LOG.info("recommend system is init...");
        LOG.info("stockPool size=" + stockPool.size());
        recommenderContext = new RecommenderContext(context.getMap("recommender"));
        LOG.info("recommender config count="+recommenderContext.getConfigList().size());
    }

    public void process(){
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(16);
        List<RecommenderContext.Config> configList = recommenderContext.getConfigList();
        for(RecommenderContext.Config config:configList){
            System.out.println("config:"+config.getName());
            Recommender recommender = (Recommender) applicationContext.getBean(config.getName());
            Runnable task = () -> recommender.process(stockPool.get(config.getStockPool()));
            executorService.scheduleAtFixedRate(task,0,config.getInterval(),TimeUnit.SECONDS);
        }
    }

    public static void main(String[] args) {
        applicationContext=new ClassPathXmlApplicationContext("applicationContext.xml");
        RecommendSystem recommendSystem = (RecommendSystem) applicationContext.getBean("recommendSystem");
        recommendSystem.process();
//        recommendSystem.redisTemplate.delete("tradingStock");
//        List<String> list = recommendSystem.stockPool.get("small");
//        String[] values = new String[list.size()];
//        for(int i=0;i<list.size();i++){
//            values[i] = list.get(i);
//        }
//        recommendSystem.redisTemplate.opsForList().leftPushAll("tradingStock",values);
//        recommendSystem.redisTemplate.expire("tradingStock",30, TimeUnit.SECONDS);
    }
}
