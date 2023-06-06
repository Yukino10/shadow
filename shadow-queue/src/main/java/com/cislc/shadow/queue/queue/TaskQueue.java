package com.cislc.shadow.queue.queue;

import com.cislc.shadow.manage.core.bean.comm.ShadowOpsBean;
import com.cislc.shadow.queue.enums.TaskPriority;
import com.cislc.shadow.queue.enums.TaskType;
import com.cislc.shadow.queue.utils.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TaskQueue
 * @Description 内存任务队列
 * @Date 2019/10/7 10:35
 * @author szh
 **/
@Slf4j
class TaskQueue {

    /** 高优先级队列 */
    private PriorityQueue highTaskQueue;
    /** 中优先级队列 */
    private PriorityQueue normalTaskQueue;
    /** 低优先级队列 */
    private PriorityQueue lowTaskQueue;
    /** 任务总数 */
    private int taskNum = 0;

    TaskQueue() {
        highTaskQueue = new PriorityQueue(TaskPriority.HIGH);
        normalTaskQueue = new PriorityQueue(TaskPriority.NORMAL);
        lowTaskQueue = new PriorityQueue(TaskPriority.LOW);
    }

    /**
     * @Description 遍历获取任务
     * @return 任务列表
     * @author szh
     * @Date 2019/10/8 19:54
     */
    synchronized List<ShadowOpsBean> takeTask() {
        // 空闲线程数量
        int threadNum = ThreadPoolUtils.getFreeThreadNum();

        // 存在空线程与未执行的任务，执行分配
        if (threadNum > 0 && taskNum > 0) {
            List<ShadowOpsBean> taskList = new ArrayList<>();
            /* 1. 可被分配到优先级队列的线程数 */
            int highThreadNum = (int) Math.ceil(TaskPriority.HIGH.getTaskProportion() * threadNum);
            int normalThreadNum = (int) (TaskPriority.NORMAL.getTaskProportion() * threadNum);
            int lowThreadNum;

            /* 2. 判断任务数量与被分配的线程数量，执行线程向下流转 */
            int highTaskNum = highTaskQueue.getTaskNum();
            int normalTaskNum = normalTaskQueue.getTaskNum();

            if (highTaskNum < highThreadNum) {
                normalThreadNum += (highThreadNum - highTaskNum);
                highThreadNum = highTaskNum;
            }
            if (normalTaskNum < normalThreadNum) {
                normalThreadNum = normalTaskNum;
            }

            /* 4. 低优先级任务线程数 */
            // 取任务数与剩余线程数的最小值
            lowThreadNum = Math.min(threadNum - highThreadNum - normalThreadNum, lowTaskQueue.getTaskNum());

            /* 5. 在各队列中获取任务 */
            if (0 < highThreadNum) {
                List<ShadowOpsBean> highTask = highTaskQueue.takeTask(highThreadNum);
                taskList.addAll(highTask);
            }
            if (0 < normalThreadNum) {
                List<ShadowOpsBean> normalTask = normalTaskQueue.takeTask(normalThreadNum);
                taskList.addAll(normalTask);
            }
            if (0 < lowThreadNum) {
                List<ShadowOpsBean> lowTask = lowTaskQueue.takeTask(lowThreadNum);
                taskList.addAll(lowTask);
            }

//            log.error("task num: {}, thread num: {}, high: {}, normal: {}, low: {}",
//                    taskNum, threadNum, highThreadNum, normalThreadNum, lowThreadNum);

            taskNum -= taskList.size();
            return taskList;
        }

        return null;
    }

    /**
     * @Description 增加任务
     * @param bean 任务
     * @param taskType 任务类型
     * @author szh
     * @Date 2019/10/9 14:37
     */
    void putTask(ShadowOpsBean bean, TaskType taskType) throws InterruptedException {
        switch (taskType.getPriority()) {
            case HIGH:
                highTaskQueue.putTask(bean, taskType.getDimension());
                taskNum++;
                break;
            case NORMAL:
                normalTaskQueue.putTask(bean, taskType.getDimension());
                taskNum++;
                break;
            case LOW:
                lowTaskQueue.putTask(bean, taskType.getDimension());
                taskNum++;
                break;
            default:
                break;
        }
    }

}
