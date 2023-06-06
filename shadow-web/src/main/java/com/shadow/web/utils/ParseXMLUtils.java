package com.shadow.web.utils;

import com.cislc.shadow.manage.core.bean.entity.VideoAttr;
import com.shadow.web.model.enums.VersionEnum;
import com.shadow.web.model.xml.FieldDetail;
import com.shadow.web.model.xml.DatabaseField;
import com.shadow.web.model.xml.EntityDetail;
import com.shadow.web.model.xml.ShadowCode;
import com.cislc.shadow.utils.enums.Encryption;
import com.cislc.shadow.utils.enums.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.util.*;

import static com.shadow.web.consts.PackageConstants.ENTITY_NAME_PACKAGE_NAME;
import static com.shadow.web.consts.PackageConstants.ANDROID_ENTITY_PACKAGE_NAME;
import static com.shadow.web.utils.XmlConstants.*;
import static com.shadow.web.consts.JavaCodeConstants.*;

/**
 * 解析XML工具类
 *
 * @since 2019/5/18 10:28
 * @author szh
 **/
@Slf4j
public class ParseXMLUtils {

    /***
     * 校验xml文件格式
     *
     * @param xmlFile xml文件
     * @param xsdFile xsd文件
     * @return boolean 是否通过校验
     * @author szh
     * @since 2019/2/8 23:28
     */
    public static boolean domValidate(File xmlFile, File xsdFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(xmlFile);

            SchemaFactory constraintFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Source constraints = new StreamSource(xsdFile);
            Schema schema = constraintFactory.newSchema(constraints);

            Validator validator = schema.newValidator();

            try {
                validator.validate(new DOMSource(doc));
            } catch (org.xml.sax.SAXException e) {
                log.error("XML file validation error: " + e.getMessage());
                return false;
            }

        } catch (ParserConfigurationException e) {
            log.error("The underlying parser does not support the requested features.");
            return false;
        } catch (FactoryConfigurationError e) {
            log.error("Error occurred obtaining Document Builder Factory.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 由xml文件动态生成class代码
     *
     * @param xmlFile 用户上传的xml文件
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @param version 打包版本
     * @return java代码
     */
    public static ShadowCode xml2ClassCode(File xmlFile, Protocol protocol, Encryption encryption, VersionEnum version) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(xmlFile);
            if (null == document) {
                log.error("读取xml文件失败");
                return null;
            }

            // 代码
            Map<String, String> entityCode = new HashMap<>();   // 实体类源码
            Map<String, String> repositoryCode = new HashMap<>();   // 数据库映射源码
            Map<String, String> initCode = new HashMap<>();     // 初始化代码
            Map<String, String> entityNameCode = new HashMap<>();   // 实体名称代码
            Map<String, String> androidEntityCode = new HashMap<>();// 安卓实体代码
            Map<String, String> androidEntityNameCode = new HashMap<>();// 安卓实体名称代码

            Map<String, EntityDetail> entityAttr = new HashMap<>();  // 实体类名与对应属性
            List<String> deviceName = new ArrayList<>();    // 使用影子平台管理的设备类

            /* step 1. 解析xml属性 */
            resolveClassInfo(document.getRootElement(), deviceName, entityAttr);

            /* step 2. 生成实体代码、数据库映射代码及安卓实体代码，A版本中不需要数据持久化 */
            if (VersionEnum.A.equals(version)) {
                generateEntityCodeVersionA(entityAttr, entityCode, androidEntityCode, deviceName, protocol, encryption);
            } else {
                generateEntityCode(entityAttr, entityCode, repositoryCode, androidEntityCode, deviceName, protocol, encryption);
            }

            /* step 3. 生成初始化代码 */
            String init = CodeTemplateUtils.generateInitCode(entityAttr.keySet(), deviceName, protocol);
            initCode.put(WEB_CLASS_INIT, init);

            /* step 4. 生成实体名称代码 */
            List<String> entityNames = new ArrayList<>(entityAttr.keySet());
            entityNames.add(VideoAttr.class.getSimpleName());
            String entityName = CodeTemplateUtils.generateEntityNameCode(
                    entityNames, ENTITY_NAME_PACKAGE_NAME, deviceName.get(0), encryption);
            entityNameCode.put(WEB_CLASS_ENTITY_NAME, entityName);

            /* step 5. 生成安卓实体名称代码 */
            String androidEntityName = CodeTemplateUtils.generateEntityNameCode(
                    entityNames, ANDROID_ENTITY_PACKAGE_NAME, deviceName.get(0), null);
            androidEntityNameCode.put(WEB_CLASS_ENTITY_NAME, androidEntityName);

            // 返回源码
            if (VersionEnum.A.equals(version)) {
                return new ShadowCode(entityCode, null, null,
                        entityNameCode, androidEntityCode, androidEntityNameCode);
            } else {
                return new ShadowCode(entityCode, repositoryCode, initCode,
                        entityNameCode, androidEntityCode, androidEntityNameCode);
            }
        } catch (Exception e) {
            log.error("由xml文件动态生成class代码失败", e);
            return null;
        }
    }

