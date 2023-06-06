package com.cislc.shadow.utils.objcetStorage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.UUID;

@Component
public class OssServer extends ObjectStorageServer {

    /**
     * 上传byte数组
     *
     * @param objectName oss key
     * @param bytes 文件byte数组
     */
    public static String upload(String objectName, byte[] bytes) {
        if (StringUtils.isBlank(objectName)) {
            objectName = UUID.randomUUID().toString().replaceAll("-", "");
        }

        OSS mOSSClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY, ACCESS_SECRET);
        mOSSClient.putObject(BUCKET_NAME, objectName, new ByteArrayInputStream(bytes));
        mOSSClient.shutdown();
        return objectName;
    }

    /**
     * 上传本地文件
     *
     * @param objectName oss key
     * @param localPath 本地文件路径
     *
     */
    public static String upload(String objectName, String localPath) {
        if (StringUtils.isBlank(objectName)) {
            objectName = UUID.randomUUID().toString().replaceAll("-", "");
        }

        OSS mOSSClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY, ACCESS_SECRET);
        PutObjectRequest mRequest = new PutObjectRequest(BUCKET_NAME, objectName, new File(localPath));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        metadata.setObjectAcl(CannedAccessControlList.Private);
        mRequest.setMetadata(metadata);
        mOSSClient.putObject(mRequest);
        mOSSClient.shutdown();
        return objectName;
    }

    /**
     * 流式下载
     *
     * @param objectName oss key
     * @return 文件流
     */
    public static BufferedReader download(String objectName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY, ACCESS_SECRET);
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(BUCKET_NAME, objectName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
        // 关闭OSSClient。
        ossClient.shutdown();
        return reader;
    }

    /**
     * 下载到本地文件
     *
     * @param objectName oss key
     * @param localPath 本地文件路径
     * @return 文件路径
     */
    public static String download(String objectName, String localPath) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY, ACCESS_SECRET);
        // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
        GetObjectRequest mRequest = new GetObjectRequest(BUCKET_NAME, objectName);
        ossClient.getObject(mRequest, new File(localPath));
        // 关闭OSSClient。
        ossClient.shutdown();
        return localPath;
    }
}
