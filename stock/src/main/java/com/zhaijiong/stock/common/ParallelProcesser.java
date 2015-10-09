package com.zhaijiong.stock.common;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * author: xuqi.xq
 * mail: xuqi.xq@alibaba-inc.com
 * date: 15-10-9.
 */
public class ParallelProcesser {

    static volatile boolean isInit = false;

    static ScheduledExecutorService executorService;

    static ExecutorService threadPool;

    public static synchronized void init(int scheduledPoolSize,int threadPoolSize) {
        if (executorService == null) {
            executorService = Executors.newScheduledThreadPool(scheduledPoolSize);
        }
        if(threadPool == null){
            threadPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadPoolSize));
        }
        isInit = true;
    }

    public static void close() {
        Utils.closeThreadPool(executorService);
        Utils.closeThreadPool(threadPool);
        isInit = false;
    }

    public static void process(Runnable task){
        threadPool.execute(task);
    }

    public static void schedule(Runnable runnable, int start, int period){
        executorService.scheduleAtFixedRate(runnable,start,period, TimeUnit.MINUTES);
    }
}
