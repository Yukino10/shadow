package com.cislc.shadow.log;

/**
 * @Author: bin
 * @Date: 2019/10/14 19:00
 * @Description:
 */
public class Logger {
// extends org.apache.log4j.Logger
//    protected Logger(String name) {
//        super(name);
//    }

//    public static Logger getLogger(Class clazz) {
//        return (Logger) org.apache.log4j.Logger.getLogger(clazz);
//    }

    public static org.apache.log4j.Logger getLogger(Class clazz) {
        return org.apache.log4j.Logger.getLogger(clazz);
    }

//    public void info(Object message) {
//        super.info(message);
//    }

}
