package com.shadow.web.model.result;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * 简单数据
 *
 * @author szh
 * @since 2021/4/4 19:59
 **/
public class SimpleData extends HashMap<String, Object> {

    /**
     * 创建数据
     *
     * @author szh
     * @since 2021/4/4 20:01
     */
    public static SimpleData create() {
        return new SimpleData();
    }

    /**
     * 增加数据
     *
     * @param key 键
     * @param value 值
     * @author szh
     * @since 2021/4/4 20:02
     */
    public SimpleData add(String key, Object value) {
        put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
