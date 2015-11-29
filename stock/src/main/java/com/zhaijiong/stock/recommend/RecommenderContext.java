package com.zhaijiong.stock.recommend;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: eryk86@gmail.com
 * date: 15-11-29.
 */
public class RecommenderContext {

    public class Config{
        String name;
        String stockPool;
        long interval;

        public Config(){}

        public Config(String name, String stockPool, int interval) {
            this.name = name;
            this.stockPool = stockPool;
            this.interval = interval;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStockPool() {
            return stockPool;
        }

        public void setStockPool(String stockPool) {
            this.stockPool = stockPool;
        }

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "name='" + name + '\'' +
                    ", stockPool='" + stockPool + '\'' +
                    ", interval=" + interval +
                    '}';
        }
    }

    private List<Config> configList = Lists.newArrayList();

    public RecommenderContext(Map<String, Object> config){
        for(Map.Entry<String,Object> recommendersConf:config.entrySet()){
            Map<String,Object> recommenderConf = (Map<String, Object>) recommendersConf.getValue();
            Config conf = new Config();
            for(Map.Entry<String,Object> params:recommenderConf.entrySet()){
                if("name".equals(params.getKey())){
                    conf.setName(String.valueOf(params.getValue()));
                }
                if("stockPool".equals(params.getKey())){
                    conf.setStockPool(String.valueOf(params.getValue()));
                }
                if("interval".equals(params.getKey())){
                    conf.setInterval((Integer) params.getValue());
                }
            }
            configList.add(conf);
        }
    }

    public List<Config> getConfigList() {
        return configList;
    }
}
