package com.zhaijiong.crawler;

public class Constants {
    /**
     * crawler constants
     */
    public static final String TEMPLATE_NAME = "source";
    public static final String TEMPLATE_CATEGORY = "category";
    public static final String TEMPLATE_START_URL = "start.url";
    public static final String TEMPLATE_LIST_URL = "list.url";
    public static final String TEMPLATE_DETAIL_URL = "detail.url";
    public static final String TEMPLATE_RULES = "rules";

    /**
     * redis config
     */
    public static final String REDIS_SERVER_ADDRESS = "redis.server.address";
    public static final String REDIS_INDEX_LIST = "redis.index.list";
    public static final String REDIS_INDEX_SIZE = "redis.index.size";
    public static final String REDIS_TASK_QUEUE = "redis.task.queue";

    /**
     * hbase config
     */
    public static final String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
    public static final String HBASE_ZOOKEEPER_ZNODE = "hbase.zookeeper.znode";
    public static final String HBASE_CRAWLED_LIST = "hbase.crawled.list";
}
