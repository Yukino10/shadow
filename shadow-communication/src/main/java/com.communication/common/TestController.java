package com.communication.common;


import com.communication.coap.InCoAPMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @Author: bin
 * @Date: 2019/10/23 15:39
 * @Description:
 */

@CommunicationController
public class TestController {


    @CommunicationService("zhangsan")
    public void testFunction(InMessage inMessage) {
        System.out.println("test function zhang san is running.");
    }

    @CommunicationService("testFunction2")
    public OutMessage testFunction2(InMessage inMessage) {
        DeviceAddr deviceAddr = inMessage.getSenderAddr();
        System.out.println("test function zhang san is running.");
        return new OutMessage(deviceAddr, "out message result.".getBytes(), "hello");
    }




    /**
     * 收到请求
     * 用户处理
     * 发送响应
     */

    /**
     * 一个重定向应用的demo
     * @param inMessage
     * @return
     */
    @CommunicationService("sys_redirect")
    public OutMessage redirect(InMessage inMessage) {
        byte[] inData = inMessage.getData();
        /** 1、从inData中解析到需要重定向到的接口 */
//        // 假设为 zhangsan
//        String redirect = "zhangsan";
        // 假设为 testFunction2
        String redirect = "testFunction2";
        MethodInfo methodInfo = MethodMap.getMethod(redirect);

        /** 2、重新封装inMessage */
        // 假设重新封装 操作如下
        InMessage newMessage = inMessage;
        /** 3、调用重定向的方法*/
        try {
            if (methodInfo.getMethod().getReturnType().getName().equals("void")) {
                methodInfo.getMethod().invoke(methodInfo.getObject(), newMessage);
                return null;
            } else {
                Object object = methodInfo.getMethod().invoke(methodInfo.getObject(), newMessage);
                if (!object.getClass().isInstance(new OutMessage(newMessage.getSenderAddr(), "".getBytes(), ""))) {
                    // 返回的结果不是 OutMessage 报错。
                    System.out.println("method " + methodInfo.getMethod().getDeclaringClass().getName() + " " + methodInfo.getMethod().getName() + "'s result is not a instance of " + OutMessage.class.getName() + " .");
                    return null;
                }
                return (OutMessage)methodInfo.getMethod().invoke(methodInfo.getObject(), newMessage);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @CommunicationService("hello")
    public void hello(InMessage inMessage) {
        System.out.println(Thread.currentThread().getName() + " server handle a request. ");
//        return new OutMessage(Protocol.COAP, "hello server result.".getBytes(), "hello", inMessage.getSenderAddr());
    }

    @CommunicationService("sys_hello_with_back")
    public OutMessage helloWithBack(InMessage inMessage) {
//        inMessage.deviceAddr;
        System.out.println(Thread.currentThread().getName() + " server handle a request. ");
        return new OutMessage(inMessage.getSenderAddr(), "hello server result.".getBytes(), "hello");
    }


    public static void main(String[] args) throws Exception {
        // 1、扫包
        CommunicationScanner scanner = new CommunicationScanner();
        scanner.addPackage("com.communication");
        try {
            scanner.getMapping();
        } catch (Exception e) {
            System.out.println("a fault happened when scanning package to find class marked with CommunicationController .");
            e.printStackTrace();
        }

        // 2、执行测试
        TestController test = new TestController();
        InMessage inMessage = MessageTransUtil.transCoAPToInMessage(new InCoAPMessage("inside message.".getBytes()));
        OutMessage out = test.redirect(inMessage);

        /** 3、因为OutMessage {@link CoAPMessage } 的 getData() 方法不是公有的，在此用反射测试调用*/
        // 获取 Message 类
        Class clazz = out.getClass().getSuperclass();
        // 获取 Message 类的 getData 方法
        Method getData = clazz.getDeclaredMethod("getData");
        // 设置为可以执行方法
        getData.setAccessible(true);
        // 调用 Message 类的 getData 方法
        Object o = getData.invoke(out);

        System.out.println("-----------虽然有些乱码，但 out message result 还是在的。-------------");
        System.out.println("=====" + new String(objectToBytes(o).get()));

    }

    // 测试方法
    private static<T> Optional<byte[]> objectToBytes(T obj){
        byte[] bytes = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream sOut;
        try {
            sOut = new ObjectOutputStream(out);
            sOut.writeObject(obj);
            sOut.flush();
            bytes= out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(bytes);
    }
}
