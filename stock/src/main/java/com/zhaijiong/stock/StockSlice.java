package com.zhaijiong.stock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.model.Stock;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;
import java.util.Map;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-9.
 */
public class StockSlice {
    public String symbol;
    public String startDate;
    public String stopDate;
    public Type type;

    public List<Stock> stocks;

    public enum Type {
        MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
    }

    public Map<String, List<Double>> points;

    public static StockSlice getSlice(String symbol,List<Stock> stocks, String start, String stop) {
        return new StockSlice(symbol,stocks, start, stop, Type.DAY);
    }

    private StockSlice(String symbol,List<Stock> stocks, String start, String stop, Type type) {
        this.symbol = symbol;
        this.startDate = start;
        this.stopDate = stop;
        this.stocks = stocks;
        this.type = type;
        points = Maps.newHashMapWithExpectedSize(stocks.size());
        setClosePrice();
        setVolumes();
    }

    public List<Stock> getStocks(){
        return stocks;
    }

    public void setClosePrice() {
        List<Double> closes = points.get(Bytes.toString(Constants.CLOSE));
        if(closes==null){
            closes = Lists.newLinkedList();
        }
        for (Stock stock : stocks) {
            closes.add(stock.close);
        }
        points.put(Bytes.toString(Constants.CLOSE), closes);
    }

    public double[] getClose(){
        return getValues(Bytes.toString(Constants.CLOSE));
    }

    public void setVolumes(){
        List<Double> volumes = points.get(Bytes.toString(Constants.VOLUME));
        if(volumes==null){
            volumes = Lists.newLinkedList();
        }
        for (Stock stock : stocks) {
            volumes.add(stock.volume);
        }
        points.put(Bytes.toString(Constants.VOLUME), volumes);
    }

    public double[] getVolumes(){
        return getValues(Bytes.toString(Constants.VOLUME));
    }

    public double[] getValues(String property) {
        List<Double> values = points.get(property);
        if (values == null) {
            values = Lists.newArrayList();
        }
        double[] result = new double[values.size()];
        for(int i=0;i<values.size();i++){
            result[i] = values.get(i);
        }
        return result;
    }
}
