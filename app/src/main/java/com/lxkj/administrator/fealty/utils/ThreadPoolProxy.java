package com.lxkj.administrator.fealty.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/7/26.
 */
public class ThreadPoolProxy {
    private ThreadPoolExecutor executor;
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;

    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
    }
    public void initThreadPoolProxy() {// 双重检测机制
        if (executor == null) {
            synchronized (ThreadPoolProxy.class) {
                if (executor == null) {
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
                    executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                            keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory, handler);
//                        public ThreadPoolExecutor(int corePoolSize,//核心池的大小
//                        int maximumPoolSize,//线程池最大线程数
//                        long keepAliveTime,//保持时间
//                        TimeUnit unit,//时间单位
//                        BlockingQueue<Runnable> workQueue,//任务队列
//                        ThreadFactory threadFactory,//线程工厂
//                        RejectedExecutionHandler handler) //异常的捕捉器
                }
            }
        }
    }

    public void execute(Runnable runnable) {
        initThreadPoolProxy();
        executor.execute(runnable);
    }

    public void remove(Runnable runnable) {
        initThreadPoolProxy();
        executor.remove(runnable);
    }
}