    /**
     * 遍历节点下元素解析类信息
     *
     * @param root xml根节点
     * @param deviceName 设备名列表
     * @param entityAttr 实体类名与对应属性
     * @author szh
     * @since 2019/10/21 14:12
     */
    private static void resolveClassInfo(Element root,
                                         List<String> deviceName,
                                         Map<String, EntityDetail> entityAttr) {
        for (Iterator<Element> itClass = root.elementIterator(); itClass.hasNext(); ) {
            Element clazz = itClass.next();
            String className = clazz.attribute(ATTR_NAME).getValue();
            String tableName = clazz.attribute(ATTR_TABLE).getValue();
            Attribute classCommentAttr = clazz.attribute(ATTR_COMMENT);
            String classComment = classCommentAttr == null ? "" : classCommentAttr.getValue();

            List<FieldDetail> fieldList = new ArrayList<>(); // 类属性
            Map<String, DatabaseField> databaseFieldMap = new HashMap<>();  // 类属性与数据库字段映射关系

            // 1. 判断是否为设备对象
            Attribute isDeviceAttr = clazz.attribute(ATTR_DEVICE);
            boolean isDevice = null != isDeviceAttr && BOOLEAN_TRUE.equals(isDeviceAttr.getValue());
            if (isDevice) {
                // 设备对象增加deviceId, topic, ip, bindCode属性
                deviceName.add(className);
                fieldList.add(DeviceAttrUtils.getDeviceIdAttr());
                fieldList.add(DeviceAttrUtils.getIpAttr());
                fieldList.add(DeviceAttrUtils.getTopicAttr());
                fieldList.add(DeviceAttrUtils.getBindCodeAttr());
            }

            // 2. 遍历属性，记录属性名及类型，以及与数据库字段对应关系
            for (Iterator<Element> itField = clazz.elementIterator(); itField.hasNext(); ) {
                Element field = itField.next();
                String attrName = field.getText();
                dealClassAttribute(attrName, field, databaseFieldMap, fieldList);
            }

            // 3. 记录类的属性信息
            entityAttr.put(className, new EntityDetail(className, classComment, tableName, fieldList, databaseFieldMap));
        }
    }

