package com.web;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolService {

    public static ThreadPoolExecutor threadPool = null;

    private ThreadPoolService() {
    }

    private static final ThreadPoolService threadPoolService = new ThreadPoolService();

    public synchronized static ThreadPoolService getInstance(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                                             TimeUnit unit) {
        if (threadPool == null) {
            if (unit == null) {
                unit = TimeUnit.SECONDS;
            }
            threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                                                new ArrayBlockingQueue<Runnable>(3),
                                                new ThreadPoolExecutor.DiscardOldestPolicy());
        }
        return threadPoolService;
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }
}
