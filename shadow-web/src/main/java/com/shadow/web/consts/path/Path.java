package com.shadow.web.consts.path;

/**
 * 基础路径
 *
 * @author szh
 * @since 2021/4/3 16:59
 **/
public class Path {

    // ============================ 项目路径 ==============================
    private final String PROJECT_PATH;
    // 后台模块路径
    private static final String MODULE_MANAGE_PATH = "shadow-manage/";
    private static final String MODULE_QUEUE_PATH = "shadow-queue/";
    // 后台代码路径
    private static final String DEVICE_CLASS_FILE_PATH = MODULE_MANAGE_PATH + "target/classes/com/cislc/shadow/manage/device";
    private static final String DEVICE_JAVA_FILE_PATH = MODULE_MANAGE_PATH + "src/main/java/com/cislc/shadow/manage/device/";
    // 安卓项目路径
    private static final String ANDROID_PATH = "ShadowUtils/";
    // 安卓java文件路径
    private static final String ANDROID_JAVA_FILE_PATH = ANDROID_PATH + "app/src/main/java/com/cislc/shadowutils/greenDao/entity/";
    private static final String ANDROID_DAO_FILE_PATH = ANDROID_PATH + "app/src/main/java/com/cislc/shadowutils/greenDao/dao/";
    // 打包脚本
    private static final String PACKAGE_SCRIPT_PATH = "package.sh";
    // 开发包路径
    private static final String PACKAGE_PATH = "package/package.zip";

    public Path(String projectPath) {
        this.PROJECT_PATH = projectPath;
    }


    /**
     * web设备代码文件路径
     *
     * @author szh
     * @since 2021/4/3 20:05
     */
    public String getDeviceJavaFilePath() {
        return PROJECT_PATH + DEVICE_JAVA_FILE_PATH;
    }
    /**
     * java实体类文件路径
     *
     * @author szh
     * @since 2021/4/3 17:17
     */
    public String getEntityFilePath() {
        return PROJECT_PATH + DEVICE_JAVA_FILE_PATH + "entity/";
    }

    /**
     * 数据库管理文件路径
     *
     * @author szh
     * @since 2021/4/3 17:17
     */
    public String getRepositoryFilePath() {
        return PROJECT_PATH + DEVICE_JAVA_FILE_PATH + "repository/";
    }

    /**
     * 初始化类文件路径
     *
     * @author szh
     * @since 2021/4/3 17:17
     */
    public String getInitFilePath() {
        return PROJECT_PATH + DEVICE_JAVA_FILE_PATH + "init/";
    }

    /**
     * 实体名常量接口路径
     *
     * @author szh
     * @since 2021/4/3 17:18
     */
    public String getEntityNameFilePath() {
        return PROJECT_PATH + DEVICE_JAVA_FILE_PATH + "names/";
    }

    /**
     * 安卓实体代码路径
     *
     * @author szh
     * @since 2021/4/3 17:20
     */
    public String getAndroidJavaFilePath() {
        return PROJECT_PATH + ANDROID_JAVA_FILE_PATH;
    }

    /**
     * 安卓dao代码路径
     *
     * @author szh
     * @since 2021/4/3 20:07
     */
    public String getAndroidDaoFilePath() {
        return PROJECT_PATH + ANDROID_DAO_FILE_PATH;
    }

    /**
     * 后台class文件路径
     *
     * @author szh
     * @since 2021/4/3 20:10
     */
    public String getDeviceClassFilePath() {
        return PROJECT_PATH + DEVICE_CLASS_FILE_PATH;
    }

    /**
     * 打包脚本路径
     *
     * @author szh
     * @since 2021/4/3 20:20
     */
    public String getPackageScriptPath() {
        return PROJECT_PATH + PACKAGE_SCRIPT_PATH;
    }

    /**
     * 开发包路径
     *
     * @author szh
     * @since 2021/4/3 20:23
     */
    public String getPackagePath() {
        return PROJECT_PATH + PACKAGE_PATH;
    }

}
