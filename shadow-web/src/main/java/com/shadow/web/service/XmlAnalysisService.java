package com.shadow.web.service;

import com.shadow.web.model.enums.VersionEnum;
import com.shadow.web.model.xml.ShadowCode;
import com.shadow.web.utils.ParseXMLUtils;
import com.cislc.shadow.utils.enums.Encryption;
import com.cislc.shadow.utils.enums.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * xml解析service
 *
 * @since 2019/11/4 17:12
 * @author szh
 **/
@Slf4j
@Service
public class XmlAnalysisService {

    /**
     * 解析xml生成代码并编译
     *
     * @param xmlFile xml文件
     * @param xsdFile xsd文件
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @param version 打包版本
     * @author szh
     * @since 2019/10/22 13:57
     */
    ShadowCode analyseXml(File xmlFile, File xsdFile, Protocol protocol, Encryption encryption, VersionEnum version) {
        /* 1. 校验xml */
        boolean validateSuccess = ParseXMLUtils.domValidate(xmlFile, xsdFile);
        if (validateSuccess) {
            /* 2. 解析生成代码 */
            return ParseXMLUtils.xml2ClassCode(xmlFile, protocol, encryption, version);
        } else {
            log.error("解析xml生成代码并编译失败：校验xml失败");
            return null;
        }
    }

}
