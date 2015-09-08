package com.zhaijiong.crawler.storage;

import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-6.
 */
public class HBaseStorage implements Storage{
    private static final Logger LOG = LoggerFactory.getLogger(HBaseStorage.class);

    private Config config;
    private HTable crawlerTable;

    public HBaseStorage(Config config){
        this.config = config;
    }

    @Override
    public void init() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set(HConstants.ZOOKEEPER_QUORUM, String.valueOf(config.get(Constants.HBASE_ZOOKEEPER_QUORUM)));
        configuration.set(HConstants.ZOOKEEPER_ZNODE_PARENT, String.valueOf(config.get(Constants.HBASE_ZOOKEEPER_ZNODE)));
        try {
            crawlerTable = new HTable(configuration,config.getStr(Constants.HBASE_CRAWLED_LIST));
        } catch (IOException e) {
            LOG.error(String.format("fail to create table %s",config.getStr(Constants.HBASE_CRAWLED_LIST)),e);
        }
    }

    public boolean isCrawled(String url){
        try {
            return crawlerTable.exists(new Get(Bytes.toBytes(url)));
        } catch (IOException e) {
            LOG.error(String.format("fail to get status with url %s",url));
        }
        return false;
    }

    public void save(Map<String,String> map){

    }

    public void flush(){
        try {
            crawlerTable.flushCommits();
        } catch (IOException e) {
            LOG.error("fail to flush table",e);
        }
    }

    @Override
    public void close() {
        try {
            crawlerTable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
