package com.shadow.web.utils;

import com.shadow.web.consts.PackageConstants;
import com.shadow.web.consts.path.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * java类操作工具类
 *
 * @since 2019/5/18 10:22
 * @author szh
 **/
@Slf4j
public class JavaFileUtils {

    // getter & setter 选项
    static final String METHOD_GETTER = "get";
    static final String METHOD_SETTER = "set";

    // 编译生成的class文件路径
    private static final String CLASS_FILE_PATH = "target/classes";

    /**
     * 单个生成java文件并编译
     *
     * @param sourceStr java代码
     * @param className 类名
     * @return 是否编译成功
     */
    public static boolean generateClass(String sourceStr, String className) {

        try {
            // 当前编译器
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // Java标准文件管理器
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            // Java文件对象
            JavaFileObject fileObject = new StringJavaObject(className, sourceStr);
            // 编译参数，类似于Javac <options> 中的options，编译文件的存放地方
            List<String> optionList = new ArrayList<>(Arrays.asList("-d", CLASS_FILE_PATH));
            // 要编译的单元
            List<JavaFileObject> fileObjectList = Arrays.asList(fileObject);
            // 设置编译环境
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, optionList, null, fileObjectList);
            return task.call();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 新建java文件
     *
     * @param classCode java代码
     * @param filePath 文件路径
     * @return java对象
     * @author szh
     * @since 2019/11/4 17:26
     */
    public static List<JavaFileObject> newJavaFiles(Map<String, String> classCode, String filePath) {
        log.info("新建java文件：classNames={}, filePath={}", classCode.keySet(), filePath);

        List<JavaFileObject> fileObjectList = new ArrayList<>();    // 要编译的单元
        for (Map.Entry<String, String> entry : classCode.entrySet()) {
            OutputStream os = null;
            try {
                // 创建java文件并写入代码
                File javaFile = new File(filePath + entry.getKey() + ".java");
                if (!javaFile.exists()) {
                    javaFile.getParentFile().mkdirs();
                    javaFile.createNewFile();
                }
                os = new FileOutputStream(javaFile);
                os.write(entry.getValue().getBytes(), 0, entry.getValue().length());
                os.flush();
                // Java文件对象
                fileObjectList.add(new StringJavaObject(entry.getKey(), entry.getValue()));
            } catch (IOException e) {
                log.error("新建java文件失败：className={}, code={}", entry.getKey(), entry.getValue(), e);
                return null;
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Exception ignore) {

                    }
                }
            }
        }
        return fileObjectList;
    }

    /**
     * 编译代码
     *
     * @param fileObjectList java代码
     * @return 是否成功
     * @author szh
     * @since 2019/11/4 17:28
     */
    public static boolean compileCode(List<JavaFileObject> fileObjectList) {
        // 当前编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // Java标准文件管理器
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        // 编译参数，类似于Javac <options> 中的options，编译文件的存放地方
        List<String> optionList = new ArrayList<>(Arrays.asList("-d", CLASS_FILE_PATH));
        // 设置编译环境
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, optionList, null, fileObjectList);
        return task.call();
    }

    /**
     * 删除生成的java文件
     *
     * @param fileName 文件名
     * @param filePath 文件路径
     * @author szh
     * @since 2019/5/20 9:38
     */
    private static void deleteJavaFiles(Set<String> fileName, String filePath) {
        for (String name : fileName) {
            File javaFile = new File(filePath + name + ".java");
            FileUtils.deleteQuietly(javaFile);
        }
    }

    /**
     * 删除Java文件
     *
     * 包括：
     *      服务端实体类及相关文件
     *      移动端实体类
     *      移动端dao
     * @author szh
     * @since 2019/10/22 16:01
     */
    public static void deleteJavaFiles(Path path) {
        log.info("删除Java文件：path={}, {}, {}", path.getDeviceJavaFilePath(),
                path.getAndroidJavaFilePath(), path.getAndroidDaoFilePath());
        try {
            FileUtils.deleteDirectory(new File(path.getDeviceJavaFilePath()));
            FileUtils.deleteDirectory(new File(path.getAndroidJavaFilePath()));
            FileUtils.deleteDirectory(new File(path.getAndroidDaoFilePath()));
        } catch (IllegalArgumentException ignored) {
        } catch (IOException e) {
            log.error("删除Java文件失败", e);
        }
    }

    /**
     * 删除编译的class文件
     *
     * @author szh
     * @since 2019/10/22 16:02
     */
    public static void deleteClassFiles(Path path) {
        try {
            FileUtils.deleteDirectory(new File(path.getDeviceClassFilePath()));
        } catch (IllegalArgumentException ignored) {
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获取getter或setter方法名称
     *
     * @param propertyName 属性名
     * @param method 方法类型：ClassGenerateUtils.METHOD_GETTER 或 ClassGenerateUtils.METHOD_SETTER
     * @return 方法名
     */
    public static String getGetterSetterName(String propertyName, String method) {
        String upperPropertyName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        return method + upperPropertyName;
    }

    /**
     * 获取生成的class的完整名称
     *
     * @param className 类名
     * @return 完整类名
     */
    public static String getClassFullName(String className) {
        return PackageConstants.ENTITY_PACKAGE_NAME + "." + className;
    }

    /**
     * 生成属性名
     *
     * @param typeName 类型名称
     * @return 属性名
     * @author szh
     * @since 2019/6/18 15:17
     */
    static String generateFieldName(String typeName) {
        if (Character.isLowerCase(typeName.charAt(0))) {
            return typeName;
        } else {
            return Character.toLowerCase(typeName.charAt(0)) + typeName.substring(1);
        }
    }

}
