package com.communication.dtls.pureDtls;

import org.eclipse.californium.elements.util.DaemonThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: bin
 * @Date: 2019/10/28 9:19
 * @Description:
 */
public class CoAPClientThreadPool {
    // 默认开启时，运行的线程数
    private static final int DEFAULT_CLIENT_AMOUNT = 1;

    // client 的最大并发数
    private static final int MAX_THREAD_AMOUNT = 1;


    // 正在运行的client线程数
    static AtomicInteger runningThread = new AtomicInteger(0);

    // 完成初始化的client，存放在此处
    static List<PureDtlsCoapClient> clientList = new ArrayList<>(MAX_THREAD_AMOUNT);

    static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),

            new DaemonThreadFactory("Aux#"));
    /**
     * 初始化客户端线程池，启动默认数量的线程。
     */
    public CoAPClientThreadPool() {
        final CountDownLatch defaultAmount = new CountDownLatch(DEFAULT_CLIENT_AMOUNT);
        // Create & start clients
        // 启动 DEFAULT_CLIENT_AMOUNT 个client
        for (int index = 0; index < DEFAULT_CLIENT_AMOUNT; ++index) {
            final PureDtlsCoapClient client = new PureDtlsCoapClient();
            clientList.add(client);
            // executor.submit()
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    client.start();
                    runningThread.addAndGet(1);
                    defaultAmount.countDown();
                    client.startSendMessage();
                }
            });
        }
        // 等待所有的 client 启动完成
        try {
            defaultAmount.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(DEFAULT_CLIENT_AMOUNT + " DTLS example clients started.");
        // 启动所有 完成初始化的 client
//        for (PureDtlsCoapClient client : clientList) {
//            client.startSendMessage();
//        }
    }

    public synchronized static void callUpAClient() {
        if (clientList.size() > 0) {
            for (PureDtlsCoapClient client : clientList) {
                if (!client.isRunning.get()) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            client.startSendMessage();
                        }
                    });
                    break;
                }
            }
        }
    }

    // 把所有暂停的线程都起一遍
    public synchronized static void callUpSomeClient() {
        if (clientList.size() > 0) {
            for (PureDtlsCoapClient client : clientList) {
                if (!client.isRunning.get()) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            client.startSendMessage();
                        }
                    });
                }
            }
        }
    }



//    public void addClient(int clientNum) {
//        if (runningThread.get() == MAX_THREAD_AMOUNT) {
//
//        }
//    }

    /**
     * 调整当前正在运行的client数量
     * TODO 需要保证线程关闭前，完成应该进行的通信，包括完成发送当前消息和从服务端收到响应。
     */
//    public void stopClient(int clientNum) {
//
//    }


}
