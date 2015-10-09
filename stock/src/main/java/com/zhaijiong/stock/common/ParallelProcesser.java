package com.zhaijiong.stock.common;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-10-9.
 */
public class ParallelProcesser {

    static volatile boolean isInit = false;

    static ScheduledExecutorService executorService;

    static ExecutorService threadPool;

    static synchronized void init(int scheduledPoolSize,int threadPoolSize) {
        if (executorService == null) {
            executorService = Executors.newScheduledThreadPool(scheduledPoolSize);
        }
        if(threadPool == null){
            threadPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadPoolSize));
        }
        isInit = true;
    }

    static void close() {
        Utils.closeThreadPool(executorService);
    }

    static void run(ParallelTask task){
        executorService.submit(task);
    }
}
