package com.zhaijiong.stock.recommend;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.StockCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 股票推荐，针对多个股票池的个性化策略周期调度系统
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-19.
 */
public abstract class Recommender {
    protected static final Logger LOG = LoggerFactory.getLogger(Recommender.class);

    protected static Map<String, Set<String>> stockCategory = StockCategory.getStockCategory("概念");

    protected static ExecutorService pool = Executors.newFixedThreadPool(32);

    private String name;

    private boolean isAlert = false;

    public Recommender(){}

    public Recommender(String name) {
        this.name = name;
    }

    /**
     * 获取股票所属概念版块名称列表
     *
     * @param symbol 6位股票代码
     * @return
     */
    public static String getStockCategory(String symbol) {
        if (stockCategory.get(symbol) != null) {
            return Joiner.on(",").join(stockCategory.get(symbol));
        }
        return "";
    }

    /**
     * 调用isBuy的策略对股票池进行验证，默认处理时间应小于5分钟
     * @param symbols
     */
    public void process(List<String> symbols) {
        LOG.info(String.format("Recommender %s is start processing,symbols count=%s",name,symbols.size()));
        Stopwatch stopwatch = Stopwatch.createStarted();
        CountDownLatch countDownLatch = new CountDownLatch(symbols.size());
        for (String symbol : symbols) {
            pool.execute(() -> {
                try {
                    if (isBuy(symbol)) {
                        StockData stockData = Provider.realtimeData(symbol);
                        if(stockData!=null){
                            recommend(stockData);
                        }else{
                            LOG.warn(String.format("fait to get realtime data,symbol is [%s]"));
                        }
                        if(isAlert){
                            alert(stockData);
                        }
                    }
                } catch (Exception e) {
                    LOG.error(String.format("fail to process [%s]", symbol), e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await(300, TimeUnit.SECONDS); //对股票池的处理操作应小于5分钟
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
        System.out.println("process elapsed time=" + stopwatch.elapsed(TimeUnit.SECONDS) + "s");
    }

    public void recommend(StockData stockData) {
        String record = Joiner.on("\t").join(
                stockData.name, stockData.symbol,
                stockData.get("close"),
                stockData.get("PE"));
        LOG.info(record + "\t" + getStockCategory(stockData.symbol));
    }

    //TODO 增加QQ、短信、微信、Mail报警
    public void alert(StockData stockData){}

    public abstract boolean isBuy(String symbol);

    public static void close(){
        Utils.closeThreadPool(pool);
    }

}
