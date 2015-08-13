package com.zhaijiong.bumblebee.store;

import com.google.common.collect.Maps;
import com.zhaijiong.bumblebee.utils.Config;
import com.zhaijiong.bumblebee.utils.Utils;
import org.redisson.Redisson;
import org.redisson.core.RQueue;
import org.redisson.core.RSet;

import java.util.Date;
import java.util.Map;

public class RedisStorage implements Storage{
    private Config config;
    private Redisson redis;

    private static final String REDIS_ADDRESS = "redis.address";
    private static final String CRAWLED_URL = "redis.crawl.url";
    private static final String TASK_QUEUE = "redis.task.queue";
    private Map<String,RSet<String>> crawledDB;
    private RQueue<String> taskQueue;

    public RedisStorage(Config config){
        this.config = config;
    }

    @Override
    public void init(){
        org.redisson.Config config = new org.redisson.Config();
        Object address = this.config.get(REDIS_ADDRESS);
        if(address!=null){
            config.useSingleServer().setAddress(String.valueOf(address));
        }
        redis = Redisson.create(config);
        crawledDB = Maps.newHashMap();
        RSet<String> set = redis.getSet(CRAWLED_URL);
        crawledDB.put(Utils.getDateStr(new Date()),set);
        taskQueue = redis.getQueue(TASK_QUEUE);
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
