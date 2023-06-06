package com.cislc.shadow.queue.service;

import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.sync.callback.*;
import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @ClassName TaskExecuteService
 * @Description 任务执行service
 * @Date 2019/10/5 22:46
 * @author szh
 **/
@Slf4j
@Service
public class TaskExecuteService {

    private final ShadowDeleteCallback deleteCallback;
    private final ShadowGetCallback getCallback;
    private final ShadowUpdateCallback updateCallback;
    private final ShadowBindCallback bindCallback;
    private final ShadowPingCallback pingCallback;

    public TaskExecuteService() {
        this.deleteCallback = new ShadowDeleteCallback();
        this.getCallback = new ShadowGetCallback();
        this.updateCallback = new ShadowUpdateCallback();
        this.bindCallback = new ShadowBindCallback();
        this.pingCallback = new ShadowPingCallback();
    }

    /**
     * @Description 执行影子更新任务
     * @param bean 影子通信数据
     * @author szh
     * @Date 2019/10/17 10:44
     */
    @Async("asyncServiceExecutor")
    public void executeTask(ShadowOpsBean bean) {
        // 根据要求选择回调方法
        switch (bean.getMethod()) {
            case ShadowConst.OPERATION_METHOD_DELETE:
                deleteCallback.run(bean.getDeviceId(), bean);
                break;
            case ShadowConst.OPERATION_METHOD_GET:
                getCallback.run(bean.getDeviceId(), bean);
                break;
            case ShadowConst.OPERATION_METHOD_UPDATE:
                updateCallback.run(bean.getDeviceId(), bean);
                break;
            case ShadowConst.OPERATION_METHOD_BIND:
                bindCallback.run(bean);
                break;
            case ShadowConst.OPERATION_METHOD_PING:
                pingCallback.run(bean.getDeviceId(), bean);
                break;
            default:
                break;
        }
    }

}
