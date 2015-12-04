package com.zhaijiong.stock.recommend;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.zhaijiong.stock.common.Context;
import com.zhaijiong.stock.common.Utils;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.tools.StockCategory;
import com.zhaijiong.stock.tools.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 股票推荐，针对多个股票池的个性化策略周期调度系统
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-19.
 */
public abstract class Recommender {
    protected static final Logger LOG = LoggerFactory.getLogger(Recommender.class);

    @Autowired
    public StockCategory stockCategory;

    protected static Map<String, Set<String>> conceptCategory;

    protected static Map<String,Set<String>> industryCategory;

    protected String name;

    private boolean isAlert = false;

    @Autowired
    public Context context;

    public List<String> account;

    public Recommender(){}

    public Recommender(String name) {
        this.name = name;
    }

    @PostConstruct
    public void init(){
        account = context.getList("account");
        conceptCategory = stockCategory.getStockCategory("概念");
        industryCategory = stockCategory.getStockCategory("行业");
//        for(String symbol:account){
//            System.out.println(symbol);
//        }
    }
    /**
     * 获取股票所属概念版块名称列表
     *
     * @param symbol 6位股票代码
     * @return
     */
    public static String getConceptCategory(String symbol) {
        if (conceptCategory.get(symbol) != null) {
            return Joiner.on(",").join(conceptCategory.get(symbol));
        }
        return "";
    }

    public static String getIndustryCategory(String symbol){
        if(industryCategory.get(symbol)!=null){
            return Joiner.on(",").join(industryCategory.get(symbol));
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
            ThreadPool.execute(() -> {
                try {
                    if (isBuy(symbol)) {
                        StockData stockData = Provider.realtimeData(symbol);
                        if (stockData != null && !Strings.isNullOrEmpty(stockData.symbol)) {
                            recommend(stockData);
                        } else {
                            LOG.warn(String.format("fait to get realtime data,symbol is [%s]", symbol));
                        }
                        if (isAlert) {
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
        LOG.info(String.format("Recommender %s process elapsed time=%ss",name, stopwatch.elapsed(TimeUnit.SECONDS)));
    }

    public void recommend(StockData stockData) {
        String record = Joiner.on("\t").join(
                stockData.name, stockData.symbol,
                stockData.get("close"),
                stockData.get("PE"));
        LOG.info(name + "\t" +
                Utils.formatDate(stockData.date,"MM月dd日 HHmmss") +"\t"+
                record + "\t" +
                getIndustryCategory(stockData.symbol) + "\t" +
                getConceptCategory(stockData.symbol));
    }

    //TODO 增加QQ、短信、微信、Mail报警
    public void alert(StockData stockData){}

    public abstract boolean isBuy(String symbol);

    public abstract boolean isBuy(List<StockData> stockDataList);

    public abstract boolean isSell(String symbol);

    public abstract boolean isSell(List<StockData> stockDataList);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
