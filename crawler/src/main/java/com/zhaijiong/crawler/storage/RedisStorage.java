package com.zhaijiong.crawler.storage;

import com.google.common.collect.Maps;
import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.Utils;
import org.redisson.Redisson;
import org.redisson.core.RQueue;
import org.redisson.core.RSet;

import java.util.Date;
import java.util.Map;

import static com.zhaijiong.crawler.Constants.*;

public class RedisStorage implements Storage{
    private Config config;
    private Redisson redis;

    //存储已经被爬过的url地址
    private Map<String,RSet<String>> crawledDB;
    private RQueue<String> taskQueue;

    public RedisStorage(Config config){
        this.config = config;
    }

    @Override
    public void init(){
        org.redisson.Config config = new org.redisson.Config();
        Object address = this.config.get(REDIS_SERVER_ADDRESS);
        if(address!=null){
            config.useSingleServer().setAddress(String.valueOf(address));
        }
        redis = Redisson.create(config);
        crawledDB = Maps.newHashMap();
        RSet<String> set = redis.getSet(REDIS_CRAWLED_URL);
        crawledDB.put(Utils.getDateStr(new Date()),set);
        taskQueue = redis.getQueue(REDIS_TASK_QUEUE);
    }

    public boolean addTask(String url){
        return taskQueue.add(url);
    }

    public String getTask(){
        return taskQueue.poll();
    }

    public int taskCount(){
        return taskQueue.size();
    }

    public boolean crawlURL(String url){
        return crawledDB.get(Utils.getDateStr(new Date())).add(url);
    }

    public boolean isCrawled(String url){
        return crawledDB.get(Utils.getDateStr(new Date())).contains(url);
    }

    @Override
    public void flush() {
        redis.flushdb();
    }

    public void cleanDB(){
        crawledDB.clear();
        taskQueue.clear();
    }

    @Override
    public void close() {
        redis.shutdown();
    }
}
