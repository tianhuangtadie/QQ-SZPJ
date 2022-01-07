package com.example.qqsz;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 0398mxy
 * 线程池管理公共类
 */
public class ThreadPoolManager {

    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    private final int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private final int maximumPoolSize;

    private final long keepAliveTime = 1;
    private final TimeUnit unit = TimeUnit.HOURS;
    private final ThreadPoolExecutor executor;
    private static final ThreadPoolManager mInstance = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return mInstance;
    }


    /**
     * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1
     */
    private ThreadPoolManager() {
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        maximumPoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                unit, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 执行任务
     */
    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.execute(runnable);
    }

    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.remove(runnable);
    }

    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    public long getTaskCount() {
        return executor.getTaskCount();
    }

    public boolean isThreadPoolExeComplete() {
        return getCompletedTaskCount() == getTaskCount();
    }
}
