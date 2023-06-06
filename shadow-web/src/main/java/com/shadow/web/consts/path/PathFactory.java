package com.shadow.web.consts.path;

import com.shadow.web.model.enums.VersionEnum;

/**
 * 路径工厂
 *
 * @author szh
 * @since 2021/4/3 16:52
 **/
public class PathFactory {

    private static final String PROJECT_PATH_VERSION_A = "/root/shadow/versionA/";
    private static final String PROJECT_PATH_VERSION_B = "/root/shadow/versionB/";
    private static final String PROJECT_PATH_VERSION_C = "/root/shadow/versionC/";

    private static final Path pathVersionA = new Path(PROJECT_PATH_VERSION_A);
    private static final Path pathVersionB = new Path(PROJECT_PATH_VERSION_B);
    private static final Path pathVersionC = new Path(PROJECT_PATH_VERSION_C);

    /**
     * 根据版本获取路径
     *
     * @param version 版本
     * @return 路径
     * @author szh
     * @since 2021/4/3 17:04
     */
    public static Path getPath(VersionEnum version) {
        switch (version) {
            case B:
                return pathVersionB;
            case C:
                return pathVersionC;
            case A:
            default:
                return pathVersionA;
        }
    }

}
