package com.cislc.shadow.utils.objcetStorage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 对象存储服务
 *
 * @author szh
 * @since 2020/12/11 10:53
 **/
@Component
public class ObjectStorageServer {
    static String ACCESS_KEY;
    static String ACCESS_SECRET;
    static String ENDPOINT;
    static String BUCKET_NAME;

    @Value("${shadow.storage.access-key:key}")
    public void setAccessKey(String accessKey) {
        ObjectStorageServer.ACCESS_KEY = accessKey;
    }

    @Value("${shadow.storage.access-secret:secret}")
    public void setAccessSecret(String accessSecret) {
        ObjectStorageServer.ACCESS_SECRET = accessSecret;
    }

    @Value("${shadow.storage.endpoint:127.0.0.1}")
    public void setEndpoint(String endpoint) {
        ObjectStorageServer.ENDPOINT = endpoint;
    }

    @Value("${shadow.storage.bucket-name:shadow}")
    public void setBucketName(String bucketName) {
        ObjectStorageServer.BUCKET_NAME = bucketName;
    }
}
