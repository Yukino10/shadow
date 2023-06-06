package com.cislc.shadow.utils.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * mqtt线程池
 *
 * @author szh
 * @since 2020/12/11 9:34
 **/
public class MqttThreadPool {

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 3;
    /**
     * 保活时间
     */
    private static final int KEEP_ALIVE_TIME = 10;
    /**
     * 阻塞队列长度
     */
    private static final int QUEUE_SIZE = 500;
    /**
     * 拒绝策略
     */
    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.AbortPolicy();
    /**
     * 线程池前缀
     */
    private static final String THREAD_POOL_PREFIX = "mqtt-pool";
    /**
     * 线程池
     */
    private static ThreadPoolExecutor threadPool;

    private static volatile MqttThreadPool mqttThreadPool;

    private MqttThreadPool() {
        ArrayBlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES,
                taskQueue, new NamedThreadFactory(THREAD_POOL_PREFIX), REJECTED_EXECUTION_HANDLER);
    }

    public static MqttThreadPool getThreadPool() {
        if (mqttThreadPool == null) {
            synchronized (MqttThreadPool.class) {
                if (mqttThreadPool == null) {
                    mqttThreadPool = new MqttThreadPool();
                }
            }
        }
        return mqttThreadPool;
    }

    public void addTask(Runnable task) {
        if (task == null) {
            return;
        }
        threadPool.execute(task);
    }

}
