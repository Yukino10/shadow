package com.cislc.shadow.manage.core.bean.shadow;

import com.cislc.shadow.manage.common.utils.JsonUtils;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;

/**
 * @ClassName ShadowDocState
 * @Description 影子文档上报数据
 * @Date 2019/5/5 9:27
 * @author szh
 **/
public class ShadowDocState {

    /** 设备上报数据 **/
    private String reported = "{}";
    /** 服务器期望数据 **/
    private ShadowDesiredDoc desired;

    public ShadowDocState() {
        desired = new ShadowDesiredDoc();
    }

    public Object getReported(Class<?> dataClass) {
        return JsonUtils.parseObject(reported, dataClass);
    }

    public String getReportedStr() {
        return reported;
    }

    public void setReported(ShadowEntity reported) {
        this.reported = JsonUtils.toJSONString(reported);
    }

    public void setReportedStr(String reported) {
        this.reported = reported;
    }

    public ShadowDesiredDoc getDesired() {
        return desired;
    }

    public void setDesired(ShadowDesiredDoc desired) {
        this.desired = desired;
    }

}
