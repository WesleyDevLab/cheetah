package com.zhaijiong.stock.collect;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.download.Downloader;
import com.zhaijiong.stock.model.Symbol;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-9.
 */
public class MinuteDataCollecter implements Collecter<String, Map<String,String>> {
    private static final Logger LOG = LoggerFactory.getLogger(MinuteDataCollecter.class);

    public final String minuteDataURL = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=%s&scale=%s&ma=no&datalen=1023";

    public final String type;

    public Date startDate;

    public long startDateLong;

    public Date stopDate;

    public long stopDateLong;

    public MinuteDataCollecter(String startDate, String stopDate, String type) {
        this.startDate = Utils.str2Date(startDate, Constants.BISNESS_DATA_FORMAT);
        this.startDateLong = this.startDate.getTime();
        this.stopDate = Utils.str2Date(stopDate, Constants.BISNESS_DATA_FORMAT);
        this.stopDateLong = this.stopDate.getTime();
        this.type = type;
    }

    @Override
    //key:md5symbolyyyyMMddHHmm,val={day:"2015-08-13 13:55:00",open:"16.300",high:"16.320",low:"16.270",close:"16.290",volume:"390800"}
    public Map<String, Map<String,String>> collect(String symbol) {
        String url = getPath(symbol);
        Map<String,Map<String,String>> stocks = Maps.newTreeMap();
        String data = Downloader.downloadStr(url);

        Pattern pattern = Pattern.compile("\\{([\\w|\"|,|:|\\s|.|-]*)\\}");
        Matcher matcher = pattern.matcher(data.trim());
        Gson gson = new Gson();
        while (matcher.find()) {
            //{day:"2015-08-13 13:55:00",open:"16.300",high:"16.320",low:"16.270",close:"16.290",volume:"390800"}
            Map<String, String> map = gson.fromJson(matcher.group(), Map.class);
            Date date = Utils.str2Date(map.get("day"), "yyyy-MM-dd HH:mm:ss");
            if (date.getTime() >= startDateLong) {
                stocks.put(Bytes.toString(Utils.getRowkeyWithMd5PrefixAndDateSuffix(symbol,Utils.formatDate(date, "yyyyMMddHHmm"))), map);
            }
        }

        return stocks;
    }

    @Override
    public String getPath(String symbol) {
        return String.format(minuteDataURL, Symbol.getSymbol(symbol, minuteDataURL), type);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        final String startDate = "20150806";
        final String stopDate = "20150815";
        Stopwatch stopwatch = Stopwatch.createStarted();
        final Collecter collect = new MinuteDataCollecter(startDate, stopDate, "15");
        Map<String, Map<String,String>> stocks = collect.collect("600376");
        for(Map.Entry<String,Map<String,String>> stock:stocks.entrySet()){
            System.out.println("date:"+stock.getKey()+"map:"+stock.getValue());
        }
        System.out.println("ts:" + stopwatch.toString());
    }
}