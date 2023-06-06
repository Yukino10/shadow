package com.cislc.shadow.utils.heartbeats;

public interface HeartBeatsCallback {
    //心跳开始
    void heartActive();
    //心跳失去与服务端的连接
    void heartInactive();
    //心跳异常获取
    void heartException(Throwable cause);
}
