package com.zhaijiong.stock.model;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-15.
 */
public class StockData extends LinkedHashMap<String,Double>{

    public String   symbol; //代码

    public String   name;   //名称

    public Date date;       //时间

    public BoardType boardType; //版块信息：主版，中小板，创业板

    public StockMarketType stockMarketType; //市场：深市，沪市

}
