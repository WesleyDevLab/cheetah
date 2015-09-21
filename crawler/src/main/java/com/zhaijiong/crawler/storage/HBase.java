package com.zhaijiong.crawler.storage;

import com.google.common.collect.Maps;
import com.zhaijiong.crawler.Config;
import com.zhaijiong.crawler.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-9-6.
 */
public class HBase {
    private static final Logger LOG = LoggerFactory.getLogger(HBase.class);

    private static Config config;
    private static HTablePool tablePool;
    private static byte[] crawledTable;
    private static byte[] detailTable;
    private static byte[] COLUMN_FAMILY = "f".getBytes();

    public static void init(Config config) {
        Configuration configuration = getHBaseConfiguration(config);
        crawledTable = config.getStr(Constants.HBASE_CRAWLED_LIST).getBytes();
        tablePool =  new HTablePool(configuration,10);
    }

    private static Configuration getHBaseConfiguration(Config config) {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set(HConstants.ZOOKEEPER_QUORUM, String.valueOf(config.get(Constants.HBASE_ZOOKEEPER_QUORUM)));
        configuration.set(HConstants.ZOOKEEPER_ZNODE_PARENT, String.valueOf(config.get(Constants.HBASE_ZOOKEEPER_ZNODE)));
        return configuration;
    }

    private static void closeTable(HTableInterface table) {
        try {
            table.close();
        } catch (IOException e) {
            LOG.error("failed to close hbase table");
        }
    }

    public static boolean isCrawled(String url){
        HTableInterface table = tablePool.getTable(crawledTable);
        try {
            return table.exists(new Get(Bytes.toBytes(url)));
        } catch (IOException e) {
            LOG.error(String.format("fail to get status with url %s",url),e);
        }finally{
            closeTable(table);
        }
        return false;
    }

    public static void save(String url,Map<String,String> map){
        HTableInterface table = tablePool.getTable(crawledTable);
        Put put = new Put(Bytes.toBytes(url));
        for(Map.Entry<String,String> entry:map.entrySet()){
            put.add(COLUMN_FAMILY,Bytes.toBytes(entry.getKey()),Bytes.toBytes(entry.getValue()));
        }
        try {
            table.put(put);
        } catch (IOException e) {
            LOG.error("fail to save page with url" + url,e);
        }
        closeTable(table);
    }

    public static Map<String,String> get(String url){
        HTableInterface table = tablePool.getTable(crawledTable);
        Map<String,String> maps = Maps.newHashMap();
        try{
            Result result = table.get(new Get(Bytes.toBytes(url)));
            NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(COLUMN_FAMILY);
            for(Map.Entry<byte[],byte[]> entry:familyMap.entrySet()){
                maps.put(Bytes.toString(entry.getKey()),Bytes.toString(entry.getValue()));
            }
        } catch (IOException e) {
            LOG.error(String.format("fail to get detail with url %s", url), e);
        } finally{
            closeTable(table);
        }
        return maps;
    }

    public static void cleanDB(){
        try {
            HBaseAdmin admin = new HBaseAdmin(getHBaseConfiguration(config));
            HTableDescriptor tableDescriptor = admin.getTableDescriptor(crawledTable);
            admin.disableTable(crawledTable);
            admin.deleteTable(crawledTable);
            admin.createTable(tableDescriptor);
        } catch (Exception e) {
            LOG.error("failed to reCreate table",e);
        }
    }

    public static void flush() {
        try {
            HTableInterface table = tablePool.getTable(crawledTable);
            table.flushCommits();
            table.close();
        } catch (IOException e) {
            LOG.error("failed to flush table",e);
        }
    }

    public static void close() {
        try {
            tablePool.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
