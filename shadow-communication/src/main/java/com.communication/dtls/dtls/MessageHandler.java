//package com.communication.dtls.dtls;
//
//import com.communication.utils.ConstUtil;
//import org.eclipse.californium.elements.util.DaemonThreadFactory;
//import org.apache.log4j.Logger;
//import java.io.*;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * @Author: bin
// * @Date: 2019/9/28 12:25
// * @Description:
// */
//public class MessageHandler {
//    private static Logger log = Logger.getLogger(MessageHandler.class);
//    // 此类 处理 preHandList中内容
//    // ying支持多线程 注意线程安全问题
//
//    public void startAHandler() {
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
//                new DaemonThreadFactory("AuxH#"));
//        executor.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                // 新接到的请求
//                List<CoAPMessage> preHandList = MessageList.getPreHandList();
//                // 等待回调的内容
////                List<CoAPMessage> preCallBackList = MessageList.getPreCallBackList();
//                while (true) {
//                    System.out.println("当前 preHandList 的长度为 " + preHandList.size());
//                    if (preHandList.size() > 0) {
//                        CoAPMessage message = preHandList.remove(0);
////                        System.out.println(message.toString());
////                        System.out.println("handler: " + message.toString());
//                        if (message.getState() == ConstUtil.MESSAGE_MY_CALLBACK) {
//                            System.out.println("找到一个需要被回调的message");
//                            // 本机之前的请求，现在返回了
//                            CoAPMessage preMessage = MessageList.getByUuid(message.getUuid(), message.getFrom(), message.getTo());
//                            if (preMessage != null) {
//                                if (message.getTotalFrame() > 1) {
//                                    if (preMessage.getResponse() == null) {
//                                        System.out.println("第一次为 null");
//                                        preMessage.initResponse(message.getTotalFrame() + 1, Util.MAX_SEND_LENGTH + 1, message.getTotalFrame());
//                                    }
////                                    preMessage.getResponse()[]
////                                    preMessage.setResponse();
//                                    System.out.println("正在保存第： " + message.getCurrentFrame() + " 帧。");
//                                    preMessage.setResponse(message.getCurrentFrame(), message.getContent());
//                                    log.info("当前完成的帧数： " + preMessage.inFrame + "  总共需要完成的帧数： " + preMessage.getTotalFrame());
//                                    System.out.println("当前完成的帧数： " + preMessage.inFrame + "  总共需要完成的帧数： " + preMessage.getTotalFrame());
//                                    System.out.println("=========当前帧================");
////                                    System.out.println(message.getContent());
////                                    System.out.println(new String(message.getContent()));
//                                    if (preMessage.inFrame == preMessage.getTotalFrame()) {
//                                        System.out.println("--------------" +
//                                                "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" +
//                                                "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" +
//                                                "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" +
//                                                "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" +
//                                                "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" +
//                                                "---------------------");
//                                        // 可以写出了。。。
//                                        try {
//                                            OutputStream out = new FileOutputStream("D:\\test\\coap\\zzz2.docx");
////                                            OutputStream out = new FileOutputStream("D:\\test\\coap\\output1.mp4");
//                                            for (int i = 0; i < message.getTotalFrame(); i++) {
//                                                out.write(preMessage.getResponse()[i]);
//                                            }
//                                            out.close();
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                }
//                                System.out.println("handler 调用 之前的回调函数");
//                                // 调用之前设置的回调函数
////                                preMessage.getCallBack().callback(message);
//                            } else {
//                                // 可以考虑把这个message再次入队，不过，感觉是浪费资源了。
//                                System.out.println("刚刚也打印了，没有找到相应的 回调。");
//                            }
//                        } else if (message.getState() == ConstUtil.MESSAGE_HIS_CALLBACK) {
//                            System.out.println("对方从这里请求，以便执行他的回调");
//                            // 对方那里 需要从 这里请求数据，以进行下一步操作。
//                            // TODO 拿到对方数据，做点什么。
//                            message.getContent();
//
////                            message.setContent(getAFile());
//                            // 设置发送者和接收者
//                            message.setTo(message.getFrom());
//                            message.setFrom("123456789012");
//                            message.setSendTime(new Date());
//                            // 改状态
//                            // 告诉对方，那里有回调 等待执行
//                            message.setState(ConstUtil.MESSAGE_MY_CALLBACK);
//                            // TODO 比如说返回一个文件
////                            byte[] result = getAFile();
//                            File inFile = new File("D:\\test\\coap\\zzz1.docx");
////                            File inFile = new File("D:\\test\\coap\\test.mp4");
//                            // TODO 这里如果刚刚是整数，那就多了一帧
//                            int balance = (int)(inFile.length() % Util.MAX_SEND_LENGTH) == 0 ? 0 : 1;
//                            int frameAmount = (int)(inFile.length() / Util.MAX_SEND_LENGTH) + balance;
//                            message.setTotalFrame(frameAmount);
//                            try {
//                                InputStream in = new FileInputStream(inFile);
//                                for (int i = 0; i < frameAmount; i ++) {
////                                    result[0] = (byte)i;
//                                    byte[] result = new byte[Util.MAX_SEND_LENGTH];
//                                    message.setCurrentFrame(i);
//                                    in.read(result);
//                                    message.setContent(result);
//
//
//                                    // TODO 这里不能浅拷贝
//                                    MessageList.getPreSendList().add(message.deepCopy());
//                                }
//                                in.close();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
////                            for (int i = 0; i < result.length; i += MAX_SEND_LENGTH) {
////                                message.setCurrentFrame(i);
////                                if ((i + 1) * MAX_SEND_LENGTH <= result.length) {
////                                    byte[] segment = new byte[result.length - (i + 1) * MAX_SEND_LENGTH];
////                                    System.arraycopy(result, i * MAX_SEND_LENGTH, segment, 0, result.length - (i + 1) * MAX_SEND_LENGTH);
////
////                                    message.setContent(segment);
////                                } else {
////                                    byte[] segment = new byte[MAX_SEND_LENGTH];
////                                    System.arraycopy(result, i * MAX_SEND_LENGTH, segment, 0, MAX_SEND_LENGTH);
////                                    message.setContent(segment);
////                                }
////                                // 将此信息发回去
////                                MessageList.getPreSendList().add(message);
////                            }
//
//
//                        } else if (message.getState() == ConstUtil.MESSAGE_NO_CALLBACK) {
//                            // 正常处理，只是没有回调函数而已。 我也不知道这是干啥的数据
//                        } else {
//                            System.out.println("这里应该报错，或者增加新的逻辑");
//                        }
//                        // 保存文件
////                        saveBytesToFile(message.getContent());
//                    } else {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//
//    private void saveBytesToFile(byte[] bytes) {
//        System.out.println("message handler save bytes to file ...");
//
//    }
//    private byte[] getAFile() {
//        File inFile = new File("D:\\test\\coap\\file.jpg");
//        InputStream in = null;
//        byte[] inBytes;
//        try {
//            in = new FileInputStream(inFile);
//
//            inBytes = new byte[(int)inFile.length()];
//            if (inBytes.length == inFile.length()) {
//                in.read(inBytes);
//            } else {
//                System.out.println("文件过大。");
//            }
//            in.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            inBytes = new byte[0];
//        }
//
//        return inBytes;
//    }
//
//}
