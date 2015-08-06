package com.zhaijiong.stock;

import com.google.common.base.Strings;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by eryk on 15-7-26.
 */
public class Context {
    private static final Logger LOG = LoggerFactory.getLogger(Context.class);

    private Map conf;
    private Configuration hbaseConf;
    private HTablePool pool;

    public Context() throws IOException {
        this.conf = Utils.readYamlConf("conf.yaml", true);
        pool = new HTablePool(getHBaseConf(),getInt(Constants.DATABASE_POOL_SIZE,1));
    }

    public void close(){
        try {
            pool.close();
        } catch (IOException e) {
            LOG.error("can't close table pool",e);
        }
    }

    public HTableInterface getTable(String tableName){
        return pool.getTable(tableName);
    }

    public void closeTable(HTableInterface table){
        try {
            table.close();
        } catch (IOException e) {
            LOG.error("can't close table " + Bytes.toString(table.getTableName()),e);
        }
    }

    public Context(String conf) throws IOException {
        this.conf = Utils.readYamlConf(conf, true);
    }

    public void put(String key, Object value) {
        conf.put(key,value);
    }

    public String getStr(String key){
        return Utils.getStrOrEmpty(conf,key);
    }

    public Integer getInt(String key,Integer defaultVal){
        if(Strings.isNullOrEmpty(getStr(key))){
            return defaultVal;
        }else{
            return Integer.parseInt(getStr(key));
        }
    }

    public List<String> getList(String key){
        List<String> list = (List<String>) conf.get(key);
        return list;
    }

    public Configuration getHBaseConf(){
        if(hbaseConf == null){
            hbaseConf = new Configuration();
            hbaseConf.set(HConstants.ZOOKEEPER_QUORUM,getStr(HConstants.ZOOKEEPER_QUORUM));
            hbaseConf.set(HConstants.ZOOKEEPER_ZNODE_PARENT,getStr(HConstants.ZOOKEEPER_ZNODE_PARENT));
        }
        return hbaseConf;
    }
}