    /**
     * 生成实体代码及数据库映射代码
     *
     * @param entityAttr 实体类名与对应属性
     * @param entityCode 实体代码
     * @param repositoryCode 数据库映射代码
     * @param androidEntityCode 安卓实体代码
     * @param deviceName 设备列表
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @author szh
     * @since 2019/10/21 14:18
     */
    private static void generateEntityCode(Map<String, EntityDetail> entityAttr,
                                           Map<String, String> entityCode,
                                           Map<String, String> repositoryCode,
                                           Map<String, String> androidEntityCode,
                                           List<String> deviceName,
                                           Protocol protocol,
                                           Encryption encryption) {
        // 安卓视频代码
        String androidVideoCode = CodeTemplateUtils.generateAndroidVideoAttr();
        androidEntityCode.put(VideoAttr.class.getSimpleName(), androidVideoCode);

        for (Map.Entry<String, EntityDetail> entry : entityAttr.entrySet()) {
            String className = entry.getKey();
            EntityDetail entityDetail = entry.getValue();
            boolean isDevice = deviceName.contains(className);

            // 实体类代码
            String sourceStr = CodeTemplateUtils.generateEntityCode(
                    className,
                    entityDetail.getTableName(),
                    entityDetail.getFieldDetailList(),
                    entityDetail.getDatabaseFieldMap(),
                    entityAttr.keySet(),
                    isDevice,
                    protocol,
                    encryption);
            entityCode.put(className, sourceStr);

            // 数据库映射代码
            String repositoryStr = CodeTemplateUtils.generateRepoCode(className);
            repositoryCode.put(DatabaseUtils.generateRepositoryName(className), repositoryStr);

            // 生成外键，安卓OneToOne的表需要外键字段
            List<FieldDetail> fieldDetailList = entityDetail.getFieldDetailList();
            Set<String> classSet = new HashSet<>(entityAttr.keySet());
            classSet.add(VideoAttr.class.getSimpleName());
            List<FieldDetail> foreignKeyFieldList = generateForeignKeyField(fieldDetailList, classSet);
            fieldDetailList.addAll(foreignKeyFieldList);
            // 安卓实体代码
            String androidStr = CodeTemplateUtils.generateAndroidEntity(
                    className,
                    fieldDetailList,
                    entityAttr.keySet(),
                    isDevice,
                    protocol,
                    encryption);
            androidEntityCode.put(className, androidStr);
        }
    }

    /**
     * 生成实体代码及数据库映射代码
     * A版本
     *
     * @param entityAttr 实体类名与对应属性
     * @param entityCode 实体代码
     * @param androidEntityCode 安卓实体代码
     * @param deviceName 设备列表
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @author szh
     * @since 2019/10/21 14:18
     */
    private static void generateEntityCodeVersionA(Map<String, EntityDetail> entityAttr,
                                           Map<String, String> entityCode,
                                           Map<String, String> androidEntityCode,
                                           List<String> deviceName,
                                           Protocol protocol,
                                           Encryption encryption) {
        // 安卓视频代码
        String androidVideoCode = CodeTemplateUtils.generateAndroidVideoAttrVersionA();
        androidEntityCode.put(VideoAttr.class.getSimpleName(), androidVideoCode);

        for (Map.Entry<String, EntityDetail> entry : entityAttr.entrySet()) {
            String className = entry.getKey();
            EntityDetail entityDetail = entry.getValue();
            boolean isDevice = deviceName.contains(className);

            // 实体类代码
            String sourceStr = CodeTemplateUtils.generateEntityCodeVersionA(
                    className,
                    entityDetail.getFieldDetailList(),
                    entityDetail.getDatabaseFieldMap(),
                    entityAttr.keySet(),
                    isDevice,
                    protocol,
                    encryption);
            entityCode.put(className, sourceStr);

            // 安卓实体代码
            String androidStr = CodeTemplateUtils.generateAndroidEntityVersionA(
                    className,
                    entityDetail.getFieldDetailList(),
                    entityAttr.keySet(),
                    isDevice,
                    protocol,
                    encryption);
            androidEntityCode.put(className, androidStr);
        }
    }

