package com.cislc.shadow.manage.common.utils;

import java.util.UUID;

/**
 * @ClassName UUIDUtils
 * @Description UUID工具类
 * @Date 2019/10/15 11:59
 * @author szh
 **/
public class UUIDUtils {

    /**
     * @Description 生成一个UUID
     * @return UUID
     * @author szh
     * @Date 2019/10/15 12:01
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
