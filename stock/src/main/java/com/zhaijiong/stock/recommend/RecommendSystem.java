package com.zhaijiong.stock.recommend;

import com.google.common.collect.ImmutableList;
import com.zhaijiong.stock.common.Conditions;
import com.zhaijiong.stock.model.StockData;
import com.zhaijiong.stock.provider.Provider;
import com.zhaijiong.stock.strategy.buy.GoldenSpiderBuyStrategy;
import com.zhaijiong.stock.strategy.buy.MACDBuyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-27.
 */

@SpringBootApplication
@ImportResource({"applicationContext.xml"})
public class RecommendSystem {
    private static final Logger LOG = LoggerFactory.getLogger(RecommendSystem.class);

    @Autowired
    public Recommender combinedRecommender;

    public List<String> stockList;

    @PostConstruct
    public void init(){
        Conditions conditions  = new Conditions();
        conditions.addCondition("close", Conditions.Operation.LT, 20d);
        conditions.addCondition("PE", Conditions.Operation.LT, 200d);
        conditions.addCondition("marketValue", Conditions.Operation.LT, 200d);

        stockList = ImmutableList.copyOf(Provider.tradingStockList(conditions));
        LOG.info("init,stockList=" + stockList.size());
    }

    @Scheduled(fixedRate = 60000)
    public void process(){
        combinedRecommender.process(stockList);
    }

    public static void main(String[] args) {
        SpringApplication.run(RecommendSystem.class);
    }
}
