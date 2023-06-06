package com.shadow.web.utils;

import com.alibaba.fastjson.annotation.JSONField;
import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.bean.entity.VideoAttr;
import com.cislc.shadow.manage.core.bean.field.EntityField;
import com.cislc.shadow.manage.core.shadow.EntityFactory;
import com.cislc.shadow.manage.core.shadow.ShadowFactory;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import com.shadow.web.model.xml.FieldDetail;
import com.shadow.web.model.xml.DatabaseField;
import com.squareup.javapoet.*;
import com.cislc.shadow.utils.enums.Encryption;
import com.cislc.shadow.utils.enums.Protocol;
import com.cislc.shadow.utils.mqtt.TopicUtils;
import com.cislc.shadow.utils.mqtts.MqttFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.lang.model.element.Modifier;
import javax.persistence.*;
import java.util.*;

import static com.shadow.web.consts.PackageConstants.*;
import static com.shadow.web.consts.JavaCodeConstants.*;

/**
 * 代码模板生成工具
 *
 * @since 2019/7/4 15:36
 * @author szh
 **/
@Slf4j
class CodeTemplateUtils {

    /**
     * 由属性及数据类型键值对生成java代码
     *
     * @param className 类名
     * @param fieldList 属性定义
     * @param databaseFieldMap 数据库字段映射
     * @param classSet 所有实体类名
     * @param isDevice 是否为设备
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @return java代码
     * @author szh
     * @since 2019/7/7 22:21
     */
    static String generateEntityCode(String className,
                                     String tableName,
                                     List<FieldDetail> fieldList,
                                     Map<String, DatabaseField> databaseFieldMap,
                                     Set<String> classSet,
                                     boolean isDevice,
                                     Protocol protocol,
                                     Encryption encryption) {
        log.info("生成java代码：className={}, property={}", className, fieldList);
        try {
            // 生成类
            AnnotationSpec tableAnno = AnnotationSpec
                    .builder(Table.class)
                    .addMember("name", "$S", tableName)
                    .build();
            TypeSpec.Builder entityBuilder = TypeSpec.classBuilder(className)
                    .addAnnotation(Entity.class)
                    .addAnnotation(tableAnno)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ShadowEntity.class);

            // 数据库字段初始化静态代码
            CodeBlock.Builder databaseStaticBuilder = CodeBlock.builder();
            for (String field : databaseFieldMap.keySet()) {
                DatabaseField databaseField = databaseFieldMap.get(field);
                databaseStaticBuilder.addStatement("databaseFieldMap.put($S, new DatabaseField($S, $S))",
                        field, databaseField.getTable(), databaseField.getColumn());
            }
            CodeBlock databaseStatic = databaseStaticBuilder.build();
            entityBuilder.addStaticBlock(databaseStatic);

            // 数据库字段映射属性
            FieldSpec databaseMap = FieldSpec
                    .builder(ParameterizedTypeName.get(Map.class, String.class, com.cislc.shadow.manage.core.bean.field.DatabaseField.class), WEB_PROPERTY_DATABASE_FIELD_MAP)
                    .addAnnotation(Transient.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T<>()", HashMap.class)
                    .build();
            entityBuilder.addField(databaseMap);

            // 构造方法
            MethodSpec blankConstructor = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super()")
                    .build();
            entityBuilder.addMethod(blankConstructor);
            MethodSpec topicConstructor = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, COMMON_PROPERTY_DEVICE_ID)
                    .addStatement("super($L)", COMMON_PROPERTY_DEVICE_ID)
                    .addStatement("setDeviceId($L)", COMMON_PROPERTY_DEVICE_ID)
                    .build();
            entityBuilder.addMethod(topicConstructor);

            AnnotationSpec jsonAnno = AnnotationSpec
                    .builder(JSONField.class)
                    .addMember("serialize", "false")
                    .build();
            // 设备实体增加通信协议和加密算法字段getter
            if (isDevice) {
                MethodSpec protocolGetterMethod = MethodSpec
                        .methodBuilder(COMMON_METHOD_GET_PROTOCOL)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Protocol.class)
                        .addStatement("return $T.$L", Protocol.class, protocol)
                        .addAnnotation(jsonAnno)
                        .addAnnotation(Transient.class)
                        .addAnnotation(Override.class)
                        .build();
                entityBuilder.addMethod(protocolGetterMethod);

                MethodSpec encryptionGetterMethod = MethodSpec
                        .methodBuilder(COMMON_METHOD_GET_ENCRYPTION)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Encryption.class)
                        .addStatement("return $T.$L", Encryption.class, encryption)
                        .addAnnotation(jsonAnno)
                        .addAnnotation(Transient.class)
                        .addAnnotation(Override.class)
                        .build();
                entityBuilder.addMethod(encryptionGetterMethod);
            }

            // 增加是否为设备getter
            MethodSpec isDeviceMethod = MethodSpec
                    .methodBuilder(WEB_METHOD_IS_DEVICE)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(boolean.class)
                    .addStatement("return " + isDevice)
                    .addAnnotation(jsonAnno)
                    .addAnnotation(Transient.class)
                    .addAnnotation(Override.class)
                    .build();
            entityBuilder.addMethod(isDeviceMethod);

            // 属性 & getter & setter
            for (FieldDetail attr : fieldList) {
                // 1. 属性字段
                String type = attr.getFieldType(); // 属性类型
                String fieldName = attr.getFieldName();  // 属性名
                String comment = attr.getFieldComment(); // 注释
                boolean isList = type.startsWith("List");
                FieldSpec field;
                ParameterSpec setterParam;
                TypeName getterType;

                if (isList) {
                    // list属性增加表的关联映射
                    String entityType = type.substring(type.indexOf("<") + 1, type.indexOf(">"));

                    AnnotationSpec oneToManyAnno = AnnotationSpec
                            .builder(OneToMany.class)
                            .addMember("fetch", "$T.EAGER", FetchType.class)
                            .addMember("cascade", "$T.ALL", CascadeType.class)
                            .build();
                    AnnotationSpec joinColumnAnno = AnnotationSpec
                            .builder(JoinColumn.class)
                            .addMember("name", "$S", DatabaseUtils.generateForeignKey(className, fieldName))
                            .build();
                    field = FieldSpec
                            .builder(ParameterizedTypeName.get(
                                    ClassName.get(Set.class),
                                    ClassName.get(ENTITY_PACKAGE_NAME, entityType)),
                                    fieldName)
                            //.addJavadoc(comment + "\n")
                            .addAnnotation(oneToManyAnno)
                            .addAnnotation(joinColumnAnno)
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    getterType = ParameterizedTypeName.get(
                            ClassName.get(Set.class),
                            ClassName.get(ENTITY_PACKAGE_NAME, entityType));
                    setterParam = ParameterSpec
                            .builder(getterType, fieldName)
                            .build();
                } else if (classSet.contains(type)) {
                    // 嵌套单个实体增加映射
                    AnnotationSpec oneToOneAnno = AnnotationSpec
                            .builder(OneToOne.class)
                            .addMember("fetch", "$T.EAGER", FetchType.class)
                            .build();
                    field = FieldSpec
                            .builder(ClassName.get(ENTITY_PACKAGE_NAME, type), fieldName)
                            //.addJavadoc(comment + "\n")
                            .addAnnotation(oneToOneAnno)
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    getterType = ClassName.get(ENTITY_PACKAGE_NAME, type);
                    setterParam = ParameterSpec
                            .builder(ClassName.get(ENTITY_PACKAGE_NAME, type), fieldName)
                            .build();
                } else if (VideoAttr.class.getSimpleName().equals(type)) {
                    // 视频属性
                    AnnotationSpec oneToOneAnno = AnnotationSpec
                            .builder(OneToOne.class)
                            .addMember("fetch", "$T.EAGER", FetchType.class)
                            .build();
                    field = FieldSpec
                            .builder(VideoAttr.class, fieldName)
                            //.addJavadoc(comment + "\n")
                            .addAnnotation(oneToOneAnno)
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    getterType = ClassName.get(VideoAttr.class);
                    setterParam = ParameterSpec.builder(VideoAttr.class, fieldName).build();
                } else {
                    // 普通属性
                    TypeName typeName = getTypeName(type);
                    FieldSpec.Builder builder = FieldSpec
                            .builder(typeName, fieldName)
                            //.addJavadoc(comment + "\n")
                            .addModifiers(Modifier.PRIVATE);

                    if (databaseFieldMap.containsKey(fieldName)) {
                        AnnotationSpec columnAnno = AnnotationSpec
                                .builder(Column.class)
                                .addMember("name", "$S", databaseFieldMap.get(fieldName).getColumn())
                                .build();
                        builder.addAnnotation(columnAnno);
                    }

                    field = builder.build();

                    getterType = typeName;
                    setterParam = ParameterSpec.builder(typeName, fieldName).build();
                }
                entityBuilder.addField(field);

                // 2. getter
                addGetterMethod(entityBuilder, getterType, JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_GETTER), fieldName, comment);

                // 3. setter
                addSetterMethod(entityBuilder, setterParam, fieldName, isList, className, comment);
            }

            TypeSpec entity = entityBuilder.build();
            JavaFile javaFile = JavaFile.builder(ENTITY_PACKAGE_NAME, entity).build();

            return javaFile.toString();
        } catch (Exception e) {
            log.error("生成java代码失败", e);
            return null;
        }
    }

    /**
     * 由属性及数据类型键值对生成java代码
     *
     * @param className 类名
     * @param fieldList 属性定义
     * @param databaseFieldMap 数据库字段映射
     * @param classSet 所有实体类名
     * @param isDevice 是否为设备
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @return java代码
     * @author szh
     * @since 2019/7/7 22:21
     */
    static String generateEntityCodeVersionA(String className,
                                     List<FieldDetail> fieldList,
                                     Map<String, DatabaseField> databaseFieldMap,
                                     Set<String> classSet,
                                     boolean isDevice,
                                     Protocol protocol,
                                     Encryption encryption) {
        log.info("生成java代码：className={}, property={}", className, fieldList);
        try {
            // 生成类
            TypeSpec.Builder entityBuilder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ShadowEntity.class);

            // 数据库字段初始化静态代码
            CodeBlock.Builder databaseStaticBuilder = CodeBlock.builder();
            for (String field : databaseFieldMap.keySet()) {
                DatabaseField databaseField = databaseFieldMap.get(field);
                databaseStaticBuilder.addStatement("databaseFieldMap.put($S, new DatabaseField($S, $S))",
                        field, databaseField.getTable(), databaseField.getColumn());
            }
            CodeBlock databaseStatic = databaseStaticBuilder.build();
            entityBuilder.addStaticBlock(databaseStatic);

            // 数据库字段映射属性
            FieldSpec databaseMap = FieldSpec
                    .builder(ParameterizedTypeName.get(Map.class, String.class, com.cislc.shadow.manage.core.bean.field.DatabaseField.class), WEB_PROPERTY_DATABASE_FIELD_MAP)
                    .addAnnotation(Transient.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T<>()", HashMap.class)
                    .build();
            entityBuilder.addField(databaseMap);

            // 构造方法
            MethodSpec blankConstructor = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("super()")
                    .build();
            entityBuilder.addMethod(blankConstructor);
            MethodSpec topicConstructor = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, COMMON_PROPERTY_DEVICE_ID)
                    .addStatement("super($L)", COMMON_PROPERTY_DEVICE_ID)
                    .addStatement("setDeviceId($L)", COMMON_PROPERTY_DEVICE_ID)
                    .build();
            entityBuilder.addMethod(topicConstructor);

            AnnotationSpec jsonAnno = AnnotationSpec
                    .builder(JSONField.class)
                    .addMember("serialize", "false")
                    .build();
            // 设备实体增加通信协议和加密算法字段getter
            if (isDevice) {
                MethodSpec protocolGetterMethod = MethodSpec
                        .methodBuilder(COMMON_METHOD_GET_PROTOCOL)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Protocol.class)
                        .addStatement("return $T.$L", Protocol.class, protocol)
                        .addAnnotation(jsonAnno)
                        .addAnnotation(Transient.class)
                        .addAnnotation(Override.class)
                        .build();
                entityBuilder.addMethod(protocolGetterMethod);

                MethodSpec encryptionGetterMethod = MethodSpec
                        .methodBuilder(COMMON_METHOD_GET_ENCRYPTION)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(Encryption.class)
                        .addStatement("return $T.$L", Encryption.class, encryption)
                        .addAnnotation(jsonAnno)
                        .addAnnotation(Transient.class)
                        .addAnnotation(Override.class)
                        .build();
                entityBuilder.addMethod(encryptionGetterMethod);
            }

            // 增加是否为设备getter
            MethodSpec isDeviceMethod = MethodSpec
                    .methodBuilder(WEB_METHOD_IS_DEVICE)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(boolean.class)
                    .addStatement("return " + isDevice)
                    .addAnnotation(jsonAnno)
                    .addAnnotation(Transient.class)
                    .addAnnotation(Override.class)
                    .build();
            entityBuilder.addMethod(isDeviceMethod);

            // 属性 & getter & setter
            for (FieldDetail attr : fieldList) {
                // 1. 属性字段
                String type = attr.getFieldType(); // 属性类型
                String fieldName = attr.getFieldName();  // 属性名
                String comment = attr.getFieldComment(); // 注释
                boolean isList = type.startsWith("List");
                FieldSpec field;
                ParameterSpec setterParam;
                TypeName getterType;

                if (isList) {
                    // list属性增加表的关联映射
                    String entityType = type.substring(type.indexOf("<") + 1, type.indexOf(">"));

                    field = FieldSpec
                            .builder(ParameterizedTypeName.get(
                                    ClassName.get(Set.class),
                                    ClassName.get(ENTITY_PACKAGE_NAME, entityType)),
                                    fieldName)
                            //.addJavadoc(comment + "\n")
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    getterType = ParameterizedTypeName.get(
                            ClassName.get(Set.class),
                            ClassName.get(ENTITY_PACKAGE_NAME, entityType));
                    setterParam = ParameterSpec
                            .builder(getterType, fieldName)
                            .build();
                } else if (classSet.contains(type)) {
                    // 嵌套单个实体增加映射
                    field = FieldSpec
                            .builder(ClassName.get(ENTITY_PACKAGE_NAME, type), fieldName)
                            //.addJavadoc(comment + "\n")
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    getterType = ClassName.get(ENTITY_PACKAGE_NAME, type);
                    setterParam = ParameterSpec
                            .builder(ClassName.get(ENTITY_PACKAGE_NAME, type), fieldName)
                            .build();
                } else if (VideoAttr.class.getSimpleName().equals(type)) {
                    // 视频属性
                    field = FieldSpec
                            .builder(VideoAttr.class, fieldName)
                            //.addJavadoc(comment + "\n")
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    getterType = ClassName.get(VideoAttr.class);
                    setterParam = ParameterSpec.builder(VideoAttr.class, fieldName).build();
                } else {
                    // 普通属性
                    TypeName typeName = getTypeName(type);
                    FieldSpec.Builder builder = FieldSpec
                            .builder(typeName, fieldName)
                            //.addJavadoc(comment + "\n")
                            .addModifiers(Modifier.PRIVATE);

                    field = builder.build();

                    getterType = typeName;
                    setterParam = ParameterSpec.builder(typeName, fieldName).build();
                }
                entityBuilder.addField(field);

                // 2. getter
                addGetterMethod(entityBuilder, getterType, JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_GETTER), fieldName, comment);

                // 3. setter
                addSetterMethod(entityBuilder, setterParam, fieldName, isList, className, comment);
            }

            TypeSpec entity = entityBuilder.build();
            JavaFile javaFile = JavaFile.builder(ENTITY_PACKAGE_NAME, entity).build();

            return javaFile.toString();
        } catch (Exception e) {
            log.error("生成java代码失败", e);
            return null;
        }
    }

    /**
     * 生成数据库映射代码
     *
     * @param entityName 实体名
     * @return java代码
     * @author szh
     * @since 2019/7/8 13:58
     */
    static String generateRepoCode(String entityName) {
        log.info("生成数据库映射代码：entityName={}", entityName);
        try {
            String repoName = DatabaseUtils.generateRepositoryName(entityName);

            TypeSpec repoInter = TypeSpec
                    .interfaceBuilder(repoName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ParameterizedTypeName.get(
                            ClassName.get(JpaRepository.class),
                            ClassName.get(ENTITY_PACKAGE_NAME, entityName),
                            ClassName.get(Integer.class)))
                    .build();

            JavaFile javaFile = JavaFile.builder(REPOSITORY_PACKAGE_NAME, repoInter).build();
            return javaFile.toString();
        } catch (Exception e) {
            log.error("生成数据库映射代码失败", e);
            return null;
        }
    }

    /**
     * 生成初始化代码
     *
     * @param entityList 实体list
     * @param deviceList 设备list
     * @return java代码
     * @author szh
     * @since 2019/7/8 13:59
     */
    static String generateInitCode(Set<String> entityList, List<String> deviceList, Protocol protocol) {
        log.info("生成初始化代码：entityList={}", entityList);
        try {
            // 执行顺序
            AnnotationSpec orderAnno = AnnotationSpec
                    .builder(Order.class)
                    .addMember("value", "$L", 2)
                    .build();
            // 生成类
            TypeSpec.Builder initBuilder = TypeSpec
                    .classBuilder(WEB_CLASS_INIT)
                    .addAnnotation(orderAnno)
                    .addAnnotation(Component.class)
                    .addAnnotation(Slf4j.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(CommandLineRunner.class);

            // 是否自动注入
            AnnotationSpec valueAnno = AnnotationSpec
                    .builder(Value.class)
                    .addMember("value", "$S", "${shadow.auto-init:false}")
                    .build();
            FieldSpec autoInitField = FieldSpec
                    .builder(Boolean.class, WEB_PROPERTY_AUTO_INIT)
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(valueAnno)
                    .build();
            initBuilder.addField(autoInitField);

            // repository属性
            for (String entityName : entityList) {
                String repoType = DatabaseUtils.generateRepositoryName(entityName);
                String repoName = JavaFileUtils.generateFieldName(repoType);
                FieldSpec repositoryField = FieldSpec
                        .builder(ClassName.get(REPOSITORY_PACKAGE_NAME, repoType), repoName)
                        .addAnnotation(Autowired.class)
                        .addModifiers(Modifier.PRIVATE)
                        .build();
                initBuilder.addField(repositoryField);
            }

            // 初始化方法
            MethodSpec.Builder initMethodBuilder = MethodSpec
                    .methodBuilder("run")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String[].class, "args")
                    .addException(Exception.class)
                    .beginControlFlow("if (autoInit)")
                    .addStatement("$T dataMap = new $T<>()", ParameterizedTypeName.get(Map.class, String.class, ShadowEntity.class), HashMap.class);

            // 获取设备
            for (String device : deviceList) {
                String deviceRepoName = JavaFileUtils.generateFieldName(DatabaseUtils.generateRepositoryName(device));
                String listName = JavaFileUtils.generateFieldName(device) + "List";
                initMethodBuilder.addStatement("$T " + listName + " = $L.findAll()",
                        ParameterizedTypeName.get(
                                ClassName.get(List.class),
                                ClassName.get(ENTITY_PACKAGE_NAME, device)),
                        deviceRepoName);
            }

            // 删除自动注入的实体，防止重复注入
            initMethodBuilder.addStatement("$T.destroyEntities()", EntityFactory.class);
            initMethodBuilder.addStatement("$T entityNames = $T.getAllEntityName()",
                    ParameterizedTypeName.get(List.class, String.class),
                    com.cislc.shadow.manage.common.utils.ClassUtils.class);

            // 注入全部设备
            for (String device : deviceList) {
                String deviceName = JavaFileUtils.generateFieldName(device);
                String listName = JavaFileUtils.generateFieldName(device) + "List";
                initMethodBuilder.beginControlFlow("for ($T $L : $L)", ClassName.get(ENTITY_PACKAGE_NAME, device), deviceName, listName);
                initMethodBuilder.addStatement("dataMap.put($L.getTopic(), $L)", deviceName, deviceName);
                initMethodBuilder.addStatement("$T.injectEntities($L, $L.getTopic(), entityNames)", EntityFactory.class, deviceName, deviceName);
                initMethodBuilder.endControlFlow();
            }

            initMethodBuilder.addStatement("boolean injectResult = $T.batchInjectShadow(dataMap)", ShadowFactory.class);

            // 如果是mqtt协议则订阅初始化主题
            if (Protocol.MQTT.equals(protocol)) {
                initMethodBuilder.addStatement("$T.subscribe($S, 0)", MqttFactory.class, TopicUtils.getUpdateTopic(ShadowConst.BIND_TOPIC));
            }

            initMethodBuilder.endControlFlow();
            initBuilder.addMethod(initMethodBuilder.build());

            JavaFile javaFile = JavaFile.builder(INIT_PACKAGE_NAME, initBuilder.build()).build();

            return javaFile.toString();
        } catch (Exception e) {
            log.error("生成初始化代码失败", e);
            return null;
        }
    }

    /**
     * 生成实体类名常量接口
     *
     * @param entityName 实体类名
     * @param packageName 包名
     * @return java代码
     * @author szh
     * @since 2019/10/21 16:24
     */
    static String generateEntityNameCode(List<String> entityName, String packageName, String deviceName, Encryption encryption) {
        log.info("生成实体类名常量接口：entityName={}", entityName);
        try {
            TypeSpec.Builder nameBuilder = TypeSpec
                    .interfaceBuilder(COMMON_CLASS_ENTITY_NAMES)
                    .addModifiers(Modifier.PUBLIC);

            // 拼接实体类名
            StringBuilder sb = new StringBuilder();
            for (String name : entityName) {
                sb.append("\"").append(name).append("\",");
            }
            String nameStr = sb.substring(0, sb.length() - 1);
            String listInit = "$T.asList(" + nameStr + ")";

            FieldSpec nameField = FieldSpec
                    .builder(ParameterizedTypeName.get(List.class, String.class), COMMON_PROPERTY_ENTITY_NAMES)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer(listInit, Arrays.class)
                    .build();
            nameBuilder.addField(nameField);

            // 设备类名
            FieldSpec deviceField = FieldSpec
                    .builder(String.class, COMMON_PROPERTY_DEVICE_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", deviceName)
                    .build();
            nameBuilder.addField(deviceField);

            if (encryption != null) {
                // 加密算法
                FieldSpec encryptionField = FieldSpec
                        .builder(Encryption.class, COMMON_PROPERTY_ENCRYPTION)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$T.$L", Encryption.class, encryption)
                        .build();
                nameBuilder.addField(encryptionField);
            }

            return JavaFile.builder(packageName, nameBuilder.build()).build().toString();
        } catch (Exception e) {
            log.error("生成实体类名常量接口失败", e);
            return null;
        }
    }

    /**
     * 生成外键
     *
     * @param fieldName 属性名
     * @return 外键名
     * @author szh
     * @since 2020/12/8 15:49
     */
    static String generateAndroidForeignKey(String fieldName) {
        return fieldName + "Sri";
    }

    /**
     * 生成安卓实体代码
     *
     * @param className 类名
     * @param fieldList 属性
     * @param classSet 实体集合
     * @param isDevice 是否是设备
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @return 代码
     * @author szh
     * @since 2019/12/23 16:56
     */
    static String generateAndroidEntity(String className,
                                        List<FieldDetail> fieldList,
                                        Set<String> classSet,
                                        boolean isDevice,
                                        Protocol protocol,
                                        Encryption encryption) {
        log.info("生成安卓实体代码：className={}, property={}", className, fieldList);
        try {
            // 生成类
            AnnotationSpec classAnno = AnnotationSpec
                    .builder(org.greenrobot.greendao.annotation.Entity.class)
                    .addMember("nameInDb", "$S", className)
                    .build();
            TypeSpec.Builder entityBuilder = TypeSpec.classBuilder(className)
                    .addAnnotation(classAnno)
                    .addModifiers(Modifier.PUBLIC);

            // sri
            FieldSpec sriField = FieldSpec
                    .builder(String.class, COMMON_PROPERTY_SRI)
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(org.greenrobot.greendao.annotation.Id.class)
                    .build();
            entityBuilder.addField(sriField);
            // getter
            addGetterMethod(entityBuilder, ClassName.get(String.class), COMMON_METHOD_GET_SRI, COMMON_PROPERTY_SRI, null);
            // setter
            ParameterSpec sriParam = ParameterSpec.builder(String.class, COMMON_PROPERTY_SRI).build();
            addSetterMethod(entityBuilder, sriParam, COMMON_METHOD_SET_SRI, COMMON_PROPERTY_SRI);

            if (!isDevice) {
                // 不是device则加入parentSri属性
                FieldSpec parentField = FieldSpec
                        .builder(String.class, ANDROID_PROPERTY_PARENT_SRI)
                        .addModifiers(Modifier.PRIVATE)
                        .build();
                entityBuilder.addField(parentField);
                addGetterMethod(entityBuilder, ClassName.get(String.class), ANDROID_METHOD_GET_PARENT_SRI, ANDROID_PROPERTY_PARENT_SRI, null);
                ParameterSpec parentSriParam = ParameterSpec.builder(String.class, ANDROID_PROPERTY_PARENT_SRI).build();
                addSetterMethod(entityBuilder, parentSriParam, ANDROID_METHOD_SET_PARENT_SRI, ANDROID_PROPERTY_PARENT_SRI);
            } else {
                // 设备实体增加通信协议和加密字段getter
                MethodSpec protocolGetterMethod = MethodSpec
                        .methodBuilder(COMMON_METHOD_GET_PROTOCOL)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return \"$L\"", protocol.getName())
                        .build();
                entityBuilder.addMethod(protocolGetterMethod);

                MethodSpec encryptionGetterMethod = MethodSpec
                        .methodBuilder(COMMON_METHOD_GET_ENCRYPTION)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return \"$L\"", encryption.getName())
                        .build();
                entityBuilder.addMethod(encryptionGetterMethod);
            }

            // 普通属性
            for (FieldDetail fieldDetail : fieldList) {
                String type = fieldDetail.getFieldType();
                String fieldName = fieldDetail.getFieldName();
                FieldSpec field;

                if (type.startsWith("List")) {
                    // list属性增加表的关联映射
                    String entityType = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
                    AnnotationSpec toManyAnno = AnnotationSpec
                            .builder(org.greenrobot.greendao.annotation.ToMany.class)
                            .addMember("referencedJoinProperty", "$S", ANDROID_PROPERTY_PARENT_SRI)
                            .build();
                    TypeName getterType = ParameterizedTypeName.get(
                            ClassName.get(List.class),
                            ClassName.get(ANDROID_ENTITY_PACKAGE_NAME, entityType));
                    field = FieldSpec
                            .builder(getterType, fieldName)
                            .addAnnotation(toManyAnno)
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    ParameterSpec setterParam = ParameterSpec
                            .builder(getterType, fieldName)
                            .build();
                    addSetterMethod(entityBuilder, setterParam, JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_SETTER), fieldName);
                } else if (classSet.contains(type)) {
                    // 嵌套单个实体增加映射
                    String foreignKey = generateAndroidForeignKey(fieldName);
                    AnnotationSpec oneToOneAnno = AnnotationSpec
                            .builder(org.greenrobot.greendao.annotation.ToOne.class)
                            .addMember("joinProperty", "$S", foreignKey)
                            .build();
                    field = FieldSpec
                            .builder(ClassName.get(ANDROID_ENTITY_PACKAGE_NAME, type), fieldName)
                            .addAnnotation(oneToOneAnno)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                } else if (VideoAttr.class.getSimpleName().equals(type)) {
                    String foreignKey = generateAndroidForeignKey(fieldName);
                    AnnotationSpec oneToOneAnno = AnnotationSpec
                            .builder(org.greenrobot.greendao.annotation.ToOne.class)
                            .addMember("joinProperty", "$S", foreignKey)
                            .build();
                    field = FieldSpec
                            .builder(ClassName.get(ANDROID_ENTITY_PACKAGE_NAME, VideoAttr.class.getSimpleName()), fieldName)
                            .addAnnotation(oneToOneAnno)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                } else {
                    // 普通属性
                    TypeName typeName = getTypeName(type);
                    field = FieldSpec
                            .builder(typeName, fieldName)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                    // 2. getter
                    addGetterMethod(entityBuilder, typeName, JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_GETTER), fieldName, null);
                    // 3. setter
                    ParameterSpec setterParam = ParameterSpec.builder(typeName, fieldName).build();
                    addSetterMethod(entityBuilder, setterParam, JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_SETTER), fieldName);
                }
                entityBuilder.addField(field);
            }

            TypeSpec entity = entityBuilder.build();
            JavaFile javaFile = JavaFile.builder(ANDROID_ENTITY_PACKAGE_NAME, entity).build();
            return javaFile.toString();
        } catch (Exception e) {
            log.error("生成安卓实体代码失败", e);
            return null;
        }
    }

    /**
     * 生成安卓实体代码
     *
     * @param className 类名
     * @param fieldList 属性
     * @param classSet 实体集合
     * @param isDevice 是否是设备
     * @param protocol 通信协议
     * @param encryption 加密算法
     * @return 代码
     * @author szh
     * @since 2019/12/23 16:56
     */
    static String generateAndroidEntityVersionA(String className,
                                                List<FieldDetail> fieldList,
                                                Set<String> classSet,
                                                boolean isDevice,
                                                Protocol protocol,
                                                Encryption encryption) {
        log.info("生成安卓实体代码：className={}, property={}", className, fieldList);
        try {
            // 生成类
            TypeSpec.Builder entityBuilder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC);

            // sri
            FieldSpec sriField = FieldSpec
                    .builder(String.class, COMMON_PROPERTY_SRI)
                    .addModifiers(Modifier.PRIVATE)
                    .build();
            entityBuilder.addField(sriField);
            // getter
            addGetterMethod(entityBuilder, ClassName.get(String.class), COMMON_METHOD_GET_SRI, COMMON_PROPERTY_SRI, null);
            // setter
            ParameterSpec sriParam = ParameterSpec.builder(String.class, COMMON_PROPERTY_SRI).build();
            addSetterMethod(entityBuilder, sriParam, COMMON_METHOD_SET_SRI, COMMON_PROPERTY_SRI);

            if (!isDevice) {
                // 不是device则加入parentSri属性
                FieldSpec parentField = FieldSpec
                        .builder(String.class, ANDROID_PROPERTY_PARENT_SRI)
                        .addModifiers(Modifier.PRIVATE)
                        .build();
                entityBuilder.addField(parentField);
                addGetterMethod(entityBuilder, ClassName.get(String.class), ANDROID_METHOD_GET_PARENT_SRI, ANDROID_PROPERTY_PARENT_SRI, null);
                ParameterSpec parentSriParam = ParameterSpec.builder(String.class, ANDROID_PROPERTY_PARENT_SRI).build();
                addSetterMethod(entityBuilder, parentSriParam, ANDROID_METHOD_SET_PARENT_SRI, ANDROID_PROPERTY_PARENT_SRI);
            } else {
                // 设备实体增加通信协议和加密字段getter
                MethodSpec protocolGetterMethod = MethodSpec
                        .methodBuilder(COMMON_METHOD_GET_PROTOCOL)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return \"$L\"", protocol.getName())
                        .build();
                entityBuilder.addMethod(protocolGetterMethod);

                MethodSpec encryptionGetterMethod = MethodSpec
                        .methodBuilder(COMMON_METHOD_GET_ENCRYPTION)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return \"$L\"", encryption.getName())
                        .build();
                entityBuilder.addMethod(encryptionGetterMethod);
            }

            // 普通属性
            for (FieldDetail fieldDetail : fieldList) {
                String type = fieldDetail.getFieldType();
                String fieldName = fieldDetail.getFieldName();
                FieldSpec field;

                if (type.startsWith("List")) {
                    // list属性增加表的关联映射
                    String entityType = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
                    TypeName getterType = ParameterizedTypeName.get(
                            ClassName.get(List.class),
                            ClassName.get(ANDROID_ENTITY_PACKAGE_NAME, entityType));
                    field = FieldSpec
                            .builder(getterType, fieldName)
                            .addModifiers(Modifier.PRIVATE)
                            .build();

                    ParameterSpec setterParam = ParameterSpec
                            .builder(getterType, fieldName)
                            .build();
                    addSetterMethod(entityBuilder, setterParam, JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_SETTER), fieldName);
                } else if (classSet.contains(type)) {
                    // 嵌套单个实体增加映射
                    field = FieldSpec
                            .builder(ClassName.get(ANDROID_ENTITY_PACKAGE_NAME, type), fieldName)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                } else if (VideoAttr.class.getSimpleName().equals(type)) {
                    field = FieldSpec
                            .builder(ClassName.get(ANDROID_ENTITY_PACKAGE_NAME, VideoAttr.class.getSimpleName()), fieldName)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                } else {
                    // 普通属性
                    TypeName typeName = getTypeName(type);
                    field = FieldSpec
                            .builder(typeName, fieldName)
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                    // 2. getter
                    addGetterMethod(entityBuilder, typeName, JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_GETTER), fieldName, null);
                    // 3. setter
                    ParameterSpec setterParam = ParameterSpec.builder(typeName, fieldName).build();
                    addSetterMethod(entityBuilder, setterParam, JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_SETTER), fieldName);
                }
                entityBuilder.addField(field);
            }

            TypeSpec entity = entityBuilder.build();
            JavaFile javaFile = JavaFile.builder(ANDROID_ENTITY_PACKAGE_NAME, entity).build();
            return javaFile.toString();
        } catch (Exception e) {
            log.error("生成安卓实体代码失败", e);
            return null;
        }
    }

    /**
     * 生成安卓视频实体代码
     *
     * @return 代码
     * @author szh
     * @since 2020/10/23 19:32
     */
    static String generateAndroidVideoAttr() {
        log.info("生成安卓视频实体代码");
        AnnotationSpec classAnno = AnnotationSpec
                .builder(org.greenrobot.greendao.annotation.Entity.class)
                .addMember("nameInDb", "$S", VideoAttr.class.getSimpleName())
                .build();
        TypeSpec.Builder videoBuilder = TypeSpec.classBuilder(VideoAttr.class.getSimpleName())
                .addAnnotation(classAnno)
                .addModifiers(Modifier.PUBLIC);

        // sri
        FieldSpec sriField = FieldSpec
                .builder(String.class, COMMON_PROPERTY_SRI)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(org.greenrobot.greendao.annotation.Id.class)
                .build();
        videoBuilder.addField(sriField);
        addGetterMethod(videoBuilder, ClassName.get(String.class), COMMON_METHOD_GET_SRI, COMMON_PROPERTY_SRI, null);
        ParameterSpec sriParam = ParameterSpec.builder(String.class, COMMON_PROPERTY_SRI).build();
        addSetterMethod(videoBuilder, sriParam, COMMON_METHOD_SET_SRI, COMMON_PROPERTY_SRI);

        // parentSri
        FieldSpec parentField = FieldSpec
                .builder(String.class, ANDROID_PROPERTY_PARENT_SRI)
                .addModifiers(Modifier.PRIVATE)
                .build();
        videoBuilder.addField(parentField);
        addGetterMethod(videoBuilder, ClassName.get(String.class), ANDROID_METHOD_GET_PARENT_SRI, ANDROID_PROPERTY_PARENT_SRI, null);
        ParameterSpec parentSriParam = ParameterSpec.builder(String.class, ANDROID_PROPERTY_PARENT_SRI).build();
        addSetterMethod(videoBuilder, parentSriParam, ANDROID_METHOD_SET_PARENT_SRI, ANDROID_PROPERTY_PARENT_SRI);

        // Camera属性
        AnnotationSpec transientAnno = AnnotationSpec
                .builder(org.greenrobot.greendao.annotation.Transient.class)
                .build();
        ClassName cameraClass = ClassName.get(ANDROID_CAMERA_CLASS_NAME, ANDROID_CLASS_CAMERA);
        FieldSpec cameraField = FieldSpec
                .builder(cameraClass, ANDROID_PROPERTY_CAMERA)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(transientAnno)
                .build();
        videoBuilder.addField(cameraField);
        // getter
        addGetterMethod(videoBuilder, cameraClass, ANDROID_METHOD_GET_CAMERA, ANDROID_PROPERTY_CAMERA, null);
        // setter
        ParameterSpec cameraParam = ParameterSpec.builder(cameraClass, ANDROID_PROPERTY_CAMERA).build();
        addSetterMethod(videoBuilder, cameraParam, ANDROID_METHOD_SET_CAMERA, ANDROID_PROPERTY_CAMERA);

        // url属性
        FieldSpec urlField = FieldSpec
                .builder(String.class, ANDROID_PROPERTY_URL)
                .addModifiers(Modifier.PRIVATE)
                .build();
        videoBuilder.addField(urlField);
        // getter
        addGetterMethod(videoBuilder, ClassName.get(String.class), ANDROID_METHOD_GET_URL, ANDROID_PROPERTY_URL, null);
        // setter
        ParameterSpec urlParam = ParameterSpec.builder(String.class, ANDROID_PROPERTY_URL).build();
        addSetterMethod(videoBuilder, urlParam, ANDROID_METHOD_SET_URL, ANDROID_PROPERTY_URL);

        TypeSpec entity = videoBuilder.build();
        JavaFile javaFile = JavaFile.builder(ANDROID_ENTITY_PACKAGE_NAME, entity).build();
        return javaFile.toString();
    }

    /**
     * 生成安卓视频实体代码
     *
     * @return 代码
     * @author szh
     * @since 2020/10/23 19:32
     */
    static String generateAndroidVideoAttrVersionA() {
        log.info("生成安卓视频实体代码");
        TypeSpec.Builder videoBuilder = TypeSpec.classBuilder(VideoAttr.class.getSimpleName())
                .addModifiers(Modifier.PUBLIC);

        // sri
        FieldSpec sriField = FieldSpec
                .builder(String.class, COMMON_PROPERTY_SRI)
                .addModifiers(Modifier.PRIVATE)
                .build();
        videoBuilder.addField(sriField);
        addGetterMethod(videoBuilder, ClassName.get(String.class), COMMON_METHOD_GET_SRI, COMMON_PROPERTY_SRI, null);
        ParameterSpec sriParam = ParameterSpec.builder(String.class, COMMON_PROPERTY_SRI).build();
        addSetterMethod(videoBuilder, sriParam, COMMON_METHOD_SET_SRI, COMMON_PROPERTY_SRI);

        // parentSri
        FieldSpec parentField = FieldSpec
                .builder(String.class, ANDROID_PROPERTY_PARENT_SRI)
                .addModifiers(Modifier.PRIVATE)
                .build();
        videoBuilder.addField(parentField);
        addGetterMethod(videoBuilder, ClassName.get(String.class), ANDROID_METHOD_GET_PARENT_SRI, ANDROID_PROPERTY_PARENT_SRI, null);
        ParameterSpec parentSriParam = ParameterSpec.builder(String.class, ANDROID_PROPERTY_PARENT_SRI).build();
        addSetterMethod(videoBuilder, parentSriParam, ANDROID_METHOD_SET_PARENT_SRI, ANDROID_PROPERTY_PARENT_SRI);

        // Camera属性
        ClassName cameraClass = ClassName.get(ANDROID_CAMERA_CLASS_NAME, ANDROID_CLASS_CAMERA);
        FieldSpec cameraField = FieldSpec
                .builder(cameraClass, ANDROID_PROPERTY_CAMERA)
                .addModifiers(Modifier.PRIVATE)
                .build();
        videoBuilder.addField(cameraField);
        // getter
        addGetterMethod(videoBuilder, cameraClass, ANDROID_METHOD_GET_CAMERA, ANDROID_PROPERTY_CAMERA, null);
        // setter
        ParameterSpec cameraParam = ParameterSpec.builder(cameraClass, ANDROID_PROPERTY_CAMERA).build();
        addSetterMethod(videoBuilder, cameraParam, ANDROID_METHOD_SET_CAMERA, ANDROID_PROPERTY_CAMERA);

        // url属性
        FieldSpec urlField = FieldSpec
                .builder(String.class, ANDROID_PROPERTY_URL)
                .addModifiers(Modifier.PRIVATE)
                .build();
        videoBuilder.addField(urlField);
        // getter
        addGetterMethod(videoBuilder, ClassName.get(String.class), ANDROID_METHOD_GET_URL, ANDROID_PROPERTY_URL, null);
        // setter
        ParameterSpec urlParam = ParameterSpec.builder(String.class, ANDROID_PROPERTY_URL).build();
        addSetterMethod(videoBuilder, urlParam, ANDROID_METHOD_SET_URL, ANDROID_PROPERTY_URL);

        TypeSpec entity = videoBuilder.build();
        JavaFile javaFile = JavaFile.builder(ANDROID_ENTITY_PACKAGE_NAME, entity).build();
        return javaFile.toString();
    }

    /**
     * 为类添加setter方法
     *
     * @param classBuilder 类构造器
     * @param param 方法参数
     * @param methodName 方法名
     * @param paramName 参数名
     * @author szh
     * @since 2020/10/23 21:07
     */
    private static void addSetterMethod(TypeSpec.Builder classBuilder, ParameterSpec param, String methodName, String paramName) {
        MethodSpec setUrlMethod = MethodSpec
                .methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(param)
                .addStatement("this.$L = $L", paramName, paramName)
                .build();
        classBuilder.addMethod(setUrlMethod);
    }

    /**
     * 为类添加getter方法
     *
     * @param classBuilder 类构造器
     * @param methodName 方法名
     * @param paramName 参数名
     * @author szh
     * @since 2020/10/23 21:09
     */
    private static void addGetterMethod(TypeSpec.Builder classBuilder, TypeName type, String methodName, String paramName, String comment) {
        MethodSpec.Builder getterMethodBuilder = MethodSpec
                .methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return $L", paramName);
//        if (StringUtils.isNotBlank(comment)) {
//            getterMethodBuilder.addJavadoc("获取" + comment + "\n")
//                    .addJavadoc("@return $L\n", comment);
//        }
        classBuilder.addMethod(getterMethodBuilder.build());
    }

    /**
     * 为类添加setter方法
     *
     * @param entityBuilder 类构造器
     * @param setterParam 参数
     * @param fieldName 属性名
     * @param isList 是否是list属性
     * @param className 类名
     * @author szh
     * @since 2021/5/5 15:10
     */
    private static void addSetterMethod(TypeSpec.Builder entityBuilder, ParameterSpec setterParam, String fieldName, boolean isList, String className, String comment) {
        String setterName = JavaFileUtils.getGetterSetterName(fieldName, JavaFileUtils.METHOD_SETTER);
        MethodSpec.Builder setterMethodBuilder = MethodSpec
                .methodBuilder(setterName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(setterParam);

//        if (StringUtils.isNotBlank(comment)) {
//            setterMethodBuilder.addJavadoc("设置" + comment + "\n")
//                    .addJavadoc("@param $L $L\n", fieldName, comment);
//        }

        if (isList) {
            setterMethodBuilder.addStatement("this.$L = $L", fieldName, fieldName);
        } else if (
                COMMON_PROPERTY_DEVICE_ID.equals(fieldName) ||
                        COMMON_PROPERTY_TOPIC.equals(fieldName) ||
                        COMMON_PROPERTY_IP.equals(fieldName) ||
                        COMMON_PROPERTY_BIND_CODE.equals(fieldName)) {
            setterMethodBuilder
                    .addStatement("this.$L = $L", fieldName, fieldName)
                    .addStatement("super." + setterName + "($L)", fieldName);
        } else {
            setterMethodBuilder
                    .addStatement("$T field = new $T($S, $S, this.$L)",
                            EntityField.class, EntityField.class, className, fieldName, fieldName)
                    .addStatement("this.$L = $L", fieldName, fieldName)
                    .addStatement("field.setFieldValue($L)", fieldName)
                    .addStatement("notifyObservers(databaseFieldMap.get($S), field)", fieldName);
        }
        entityBuilder.addMethod(setterMethodBuilder.build());
    }

    /**
     * 由名称获取typename
     *
     * @param type 类型名称
     * @author szh
     * @since 2019/12/26 15:10
     */
    private static TypeName getTypeName(String type) {
        switch (type) {
            case "byte":
            case "Byte":
                return TypeName.BYTE.box();
            case "boolean":
            case "Boolean":
                return TypeName.BOOLEAN.box();
            case "short":
            case "Short":
                return TypeName.SHORT.box();
            case "int":
            case "Integer":
                return TypeName.INT.box();
            case "long":
            case "Long":
                return TypeName.LONG.box();
            case "double":
            case "Double":
                return TypeName.DOUBLE.box();
            case "float":
            case "Float":
                return TypeName.FLOAT.box();
            case "char":
            case "Character":
                return TypeName.CHAR.box();
            case "date":
            case "Date":
                return ClassName.get(Date.class);
            default:
                return ClassName.get("java.lang", type);
        }
    }
}
