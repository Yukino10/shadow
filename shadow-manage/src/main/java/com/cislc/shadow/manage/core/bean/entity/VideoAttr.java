package com.cislc.shadow.manage.core.bean.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.cislc.shadow.manage.core.bean.field.DatabaseField;
import com.cislc.shadow.manage.core.bean.field.EntityField;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;

/**
 * 视频属性
 *
 * @author szh
 * @since 2020/10/6 15:30
 **/
@Entity
public class VideoAttr extends ShadowEntity {

    @Transient
    private static final Map<String, DatabaseField> databaseFieldMap = new HashMap<>();

    static {
        databaseFieldMap.put("url", new DatabaseField("video_attr", "url"));
    }

    public VideoAttr() {
        super();
    }

    public VideoAttr(String deviceId) {
        super(deviceId);
        setDeviceId(deviceId);
    }

    @JSONField(serialize = false)
    @Transient
    @Override
    public boolean isDevice() {
        return false;
    }

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        EntityField field = new EntityField("VideoAttr", "url", this.url);
        this.url = url;
        field.setFieldValue(url);
        notifyObservers(databaseFieldMap.get("url"), field);
    }
}
