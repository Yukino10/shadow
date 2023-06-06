package com.shadow.web.service;

import com.cislc.shadow.utils.enums.Encryption;
import com.cislc.shadow.utils.enums.Protocol;
import com.shadow.web.consts.path.Path;
import com.shadow.web.consts.path.PathFactory;
import com.shadow.web.model.enums.VersionEnum;
import com.shadow.web.model.result.ProductInfo;
import com.shadow.web.model.result.Result;
import com.shadow.web.model.xml.ShadowCode;
import com.shadow.web.utils.ParseXMLUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class XmlAnalysisServiceTest {

    @Autowired
    private XmlAnalysisService xmlAnalysisService;
    @Autowired
    private PackageService packageService;
    @Autowired
    private ProductService productService;

    @Test
    public void analyseXml() {
        File xmlFile = new File("src/main/resources/xmlData/SduCabinet.xml");
        File xsdFile = new File("src/main/resources/xmlData/model.xsd");
        VersionEnum version = VersionEnum.B;
        Path path = PathFactory.getPath(version);

        ShadowCode shadowCode = xmlAnalysisService.analyseXml(xmlFile, xsdFile, Protocol.MQTT, Encryption.NONE, version);
        packageService.generateJavaFile(shadowCode, path);
    }

    @Test
    public void validateXml() {
        File xmlFile = new File("E:\\root\\model\\1620287503312.xml");
        File xsdFile = new File("src/main/resources/xmlData/model.xsd");
        boolean validateSuccess = ParseXMLUtils.domValidate(xmlFile, xsdFile);
        log.error("validate result ===========> {}", validateSuccess);
    }

    @Test
    public void buildXmlFile() {
        Result<ProductInfo> productInfoResult = productService.selectProductInfoById(14);
        Result<String> re = productService.buildXmlFile(productInfoResult.value());
        log.error("build xml file path ==========> {}", re.value());
    }

}