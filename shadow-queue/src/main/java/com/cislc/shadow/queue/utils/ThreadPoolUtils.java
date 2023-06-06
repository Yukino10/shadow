package com.cislc.shadow.queue.utils;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ThreadPoolUtils
 * @Description 线程池工具
 * @Date 2019/10/6 21:59
 * @author szh
 **/
public class ThreadPoolUtils {

    /**
     * @Description 获取线程池executor
     * @return executor
     * @author szh
     * @Date 2019/10/6 22:03
     */
    private static ThreadPoolTaskExecutor getExecutor() {
        return ApplicationContextUtil.getBean(ThreadPoolTaskExecutor.class);
    }

    /**
     * @Description 获取线程池中还可以使用的线程数量
     * @return 空闲线程数量
     * @author szh
     * @Date 2019/10/6 22:32
     */
    public static int getFreeThreadNum() {
        ThreadPoolTaskExecutor taskExecutor = getExecutor();
        ThreadPoolExecutor executor = taskExecutor.getThreadPoolExecutor();
        int num = taskExecutor.getMaxPoolSize() + executor.getQueue().remainingCapacity() - executor.getActiveCount();
        return Math.max(num, 0);
    }

}
