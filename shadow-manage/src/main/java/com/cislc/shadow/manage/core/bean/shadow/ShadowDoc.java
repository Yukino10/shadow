package com.cislc.shadow.manage.core.bean.shadow;

import com.cislc.shadow.manage.core.bean.comm.ReState;
import com.cislc.shadow.manage.core.bean.entity.ShadowEntity;
import lombok.Data;

/**
 * @ClassName ShadowDoc
 * @Description 设备影子文档
 * @Date 2019/4/27 23:59
 * @author szh
 **/
@Data
public class ShadowDoc {

    /** 影子状态 **/
    private ShadowDocState state;
    /** 影子元数据 **/
    private ShadowDocMetadata metadata;
    /** 最后修改时间戳 **/
    private long timestamp;
    /** 影子版本 **/
    private Integer version;

    public ShadowDoc() {}

    public ShadowDoc(ShadowDocState state, ShadowDocMetadata metadata) {
        this.state = state;
        this.metadata = metadata;
        this.timestamp = System.currentTimeMillis();
        this.version = 0;
    }

    /**
     * @Description 版本递增
     * @author szh
     * @Date 2019/5/3 11:13
     */
    public void addUpVersion() {
        version += 1;
    }

    /**
     * @Description 获取用于返回的state
     * @param dataClass 影子对象类型
     * @return state
     * @author szh
     * @Date 2019/7/1 14:54
     */
    public ReState getAllStateTrans(Class<?> dataClass) {
        return new ReState((ShadowEntity) state.getReported(dataClass), state.getDesired());
    }

    /**
     * @Description 获取期望state
     * @return state
     * @author szh
     * @Date 2019/7/1 15:15
     */
    public ReState getDesiredStateTrans() {
        return new ReState(state.getDesired());
    }

    /**
     * @Description 清空期望数据
     * @author szh
     * @Date 2019/7/22 16:03
     */
    void clearDesired() {
        state.getDesired().clearDesired();
    }

}
