package com.zhaijiong.datacenter;

import com.zhaijiong.stock.model.StockBlock;
import com.zhaijiong.stock.tools.ThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-12-22.
 */
@SpringBootApplication
@EnableScheduling
@ImportResource({"classpath:applicationContext.xml"})
public class DataCenter {

    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        ThreadPool.init(16);
        StockDataDownload.rebuild = true;
        SpringApplication springApplication = new SpringApplication(DataCenter.class);
        springApplication.setWebEnvironment(false);
        applicationContext= springApplication.run(args);
        if(StockDataDownload.rebuild){
            StockDataDownload stockDataDownload = (StockDataDownload) applicationContext.getBean("stockDataDownload");
            stockDataDownload.downloadDailyData();
        }
    }
}
