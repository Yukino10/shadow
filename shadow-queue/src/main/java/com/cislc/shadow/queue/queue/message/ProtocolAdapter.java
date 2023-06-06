package com.cislc.shadow.queue.queue.message;
/**
 * 消息协议适配器
 *
 * @since 2020/10/26 14:33
 * @author szh
 **/
public interface ProtocolAdapter {

    /**
     * 转换数据
     *
     * @param message 原始数据
     * @return 转换后数据
     * @author szh
     * @since 2020/10/26 15:33
     */
    default String transform(String message) {
        return message;
    }

}
