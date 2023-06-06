package com.cislc.shadow.manage.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author szh
 * @ClassName JsonUtils
 * @Description json工具
 * @Date 2020/1/30 20:46
 **/
public class JsonUtils {

    /**
     * @Description 字符串转实体
     * @param text 字符串
     * @param clazz 实体类
     * @return 实体
     * @author szh
     * @Date 2020/1/30 22:21
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        int disableDecimalFeature = JSON.DEFAULT_PARSER_FEATURE & ~Feature.UseBigDecimal.getMask();
        return JSON.parseObject(text, clazz, disableDecimalFeature);
    }

    /**
     * @Description 实体转字符串
     * @param o 实体
     * @return 字符串
     * @author szh
     * @Date 2020/1/30 22:22
     */
    public static String toJSONString(Object o) {
        return JSON.toJSONString(o, SerializerFeature.DisableCircularReferenceDetect);
    }

}
