package com.zhaijiong.stock.common;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * start:今天往前的n个工作日
 * stop：明天，因为hbase的stoprow是开区间
 */
public class DateRange {

    private int dayCount;

    private DateRange() {
    }

    private DateRange(int dayCount) {
        this.dayCount = dayCount;
    }

    public static DateRange getRange(int dayCount) {
        return new DateRange(dayCount);
    }

    public static DateRange getRange(){
        return new DateRange(0);
    }

    public String start() {
        return start("yyyyMMdd");
    }

    public String start(String format) {
        DateTime dt = new DateTime();
        dt = addDays(dt, dayCount);  //[)
        return dt.toString(format);
    }

    public Date startDate(String format){
        return Utils.str2Date(start(),format);
    }

    public Date startDate(){
        return startDate("yyyyMMdd");
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

    public Date stopDate(String format){
        return Utils.str2Date(stop(),format);
    }

    public Date stopDate(){
        return stopDate("yyyyMMdd");
    }
}
