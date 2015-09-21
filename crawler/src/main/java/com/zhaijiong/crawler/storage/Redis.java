package com.zhaijiong.crawler.storage;

import com.google.common.base.Strings;
import com.zhaijiong.crawler.Config;
import org.redisson.Redisson;
import org.redisson.core.RList;
import org.redisson.core.RQueue;

import java.util.List;

import static com.zhaijiong.crawler.Constants.*;

public class Redis {
    private static Config config;
    private static Redisson redis;

    //存储已经被爬过的url地址
    private static RList<String> indexList;
    private static RQueue<String> taskQueue;

    private static int indexLength = 0;

    private static org.redisson.Config getRedissonConf() {
        org.redisson.Config redisonConf = new org.redisson.Config();
        String address = config.getStr(REDIS_SERVER_ADDRESS);
        if(!Strings.isNullOrEmpty(address)){
            redisonConf.useSingleServer().setAddress(address);
        }
        return redisonConf;
    }

    public static void init(Config conf){
        config = conf;
        redis = Redisson.create(getRedissonConf());
        indexList = redis.getList(config.getStr(REDIS_INDEX_LIST));
        taskQueue = redis.getQueue(config.getStr(REDIS_TASK_QUEUE));
        indexLength = config.getInt(REDIS_INDEX_SIZE);
    }

    public static boolean addTask(String url){
        return taskQueue.add(url);
    }

    public static String getTask(){
        return taskQueue.poll();
    }

    public static int taskCount(){
        return taskQueue.size();
    }

    public static void addIndex(String url){
        while(indexList.size() > indexLength){
            indexList.remove(0);
        }
        indexList.add(url);
    }

    public static List<String> getIndex(){
        List<String> index = indexList.subList(0, indexList.size());
        return index;
    }

    public static void flush() {
        redis.flushdb();
    }

    public static void cleanDB(){
        indexList.clear();
        taskQueue.clear();
    }

    public static void close() {
        redis.shutdown();
    }
}
