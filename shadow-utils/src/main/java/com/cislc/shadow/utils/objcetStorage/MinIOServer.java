package com.cislc.shadow.utils.objcetStorage;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * MinIO服务
 *
 * @author szh
 * @since 2020/12/11 10:56
 **/
@Component
public class MinIOServer extends ObjectStorageServer {

    private static final Logger log = LoggerFactory.getLogger(MinIOServer.class);

    public static final String PLAIN = "text/plain";
    public static final String JPEG = "image/jpeg";
    public static final String PNG = "image/png";

    /**
     * 上传文件
     *
     * @param objectName key
     * @param stream 文件流
     * @param contentType 文件类型
     * @return key
     * @author szh
     * @since 2020/12/11 17:22
     */
    public static String upload(String objectName, InputStream stream, String contentType) {
        if (StringUtils.isBlank(objectName)) {
            objectName = UUID.randomUUID().toString().replaceAll("-", "");
        }

        try {
            MinioClient minioClient = new MinioClient(ENDPOINT, ACCESS_KEY, ACCESS_SECRET);
            if (!minioClient.bucketExists(BUCKET_NAME)) {
                minioClient.makeBucket(BUCKET_NAME);
            }
            minioClient.putObject(BUCKET_NAME, objectName, stream, contentType);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException | XmlPullParserException ex) {
            log.error("MinIO upload file failed", ex);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return objectName;
    }

    /**
     * 上传本地文件
     *
     * @param objectName key
     * @param localPath 本地文件路径
     * @return key
     * @author szh
     * @since 2020/12/11 17:22
     */
    public static String upload(String objectName, String localPath) {
        if (StringUtils.isBlank(objectName)) {
            objectName = UUID.randomUUID().toString().replaceAll("-", "");
        }

        try {
            MinioClient minioClient = new MinioClient(ENDPOINT, ACCESS_KEY, ACCESS_SECRET);
            if (!minioClient.bucketExists(BUCKET_NAME)) {
                minioClient.makeBucket(BUCKET_NAME);
            }
            minioClient.putObject(BUCKET_NAME, objectName, localPath);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException | XmlPullParserException ex) {
            log.error("MinIO upload file failed", ex);
        }
        return objectName;
    }

    /**
     * 下载文件
     *
     * @param objectName key
     * @return 文件流
     * @author szh
     * @since 2020/12/11 18:20
     */
    public static InputStream download(String objectName) {
        try {
            MinioClient minioClient = new MinioClient(ENDPOINT, ACCESS_KEY, ACCESS_SECRET);
            minioClient.statObject(BUCKET_NAME, objectName);

            return minioClient.getObject(BUCKET_NAME, objectName);
        } catch (MinioException | NoSuchAlgorithmException | IOException | InvalidKeyException | XmlPullParserException ex) {
            log.error("MinIO download file failed", ex);
            return null;
        }
    }

    /**
     * 下载到本地文件
     *
     * @param objectName key
     * @param localPath 本地文件路径
     */
    public static void download(String objectName, String localPath) {
        try {
            MinioClient minioClient = new MinioClient(ENDPOINT, ACCESS_KEY, ACCESS_SECRET);
            minioClient.statObject(BUCKET_NAME, objectName);

            minioClient.getObject(BUCKET_NAME, objectName, localPath);
        } catch (MinioException | NoSuchAlgorithmException | IOException | InvalidKeyException | XmlPullParserException ex) {
            log.error("MinIO download file failed", ex);
        }
    }

}
