package com.zhaijiong.stock.scheduler;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.zhaijiong.stock.model.StockSlice;
import com.zhaijiong.stock.common.Constants;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.indicators.Indicators;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zhaijiong.stock.common.Constants.*;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-8-12.
 */
public class IndicatorsComputeJob extends JobBase {

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        String starttime = Constants.MARKET_START_DATE;
        String stoptime = Utils.getTomorrow(ROWKEY_DATA_FORMAT);

        Indicators indicators = new Indicators();
        List<String> stockSymbols = stockDB.getStockSymbols();
        for(String symbol:stockSymbols){
            try {
                Stopwatch stopwatch = Stopwatch.createStarted();

                StockSlice stockSliceDaily = stockDB.getStockSliceDaily(symbol, starttime, stoptime);
                if(stockSliceDaily.getStocks().size()==0){
                    LOG.warn(String.format("can't get %s stock data",symbol));
                }
                String date = Utils.formatDate(stockSliceDaily.getStocks().get(stockSliceDaily.getStocks().size()-1).date,ROWKEY_DATA_FORMAT);

                List<Put> puts = Lists.newLinkedList();

                double[] closes = stockSliceDaily.getClose();
                double[] volumes = stockSliceDaily.getVolumes();

                Put put = new Put(Utils.getRowkeyWithMd5PrefixAndDateSuffix(symbol,date));
                LOG.info(Bytes.toString(Utils.getRowkeyWithMd5PrefixAndDateSuffix(symbol,date)));
                double[][] macd = indicators.macd(closes);

                double dif = macd[0][closes.length - 1];
                double dea = macd[1][closes.length - 1];
                double macdRtn = (dif - dea) * 2;

                put.add(TABLE_CF_DATA,"macd_dif".getBytes(), Bytes.toBytes(dif));
                put.add(TABLE_CF_DATA,"macd_dea".getBytes(), Bytes.toBytes(dea));
                put.add(TABLE_CF_DATA,"macd_macd".getBytes(), Bytes.toBytes(macdRtn));

                double[][] bbands = indicators.bbands(closes);

                put.add(TABLE_CF_DATA,"boll_upper".getBytes(), Bytes.toBytes(bbands[0][closes.length - 1]));
                put.add(TABLE_CF_DATA,"boll_mid".getBytes(), Bytes.toBytes(bbands[1][closes.length - 1]));
                put.add(TABLE_CF_DATA,"boll_lower".getBytes(), Bytes.toBytes(bbands[2][closes.length - 1]));

                double[] volume_5 = indicators.sma(volumes, 5);
                double[] volume_10 = indicators.sma(volumes, 10);
                double[] volume_20 = indicators.sma(volumes, 20);
                double[] volume_30 = indicators.sma(volumes, 30);
                double[] volume_60 = indicators.sma(volumes, 60);
                double[] volume_120 = indicators.sma(volumes, 120);
                //单位：股
                put.add(TABLE_CF_DATA,"volume_5".getBytes(), Bytes.toBytes(volume_5[volumes.length-1]));
                put.add(TABLE_CF_DATA,"volume_10".getBytes(), Bytes.toBytes(volume_10[volumes.length-1]));
                put.add(TABLE_CF_DATA,"volume_20".getBytes(), Bytes.toBytes(volume_20[volumes.length-1]));
                put.add(TABLE_CF_DATA,"volume_30".getBytes(), Bytes.toBytes(volume_30[volumes.length-1]));
                put.add(TABLE_CF_DATA,"volume_60".getBytes(), Bytes.toBytes(volume_60[volumes.length-1]));
                put.add(TABLE_CF_DATA,"volume_120".getBytes(), Bytes.toBytes(volume_120[volumes.length-1]));

                double[] closes_5 = indicators.sma(closes, 5);
                double[] closes_10 = indicators.sma(closes, 10);
                double[] closes_20 = indicators.sma(closes, 20);
                double[] closes_30 = indicators.sma(closes, 30);
                double[] closes_60 = indicators.sma(closes, 60);
                double[] closes_120 = indicators.sma(closes, 120);

                put.add(TABLE_CF_DATA,"close_5".getBytes(), Bytes.toBytes(closes_5[volumes.length-1]));
                put.add(TABLE_CF_DATA,"close_10".getBytes(), Bytes.toBytes(closes_10[volumes.length-1]));
                put.add(TABLE_CF_DATA,"close_20".getBytes(), Bytes.toBytes(closes_20[volumes.length-1]));
                put.add(TABLE_CF_DATA,"close_30".getBytes(), Bytes.toBytes(closes_30[volumes.length-1]));
                put.add(TABLE_CF_DATA,"close_60".getBytes(), Bytes.toBytes(closes_60[volumes.length-1]));
                put.add(TABLE_CF_DATA,"close_120".getBytes(), Bytes.toBytes(closes_120[volumes.length-1]));

                puts.add(put);

                stockDB.save(TABLE_STOCK_DAILY,puts);

                LOG.info("symbol:"+symbol + ",cost="+stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
