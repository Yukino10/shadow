package com.cislc.shadow.queue.queue;

import com.cislc.shadow.manage.common.utils.JsonUtils;
import com.cislc.shadow.manage.core.bean.comm.ShadowConst;
import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.manage.device.names.EntityNames;
import com.cislc.shadow.queue.enums.TaskDimension;
import com.cislc.shadow.queue.enums.TaskPriority;
import com.cislc.shadow.queue.enums.TaskType;
import com.cislc.shadow.queue.queue.message.AbsDeleteMsgHandler;
import com.cislc.shadow.queue.queue.message.AbsUpdateMsgHandler;
import com.cislc.shadow.queue.queue.message.MessageSubject;
import com.cislc.shadow.queue.queue.message.ProtocolAdapter;
import com.cislc.shadow.queue.service.TaskExecuteService;
import com.cislc.shadow.utils.coap.CoAPClient;
import com.cislc.shadow.utils.encription.AESUtils;
import com.cislc.shadow.utils.encription.RSAUtil;
import com.cislc.shadow.utils.mqtts.MqttFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * QueueExecutor
 * 队列执行者
 * @since 2019/10/7 15:01
 * @author szh
 **/
@Order(value = 1)
@Component
@Slf4j
public class QueueExecutor extends MessageSubject implements CommandLineRunner {

    private TaskQueue taskQueue;

    @Autowired
    private TaskExecuteService taskExecuteService;
    @Autowired(required = false)
    private ProtocolAdapter protocolAdapter;
    @Autowired(required = false)
    private List<AbsUpdateMsgHandler<?>> updateMsgHandlers;
    @Autowired(required = false)
    private List<AbsDeleteMsgHandler<?>> deleteMsgHandlers;

    @Override
    public void run(String... args) {
        taskQueue = new TaskQueue();

        // 启动mqtt和coap服务
        CoAPClient.startServer((String inMessage) -> {
            try {
                // 1. 解密
                switch (EntityNames.encryption) {
                    case AES:
                        inMessage = AESUtils.aesDecryptString(inMessage, "");
                        break;
                    case RSA:
                        inMessage = RSAUtil.decrypt(inMessage);
                        break;
                }

                log.info("coap receive message: {}", inMessage);

                // 2. 消息转换
                if (protocolAdapter != null) {
                    inMessage = protocolAdapter.transform(inMessage);
                }

                // 3. 加入队列
                ShadowOpsBean bean = JsonUtils.parseObject(inMessage, ShadowOpsBean.class);
                addTask(bean, TaskType.BIG_HIGH_TASK);

                // 这里可以返回一段message, coap将以响应的形式返回。
                return null;
            } catch (Exception e) {
                log.error("resolve msg error", e);
                return null;
            }
        });

        // 设置收到mqtt消息时的回调
        MqttFactory.setCallback((String inMessage) -> {
            try {
                // 1. 解密
                switch (EntityNames.encryption) {
                    case AES:
                        inMessage = AESUtils.aesDecryptString(inMessage, "");
                        break;
                    case RSA:
                        inMessage = RSAUtil.decrypt(inMessage);
                        break;
                }

                log.info("mqtt receive message: {}", inMessage);

                // 2. 消息转换
                if (protocolAdapter != null) {
                    inMessage = protocolAdapter.transform(inMessage);
                }

                // 3. 加入队列
                ShadowOpsBean bean = JsonUtils.parseObject(inMessage, ShadowOpsBean.class);
                addTask(bean, TaskType.BIG_HIGH_TASK);
            } catch (Exception e) {
                log.error("resolve msg error", e);
            }
        });
    }
    /**
     * 定时分配任务 50ms
     * @author szh
     * @since 2019/10/8 20:06
     */
    @Scheduled(fixedDelay = 50)
    public void queryQueues() {
        if (null != taskQueue) {
            List<ShadowOpsBean> tasks = taskQueue.takeTask();
            if (null != tasks) {
                tasks.forEach(bean -> {
                    taskExecuteService.executeTask(bean);
                    notifyObservers(bean);
                    customerHandleMsg(bean);
                });
            }
        }
    }

    /**
     * 用户处理信息
     *
     * @param opsBean 通信内容
     * @author szh
     * @since 2021/5/11 21:02
     */
    private void customerHandleMsg(ShadowOpsBean opsBean) {
        switch (opsBean.getMethod()) {
            case ShadowConst.OPERATION_METHOD_UPDATE:
                if (!CollectionUtils.isEmpty(updateMsgHandlers)) {
                    for (AbsUpdateMsgHandler<?> handler : updateMsgHandlers) {
                        handler.handleMsg(opsBean);
                    }
                }
                break;

            case ShadowConst.OPERATION_METHOD_DELETE:
                if (!CollectionUtils.isEmpty(deleteMsgHandlers)) {
                    for (AbsDeleteMsgHandler<?> handler : deleteMsgHandlers) {
                        handler.handleMsg(opsBean);
                    }
                }
                break;
        }
    }

    /**
     * 添加任务
     * @param bean 任务
     * @param taskType 任务类型
     * @author szh
     * @since 2019/10/9 15:36
     */
    public void addTask(ShadowOpsBean bean, TaskType taskType) {
        try {
            taskQueue.putTask(bean, taskType);
        } catch (InterruptedException e) {
            log.error("add task interrupted: '{}'", e.getMessage());
        }
    }

    /**
     * 添加任务
     * @param bean 任务
     * @param priority 优先级
     * @author szh
     * @since 2019/10/14 15:29
     */
    public void addTask(ShadowOpsBean bean, TaskPriority priority) {
        try {
            // TODO 判断维度
            taskQueue.putTask(bean, TaskType.getTaskType(TaskDimension.BIG, priority));
        } catch (InterruptedException e) {
            log.error("add task interrupted: '{}'", e.getMessage());
        }
    }

}
