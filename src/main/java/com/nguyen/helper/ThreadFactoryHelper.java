package com.nguyen.helper;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by losyn on 3/28/17.
 */
public final class ThreadFactoryHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadFactoryHelper.class);

    private static int MAX_THREADS = 1024;

    private ThreadFactoryHelper() {
    }

    /** 创建带缓存的线程池 **/
    public static ExecutorService newCachedThreadPool(String name) {
        return new ThreadPoolExecutor(0, MAX_THREADS, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactoryOf(name));
    }

    public static ExecutorService newCachedThreadPool(int threads, String name) {
        return new ThreadPoolExecutor(0, threads > MAX_THREADS ? MAX_THREADS : threads, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), threadFactoryOf(name));
    }

    public static ThreadFactory threadFactoryOf(String name) {
        String nameFormat = StringUtils.defaultIfBlank(name, "ES") + "@t%d";
        Thread.UncaughtExceptionHandler eh = (t, e) -> LOG.warn("Unexpected Exception at thread %s ", t.getName(), e);
        return new ThreadFactoryBuilder().setNameFormat(nameFormat).setUncaughtExceptionHandler(eh).build();
    }
}