    /**
     * 解析类属性
     *
     * @param attrName 属性名
     * @param field xml节点
     * @param databaseFieldMap 数据库字段对应关系
     * @param fieldList 类属性
     * @author szh
     * @since 2019/6/11 18:37
     */
    private static void dealClassAttribute(String attrName,
                                    Element field,
                                    Map<String, DatabaseField> databaseFieldMap,
                                    List<FieldDetail> fieldList) {
        switch (field.getName()) {
            // id
            case ELEMENT_ID:
                fieldList.add(new FieldDetail("int", attrName, "id"));
                Attribute idAttrTable = field.attribute(ATTR_TABLE);
                Attribute idAttrColumn = field.attribute(ATTR_COLUMN);
                String idTable = "";
                if (null != idAttrTable) {
                    idTable = idAttrTable.getValue();
                }
                String idColumn = "";
                if (null != idAttrColumn) {
                    idColumn = idAttrColumn.getValue();
                }
                databaseFieldMap.put(ELEMENT_ID, new DatabaseField(idTable, idColumn));
                break;

            // 普通属性
            case ELEMENT_FIELD:
                Attribute fieldAttrType = field.attribute(ATTR_TYPE);
                Attribute fieldAttrComment = field.attribute(ATTR_COMMENT);
                String fieldComment = fieldAttrComment == null ? "" : fieldAttrComment.getValue();
                if ( null != fieldAttrType) {
                    fieldList.add(new FieldDetail(fieldAttrType.getValue(), attrName, fieldComment));
                }
                // 数据库字段对应关系
                Attribute fieldAttrTable = field.attribute(ATTR_TABLE);
                Attribute fieldAttrColumn = field.attribute(ATTR_COLUMN);
                String fieldTable = "";
                if (null != fieldAttrTable) {
                    fieldTable = fieldAttrTable.getValue();
                }
                String fieldColumn = "";
                if (null != fieldAttrColumn) {
                    fieldColumn = fieldAttrColumn.getValue();
                }
                databaseFieldMap.put(attrName, new DatabaseField(fieldTable, fieldColumn));
                break;

            // 列表属性
            case ELEMENT_LIST:
                Attribute listAttrType = field.attribute(ATTR_TYPE);
                Attribute listAttrComment = field.attribute(ATTR_COMMENT);
                String listComment = listAttrComment == null ? "" : listAttrComment.getValue();
                if (null != listAttrType) {
                    fieldList.add(new FieldDetail("List<" + listAttrType.getValue() + ">", attrName, listComment));
                }
                break;

            // map属性
            case ELEMENT_MAP:
                Attribute mapAttrKey = field.attribute(ATTR_KEY);
                Attribute mapAttrValue = field.attribute(ATTR_VALUE);
                Attribute mapAttrComment = field.attribute(ATTR_COMMENT);
                String mapComment = mapAttrComment == null ? "" : mapAttrComment.getValue();
                if (null != mapAttrKey && null != mapAttrValue) {
                    fieldList.add(new FieldDetail(
                            "Map<" + mapAttrKey.getValue() + ", " + mapAttrValue.getValue() + ">",
                            attrName, mapComment));
                }
                break;

            // 视频属性
            case ELEMENT_VIDEO:
                Attribute videoAttrComment = field.attribute(ATTR_COMMENT);
                String videoComment = videoAttrComment == null ? "" : videoAttrComment.getValue();
                fieldList.add(new FieldDetail(VideoAttr.class.getSimpleName(), attrName, videoComment));
                break;

            default:
                break;
        }
    }

    /**
     * 生成外键字段
     *
     * @param fieldList 类的属性
     * @param classSet 实体集合
     * @return 外键字段
     * @author szh
     * @since 2020/12/8 15:43
     */
    private static List<FieldDetail> generateForeignKeyField(List<FieldDetail> fieldList, Set<String> classSet) {
        List<FieldDetail> foreignKeyFieldList = new ArrayList<>();
        for (FieldDetail fieldDetail : fieldList) {
            String type = fieldDetail.getFieldType();
            String fieldName = fieldDetail.getFieldName();
            if (classSet.contains(type)) {
                String foreignKey = CodeTemplateUtils.generateAndroidForeignKey(fieldName);
                foreignKeyFieldList.add(new FieldDetail("String", foreignKey, null));
            }
        }
        return foreignKeyFieldList;
    }

}
