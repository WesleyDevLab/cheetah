package com.zhaijiong.stock.common;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.ReadablePeriod;

/**
 * start:今天往前的n个工作日
 * stop：明天，因为hbase的stoprow是开区间
 */
public class DateRange {

    private String startDate;
    private String stopDate;
    private int dayCount;

    private DateRange() {
    }

    private DateRange(int dayCount) {
        this.dayCount = dayCount;
    }

    public static DateRange getRange(int dayCount) {
        return new DateRange(dayCount);
    }

    public String start() {
        return start("yyyyMMdd");
    }

    public String start(String format) {
        DateTime dt = new DateTime();
        dt = addDays(dt, dayCount);  //[)
        return dt.toString(format);
    }

    private DateTime addDays(DateTime dateTime, int days) {
        DateTime dt = dateTime;
        for (int i = 0; i < days; i++) {
            dt = dt.plusDays(-1);
            if (dt.getDayOfWeek() == 7) {
                dt = dt.plusDays(-2);
            }
        }
        return dt;
    }

    public String stop() {
        return stop("yyyyMMdd");
    }

    public String stop(String format) {
        DateTime dt = new DateTime();
        dt = dt.plusDays(1);
        return dt.toString(format);
    }
}
