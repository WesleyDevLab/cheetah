package com.zhaijiong.crawler.storage;

import com.google.common.base.Strings;
import com.zhaijiong.crawler.Config;
import org.redisson.Redisson;
import org.redisson.core.RList;
import org.redisson.core.RQueue;

import java.util.List;

import static com.zhaijiong.crawler.Constants.*;

public class RedisStorage implements Storage{
    private Config config;
    private Redisson redis;

    //存储已经被爬过的url地址
    private RList<String> indexList;
    private RQueue<String> taskQueue;

    private int indexLength = 0;

    public RedisStorage(Config config){
        this.config = config;
    }

    private org.redisson.Config getRedissonConf() {
        org.redisson.Config redisonConf = new org.redisson.Config();
        String address = this.config.getStr(REDIS_SERVER_ADDRESS);
        if(!Strings.isNullOrEmpty(address)){
            redisonConf.useSingleServer().setAddress(address);
        }
        return redisonConf;
    }

    @Override
    public void init(){
        redis = Redisson.create(getRedissonConf());
        indexList = redis.getList(this.config.getStr(REDIS_INDEX_LIST));
        taskQueue = redis.getQueue(this.config.getStr(REDIS_TASK_QUEUE));
        indexLength = this.config.getInt(REDIS_INDEX_SIZE);
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

    public void addIndex(String url){
        while(indexList.size() > indexLength){
            indexList.remove(0);
        }
        indexList.add(url);
    }

    public List<String> getIndex(String url){
        List<String> index = indexList.subList(0, indexList.size());
        return index;
    }

    public void flush() {
        redis.flushdb();
    }

    public void cleanDB(){
        indexList.clear();
        taskQueue.clear();
    }

    @Override
    public void close() {
        redis.shutdown();
    }
}
