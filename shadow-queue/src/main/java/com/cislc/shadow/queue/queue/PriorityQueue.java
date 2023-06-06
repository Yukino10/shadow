package com.cislc.shadow.queue.queue;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.queue.enums.TaskDimension;
import com.cislc.shadow.queue.enums.TaskPriority;
import com.cislc.shadow.queue.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName PriorityQueue
 * @Description 任务队列
 * @Date 2019/10/7 11:05
 * @author szh
 **/
@Slf4j
class PriorityQueue {

    /** 大型任务队列 */
    private LinkedBlockingQueue<ShadowOpsBean> bigTaskQueue;
    /** 一般任务队列 */
    private LinkedBlockingQueue<ShadowOpsBean> normalTaskQueue;
    /** 小型任务队列 */
    private LinkedBlockingQueue<ShadowOpsBean> smallTaskQueue;
    /** 此优先级任务总数 */
    private int taskNum = 0;
    /** 此优先级 */
    private TaskPriority priority;

    PriorityQueue(TaskPriority priority) {
        this.priority = priority;
        bigTaskQueue = new LinkedBlockingQueue<>();
        normalTaskQueue = new LinkedBlockingQueue<>();
        smallTaskQueue = new LinkedBlockingQueue<>();
    }

    /**
     * @Description 获取任务
     * @param num 任务数量
     * @return 任务列表
     * @author szh
     * @Date 2019/10/8 15:59
     */
    List<ShadowOpsBean> takeTask(int num) {
        List<ShadowOpsBean> list = new ArrayList<>();
        // 遍历所有队列取任务，直到拿到指定数量的任务或者取走所有任务
        for (int i = 0; list.size() < num && taskNum > 0 && list.size() <= taskNum; i++) {
            // 轮询同等级内的所有队列，取枚举类型的下标
            int queueIndex = i % TaskDimension.values().length;

            LinkedBlockingQueue<ShadowOpsBean> selectedQueue;
            switch (TaskDimension.values()[queueIndex]) {
                case BIG:
                    selectedQueue = bigTaskQueue;
                    break;
                case NORMAL:
                    selectedQueue = normalTaskQueue;
                    break;
                case SMALL:
                    selectedQueue = smallTaskQueue;
                    break;
                default:
                    selectedQueue = null;
            }

            // 仿照蓄水池算法，遍历队列时每次取到任务的概率为 num/taskNum
            if (null != selectedQueue && selectedQueue.size() > 0) {
                if (RandomUtils.getProbability(num, taskNum)) {
                    try {
                        list.add(selectedQueue.take());
                    } catch (InterruptedException e) {
                        log.error("{} task queue: take task interrupted", priority.getPriority());
                    }
                }
            }
        }

        taskNum -= list.size();
        return list;
    }

    /**
     * @Description 增加任务
     * @param bean 任务
     * @param dimension 任务维度
     * @author szh
     * @Date 2019/10/8 16:30
     */
    void putTask(ShadowOpsBean bean, TaskDimension dimension) throws InterruptedException {
        switch (dimension) {
            case BIG:
                bigTaskQueue.put(bean);
                taskNum++;
                break;
            case NORMAL:
                normalTaskQueue.put(bean);
                taskNum++;
                break;
            case SMALL:
                smallTaskQueue.put(bean);
                taskNum++;
                break;
            default:
                break;
        }
    }

    int getTaskNum() {
        return taskNum;
    }
}
