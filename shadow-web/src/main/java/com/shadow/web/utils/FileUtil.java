package com.shadow.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @Auther: wangzhendong
 * @Date: 2019/10/28 12:38
 * @Description:
 */
@Slf4j
public class FileUtil {

    public String saveFile(File file){
        return "";
    }

    /**
     * @Description 下载文件
     * @param filePath 文件路径
     * @param fileName 文件名
     * @author szh
     * @Date 2019/11/6 16:22
     */
    public static void downloadFile(String filePath,
                             String fileName,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        log.info("下载文件：filePath={}, fileName={}", filePath, fileName);

        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        try (FileInputStream is = new FileInputStream(new File(filePath))) {
            response.setHeader(
                    "Content-Disposition",
                    "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("下载文件失败", e);
        }
    }

}
