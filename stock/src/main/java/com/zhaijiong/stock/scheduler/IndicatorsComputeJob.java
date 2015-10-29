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
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-12.
 */
public class IndicatorsComputeJob extends JobBase {

    @Override
    public void execute(JobExecutionContext jobContext) throws JobExecutionException {
        String starttime = Constants.MARKET_START_DATE;
        String stoptime = Utils.getTomorrow(ROWKEY_DATA_FORMAT);

        Indicators indicators = new Indicators();
        List<String> stockSymbols = getSymbolList();
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

                double[][] bbands = indicators.boll(closes);

                put.add(TABLE_CF_DATA,"boll_upper".getBytes(), Bytes.toBytes(bbands[0][closes.length - 1]));
                put.add(TABLE_CF_DATA,"boll_mid".getBytes(), Bytes.toBytes(bbands[1][closes.length - 1]));
                put.add(TABLE_CF_DATA,"boll_lower".getBytes(), Bytes.toBytes(bbands[2][closes.length - 1]));

                setPutWithValue(indicators,5 ,volumes,"volume_5", put);
                setPutWithValue(indicators,10 ,volumes,"volume_10", put);
                setPutWithValue(indicators,20 ,volumes,"volume_20", put);
                setPutWithValue(indicators,30 ,volumes,"volume_30", put);
                setPutWithValue(indicators,60 ,volumes,"volume_60", put);
                setPutWithValue(indicators,120 ,volumes,"volume_120", put);

                setPutWithValue(indicators,5 ,closes,"close_5", put);
                setPutWithValue(indicators,10 ,closes,"close_10", put);
                setPutWithValue(indicators,20 ,closes,"close_20", put);
                setPutWithValue(indicators,30 ,closes,"close_30", put);
                setPutWithValue(indicators,60 ,closes,"close_60", put);
                setPutWithValue(indicators,120 ,closes,"close_120", put);

                puts.add(put);

                stockDB.save(TABLE_STOCK_DAILY,puts);

                LOG.info("symbol:"+symbol + ",cost="+stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void setPutWithValue(Indicators indicators,int period, double[] arrays,String columnName, Put put) {
        if(arrays.length>=period){
            double[] values = indicators.sma(arrays, period);
            put.add(TABLE_CF_DATA,Bytes.toBytes(columnName), Bytes.toBytes(values[arrays.length - 1]));
        }
    }
}
