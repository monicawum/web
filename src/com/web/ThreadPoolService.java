package com.web;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolService {

    private ThreadPoolService() {
    }

    private static ThreadPoolExecutor threadPool = null;

    public static ThreadPoolExecutor getThreadPool() {
        synchronized (ThreadPoolService.class) {
            if (threadPool == null) {
                threadPool = new ThreadPoolExecutor(3, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3),
                                                    new ThreadPoolExecutor.DiscardOldestPolicy());
            }
        }
        return threadPool;
    }
}
