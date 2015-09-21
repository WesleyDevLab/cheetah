package com.zhaijiong.crawler.storage;

import com.google.common.collect.Maps;
import com.zhaijiong.crawler.Config;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-20.
 */
public class CrawlerDB{

    public static void init(Config config){
        HBase.init(config);
        Redis.init(config);
    }

    /**
     * 判断url是否已经爬过
     * @param url
     * @return
     */
    public static boolean isCrawled(String url){
        return HBase.isCrawled(url);
    }

    /**
     * 将list url解析出来url加入任务队列
     * @param url
     */
    public static void addTask(String url){
        Redis.addTask(url);
    }

    /**
     * 从任务队列中获取任务url
     * @return
     */
    public static String getTask(){
        return Redis.getTask();
    }

    public static void addIndex(String url,Map<String,String> map){
        Redis.addIndex(url);
        HBase.save(url,map);
    }

    public static Map<String,Map<String,String>> getIndex(){
        Map<String,Map<String,String>> map = Maps.newLinkedHashMap();

        List<String> list = Redis.getIndex();
        for(String url:list){
            Map<String, String> kvMap = HBase.get(url);
            map.put(url,kvMap);
        }
        return map;
    }
}
