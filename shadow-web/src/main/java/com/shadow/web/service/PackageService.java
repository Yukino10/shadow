package com.shadow.web.service;

import com.shadow.web.consts.path.Path;
import com.shadow.web.consts.path.PathFactory;
import com.shadow.web.model.enums.VersionEnum;
import com.shadow.web.model.xml.ShadowCode;
import com.shadow.web.utils.*;
import com.cislc.shadow.utils.enums.Encryption;
import com.cislc.shadow.utils.enums.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * PackageService
 * 打包service
 * @since 2019/11/6 9:33
 * @author szh
 **/
@Slf4j
@Service
public class PackageService {

    @Autowired
    private XmlAnalysisService xmlAnalysisService;

    /**
     * 打包并下载
     * @param xmlFiles xml文件
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @param serverPackageName 后台jar包文件名
     * @author szh
     * @since 2019/11/6 16:33
     */
    void packageJar(List<File> xmlFiles,
                           Protocol protocol,
                           Encryption encryption,
                           String serverPackageName,
                           VersionEnum version,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        /* 1. 获取版本对应路径 */
        Path path = PathFactory.getPath(version);

        /* 2. 删除之前的代码 */
        JavaFileUtils.deleteJavaFiles(path);

        /* 3. 解析xml生成代码 */
        // 读取xsd
        ClassPathResource cpr = new ClassPathResource(XmlConstants.XSD_PATH);
        File xsdFile;
        try (InputStream is = cpr.getInputStream()) {
            xsdFile = File.createTempFile("model", ".xsd");
            FileUtils.copyInputStreamToFile(is, xsdFile);
        } catch (IOException e) {
            log.error("打包失败：读xml失败", e);
            return;
        }
        // 解析xml
        for (File file : xmlFiles) {
            // 解析
            ShadowCode code = xmlAnalysisService.analyseXml(file, xsdFile, protocol, encryption, version);
            if (code == null) {
                log.error("打包失败：解析xml失败");
                JavaFileUtils.deleteJavaFiles(path);
                return;
            }

            // 写入文件
            generateJavaFile(code, path);
        }

        /* 4. maven打包 */
        long current = System.currentTimeMillis();
        log.info("maven打包开始：packageName={}, current={}", serverPackageName, current);

        String packageResult = CommandLineUtils.mvnPackage(path.getPackageScriptPath());

        if (!"success".equals(packageResult)) {
            log.error("打包失败：maven打包失败，{}", packageResult);
            JavaFileUtils.deleteJavaFiles(path);
            return;
        }
        log.info("maven打包结束：packageName={}, usedTime={}", serverPackageName, System.currentTimeMillis() - current);

        /* 5. 返回jar包 */
        FileUtil.downloadFile(path.getPackagePath(), serverPackageName, request, response);
    }

    /**
     * 写入代码到文件
     *
     * @param code 代码
     * @author szh
     * @since 2021/4/3 16:47
     */
    public void generateJavaFile(ShadowCode code, Path path) {
        if (MapUtils.isNotEmpty(code.getEntityCode())) {
            JavaFileUtils.newJavaFiles(code.getEntityCode(), path.getEntityFilePath());
        }
        if (MapUtils.isNotEmpty(code.getRepositoryCode())) {
            JavaFileUtils.newJavaFiles(code.getRepositoryCode(), path.getRepositoryFilePath());
        }
        if (MapUtils.isNotEmpty(code.getInitCode())) {
            JavaFileUtils.newJavaFiles(code.getInitCode(), path.getInitFilePath());
        }
        if (MapUtils.isNotEmpty(code.getEntityNameCode())) {
            JavaFileUtils.newJavaFiles(code.getEntityNameCode(), path.getEntityNameFilePath());
        }
        if (MapUtils.isNotEmpty(code.getAndroidEntityCode())) {
            JavaFileUtils.newJavaFiles(code.getAndroidEntityCode(), path.getAndroidJavaFilePath());
        }
        if (MapUtils.isNotEmpty(code.getAndroidEntityNameCode())) {
            JavaFileUtils.newJavaFiles(code.getAndroidEntityNameCode(), path.getAndroidJavaFilePath());
        }
    }

}
