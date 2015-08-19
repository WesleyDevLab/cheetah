package com.zhaijiong.stock.model;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-19.
 */
public enum ArticleType {
    FINANCIAL_STATEMENTS(0),   //财务报表
    RESEARCH_REPORT(1),        //研究报告
    NOTICE(2),                 //公告
    COMMENT(3),                //评论
    NEWS(4);                   //新闻

    private byte[] type;

    ArticleType(int type){
        this.type = Bytes.toBytes(type);
    }

    public byte[] getType(){
        return this.type;
    }
}
