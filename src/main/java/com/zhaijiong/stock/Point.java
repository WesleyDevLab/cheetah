package com.zhaijiong.stock;

import java.util.Date;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-7.
 */
public class Point {
    private Date date;
    private Double val;

    public Point(Date date, Double val) {
        this.date = date;
        this.val = val;
    }

    public Date getDate() {
        return date;
    }

    public Double getVal() {
        return val;
    }
}
