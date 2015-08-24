package com.zhaijiong.stock.convert;

import com.google.common.collect.Lists;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.collect.RealtimeDataCollecter;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.dao.HBase;
import com.zhaijiong.stock.dao.StockDB;
import com.zhaijiong.stock.model.StockData;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zhaijiong.stock.common.Constants.TABLE_CF_DATA;
import static com.zhaijiong.stock.common.Constants.TABLE_STOCK_DAILY;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-16.
 */
public class RealTimeDataConverter implements Converter<Map<String, List<String>>> {
    private static final Logger LOG = LoggerFactory.getLogger(RealTimeDataConverter.class);


    private List<String> columnNames = Lists.newArrayList(
            "marketType",           //0     市场类型,沪市:1,深市:2
            "code",                 //1     证券代码
            "name",                 //2     证券名称
            "buy1",                 //3     买一
            "buy2",                 //4     买二
            "buy3",                 //5     买三
            "buy4",                 //6     买四
            "buy5",                 //7     买五
            "sell1",                //8     卖一
            "sell2",                //9     卖二
            "sell3",                //10     卖三
            "sell4",                //11    卖四
            "sell5",                //12    卖五
            "buy1Volume",           //13    买一手数
            "buy2Volume",           //14    买二手数
            "buy3Volume",           //15    买三手数
            "buy4Volume",           //16    买四手数
            "buy5Volume",           //17    买五手数
            "sell1Volume",          //18    卖一手数
            "sell2Volume",          //19    卖二手数
            "sell3Volume",          //20    卖三手数
            "sell4Volume",          //21    卖四手数
            "sell5Volume",          //22    卖五手数
            "limitUp",              //23    涨停价
            "limitDown",            //24    跌停价
            "close",                //25    最新价,收盘价
            "avgCost",              //26    均价
            "changeAmount",         //27    涨跌额
            "open",                 //28    开盘价
            "change",               //29    涨跌幅
            "high",                 //30    最高价
            "volume",               //31    成交量，单位：手
            "low",                  //32    最低价
            "",                     //33    未知
            "lastClose",            //34    昨收盘
            "amount",               //35    成交额,单位:亿
            "quantityRelative ",    //36    量比
            "turnoverRate",         //37    换手率
            "PE",                   //38    市盈率
            "outerDisc",            //39    外盘,主动买
            "innerDisc",            //40    内盘,主动卖
            "committeeThan",        //41    委比,百分比
            "committeeSent",        //42    委差
            "PB",                   //43    市净率
            "",                     //44    未知
            "circulationMarketValue",   //45流通市值,单位:元
            "aggregateMarketValue",     //46总市值,单位:元
            "",                     //47    未知
            "",                     //48    未知
            "date"                  //49    时间
    );

    @Override
    public List<Put> toPut(Map<String, List<String>> map) {
        if(map.size()==0){
            return null;
        }
        List<String> columns = map.get("Value");
        if (columns.size() != 50) {
            return null;
        }
        Date date = Utils.parseDate(columns.get(49), "yyyy-MM-dd HH:mm:ss");
        //时间设置到15:00:00以方便后期获取日线级别数据,即每天一条记录
        String dateStr = Utils.formatDate(date, "yyyyMMdd") + "1500";
        byte[] rowkey = Utils.getRowkeyWithMd5PrefixAndDateSuffix(columns.get(1), dateStr);
        Put put = new Put(rowkey);
        for (int i = 3; i < columns.size() - 1; i++) {

            if (columnNames.get(i).equals("amount")) {
                put.add(TABLE_CF_DATA, Bytes.toBytes(columnNames.get(i)),
                        Bytes.toBytes(Double.parseDouble(columns.get(i).replaceAll("[亿|千万|百万|十万|万]",""))));
            } else if (!columnNames.get(i).equals("")) {
                if (Utils.isDouble(columns.get(i))) {
                    put.add(TABLE_CF_DATA, Bytes.toBytes(columnNames.get(i)), Bytes.toBytes(Double.parseDouble(columns.get(i))));
                } else {
//                    LOG.error(String.format("rowkey=%s,%s=%s", Bytes.toString(rowkey), columnNames.get(i), columns.get(i)));
                }
            }
        }
        return Lists.newArrayList(put);
    }

    public static void main(String[] args) {
        RealtimeDataCollecter collecter = new RealtimeDataCollecter();
        Map<String, List<String>> collect = collecter.collect("601886");
        RealTimeDataConverter converter = new RealTimeDataConverter();
        List<Put> puts = converter.toPut(collect);
//        Map<byte[], List<KeyValue>> familyMap = puts.get(0).getFamilyMap();
//        List<KeyValue> keyValues = familyMap.get(TABLE_CF_DATA);
//        for(KeyValue kv:keyValues){
//            System.out.println(Bytes.toString(kv.getQualifier())+":"+Bytes.toDouble(kv.getValue()));
//        }
        Context context = new Context();
        HBase hbase = new HBase(context);
        hbase.put(TABLE_STOCK_DAILY, puts);

        StockDB stockDB = new StockDB(context);
        List<StockData> stockDataDaily = stockDB.getStockDataDaily("601886", "20150801", "20150817");
        System.out.println(stockDataDaily.size());
        for (StockData stockData : stockDataDaily) {
            System.out.println(stockData);
        }
    }
}
