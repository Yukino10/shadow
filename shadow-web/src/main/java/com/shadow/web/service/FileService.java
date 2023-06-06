package com.shadow.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;

/**
 * @Auther: wangzhendong
 * @Date: 2019/10/28 12:45
 * @Description:
 */
@Slf4j
@Service
public class FileService {

    /**
     * 存放资源文件的路径
     */
    @Value("${file.resource.location}")
    private String resourceLocation;

    /**
     * 文件存储
     * @param file MultipartFile类型
     * @return boolean
     */
    public String saveFile(MultipartFile file) throws IOException {
        File localFile = new File(resourceLocation);
        if(!localFile.exists()){
            localFile.mkdirs();
        }
        FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
        String fileName = new Date().getTime() + "." + file.getOriginalFilename().split("\\.")[1];
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(resourceLocation + File.separator + fileName));
        byte[] bs = new byte[1024];
        int len;
        while((len = fileInputStream.read(bs)) !=-1 ){
            bos.write(bs, 0, len);
        }
        bos.flush();
        bos.close();
        return resourceLocation+"/"+fileName;
    }

    /**
     * 文件存储
     * @param content 文件内容
     * @param fileName 文件名
     * @return 文件路径
     */
    public String saveFile(String content, String fileName) {
        File localFile = new File(resourceLocation);
        if(!localFile.exists()){
            localFile.mkdirs();
        }

        String filePath = resourceLocation + File.separator + fileName;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            return filePath;
        } catch (IOException ex) {
            log.error("写入文件失败：content={}, fileName={}", content, fileName, ex);
        }
        return null;
    }
}
