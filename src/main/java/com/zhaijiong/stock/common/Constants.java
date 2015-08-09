package com.zhaijiong.stock.common;

import java.nio.charset.Charset;

/**
 * Created by eryk on 2015/7/20.
 */
public class Constants {
    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static final String DATABASE_POOL_SIZE = "database.pool.size";

    public static final String MARKET_START_DATE = "19901219";

    public static final String SCHEDULER_JOBS = "jobs";

    public static final String SCHEDULER_JOB_CLASSNAME = "classname";

    public static final String SCHEDULER_JOB_CRON = "cron";

    /**
     * table definition
     */
    public static final String TABLE_STOCK_DAILY = "stocks_day";

    public static final byte[] TABLE_CF_BASE = "b".getBytes();

    public static final byte[] TABLE_CF_FEATURE = "f".getBytes();

    public static final String TABLE_STOCK_INFO = "stocks_info";

    public static final byte[] TABLE_CF_INFO = "i".getBytes();

    public static final byte[] TABLE_STOCK_NEWS = "stocks_news".getBytes();

    public static final byte[] TABLE_CF_NEWS = "n".getBytes();

    /**
     * stock column
     */
    public static final byte[] CLOSE = "close".getBytes(UTF8);
    public static final byte[] HIGH = "high".getBytes(UTF8);
    public static final byte[] LOW = "low".getBytes(UTF8);
    public static final byte[] OPEN = "open".getBytes(UTF8);
    public static final byte[] LAST_CLOSE = "lastClose".getBytes(UTF8);
    public static final byte[] CHANGE_AMOUNT = "changeAmount".getBytes(UTF8);
    public static final byte[] CHANGE = "change".getBytes(UTF8);
    public static final byte[] TURNOVER_RATE = "turnoverRate".getBytes(UTF8);
    public static final byte[] VOLUME = "volume".getBytes(UTF8);
    public static final byte[] AMOUNT = "amount".getBytes(UTF8);
    public static final byte[] TOTAL_VALUE = "totalValue".getBytes(UTF8);
    public static final byte[] MARKET_VALUE = "marketValue".getBytes(UTF8);
    public static final byte[] AMPLITUDE = "amplitude".getBytes(UTF8);
    public static final byte[] NAME = "name".getBytes(UTF8);
}
