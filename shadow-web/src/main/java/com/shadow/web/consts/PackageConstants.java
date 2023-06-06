package com.shadow.web.consts;

/**
 * 包名
 *
 * @author szh
 * @since 2021/4/3 20:14
 **/
public interface PackageConstants {

    // 后台主包名
    String MAIN_PACKAGE_NAME = "com.cislc.shadow.manage.";

    // ============================== 包名 ==================================
    // 动态类的包名
    String ENTITY_PACKAGE_NAME = MAIN_PACKAGE_NAME + "device.entity";
    // 数据库映射包名
    String REPOSITORY_PACKAGE_NAME = MAIN_PACKAGE_NAME + "device.repository";
    // 初始化类包名
    String INIT_PACKAGE_NAME = MAIN_PACKAGE_NAME + "device.init";
    // 实体名常量接口包名
    String ENTITY_NAME_PACKAGE_NAME = MAIN_PACKAGE_NAME + "device.names";

    // ============================= Android =============================
    // android动态类包名
    String ANDROID_ENTITY_PACKAGE_NAME = "com.cislc.shadowutils.greenDao.entity";
    // android相机类名
    String ANDROID_CAMERA_CLASS_NAME = "android.hardware";
}
