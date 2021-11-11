package com.ruitech.bookstudy.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

public class Executors {
    private static final int CPU_COUNT = Math.max(Runtime.getRuntime().availableProcessors(), 1);

    private static ExecutorService hard;
    private static ExecutorService io;
    private static ExecutorService api;

    /**
     * used for local io, database, and other works without too much time.
     */
    public static ExecutorService io() {
        if (io == null) {
            ThreadFactory threadFactory = getThreadFactory("io-");
            int max = Math.min(4, CPU_COUNT * 2 + 1);
            io = new ThreadPoolExecutor(max, max,
                    10L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(), threadFactory);

            ((ThreadPoolExecutor) io).allowCoreThreadTimeOut(true);
        }

        return io;
    }

    public static final Executor SIMPLE_THREAD_EXECUTOR  = java.util.concurrent.Executors.newFixedThreadPool(1);

    /**
     * used for network requesting, but DO NOT use this for big file upload and download.
     */

    public static ExecutorService network() {
        if (api == null) {
            ThreadFactory threadFactory = getThreadFactory("network-");
            int max = Math.min(8, CPU_COUNT * 2 + 1);
            api = new ThreadPoolExecutor(max, max,
                    10L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(), threadFactory);

            ((ThreadPoolExecutor) api).allowCoreThreadTimeOut(true);
        }

        return api;
    }

    /**
     * used for works time-consuming.
     */
    public static ExecutorService hard() {
        if (hard == null) {
            ThreadFactory threadFactory = getThreadFactory("hard-");
            int max = Math.min(4, CPU_COUNT * 2 + 1);
            hard = new ThreadPoolExecutor(max, max,
                    1L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(), threadFactory);

            ((ThreadPoolExecutor) hard).allowCoreThreadTimeOut(true);
        }

        return hard;
    }

    @NonNull
    private static ThreadFactory getThreadFactory(String namePrefix) {
        return new Factory(namePrefix);
    }

    private static class Factory implements ThreadFactory {

        private final String namePrefix;
        AtomicInteger id = new AtomicInteger(1);

        private Factory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + id.getAndIncrement());
            return t;
        }
    }
}

