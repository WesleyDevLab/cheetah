package com.zhaijiong.stock.common;

import java.nio.charset.Charset;

/**
 * Created by eryk on 2015/7/20.
 */
public class Constants {
    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static final String DATABASE_POOL_SIZE = "database.pool.size";

    public static String NETEASE_DATE_STYLE = "yyyy-MM-dd";

    public static String IFENG_DATE_STYLE = "yyyy-MM-dd";

    public static String BISNESS_DATA_FORMAT = "yyyyMMdd";

    public static final String ROWKEY_DATA_FORMAT = "yyyyMMddHHmm";

    public static final String MARKET_START_DATE = "19901219";

    public static final String SCHEDULER_JOBS = "jobs";

    public static final String SCHEDULER_JOB_CLASSNAME = "classname";

    public static final String SCHEDULER_JOB_CRON = "cron";

    /**
     * table definition
     */
    public static final String TABLE_STOCK_5_MINUTES = "stocks_data_5mins";

    public static final String TABLE_STOCK_15_MINUTES = "stocks_data_15mins";

    public static final String TABLE_STOCK_30_MINUTES = "stocks_data_30mins";

    public static final String TABLE_STOCK_60_MINUTES = "stocks_data_60mins";

    public static final String TABLE_STOCK_DAILY = "stocks_data_daily";

    public static final String TABLE_STOCK_WEEK = "stocks_data_week";

    public static final String TABLE_STOCK_MONTH = "stocks_data_month";

    public static final byte[] TABLE_CF_DATA = "d".getBytes();

    public static final String TABLE_STOCK_INFO = "stocks_info";

    public static final byte[] TABLE_CF_INFO = "i".getBytes();

    public static final String TABLE_ARTICLE = "stocks_article";

    public static final byte[] TABLE_CF_ARTICLE = "a".getBytes();


    /**
     * stock column
     */
    public static final byte[] CLOSE = "close".getBytes();
    public static final byte[] HIGH = "high".getBytes();
    public static final byte[] LOW = "low".getBytes();
    public static final byte[] OPEN = "open".getBytes();
    public static final byte[] LAST_CLOSE = "lastClose".getBytes();
    public static final byte[] CHANGE_AMOUNT = "changeAmount".getBytes();
    public static final byte[] CHANGE = "change".getBytes();
    public static final byte[] TURNOVER_RATE = "turnoverRate".getBytes();
    public static final byte[] VOLUME = "volume".getBytes();
    public static final byte[] AMOUNT = "amount".getBytes();
    public static final byte[] TOTAL_VALUE = "totalValue".getBytes();
    public static final byte[] MARKET_VALUE = "marketValue".getBytes();
    public static final byte[] AMPLITUDE = "amplitude".getBytes();
    public static final byte[] NAME = "name".getBytes();
    public static final byte[] STATUS = "status".getBytes();

    public static final byte[] AVG_COST = "avgCost".getBytes();
}
