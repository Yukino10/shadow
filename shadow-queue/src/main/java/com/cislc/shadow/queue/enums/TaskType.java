package com.cislc.shadow.queue.enums;

/**
 * 任务类型
 */
public enum TaskType {

    BIG_HIGH_TASK(TaskDimension.BIG, TaskPriority.HIGH),
    BIG_NORMAL_TASK(TaskDimension.BIG, TaskPriority.NORMAL),
    BIG_LOW_TASK(TaskDimension.BIG, TaskPriority.LOW),

    NORMAL_HIGH_TASK(TaskDimension.NORMAL, TaskPriority.HIGH),
    NORMAL_NORMAL_TASK(TaskDimension.NORMAL, TaskPriority.NORMAL),
    NORMAL_LOW_TASK(TaskDimension.NORMAL, TaskPriority.LOW),

    SMALL_HIGH_TASK(TaskDimension.SMALL, TaskPriority.HIGH),
    SMALL_NORMAL_TASK(TaskDimension.SMALL, TaskPriority.NORMAL),
    SMALL_LOW_TASK(TaskDimension.SMALL, TaskPriority.LOW);

    private TaskDimension dimension;
    private TaskPriority priority;
    public String name;

    /**
     * @Description 构建topic
     * @param dimension 任务维度
     * @param priority 任务优先级
     * @author szh
     * @Date 2019/10/9 14:25
     */
    TaskType(TaskDimension dimension, TaskPriority priority) {
        this.dimension = dimension;
        this.priority = priority;
        this.name = String.format("%s-%s-%s", dimension.getDimension(), priority.getPriority(), "task");
    }

    /**
     * @Description 获取任务类型
     * @param name topic
     * @return 任务类型
     * @author szh
     * @Date 2019/10/9 14:28
     */
    public static TaskType getTaskType(String name) {
        for (TaskType taskType : TaskType.values()) {
            if (taskType.getName().equals(name)) {
                return taskType;
            }
        }
        return null;
    }

    /**
     * @Description 获取任务类型
     * @param dimension 任务维度
     * @param priority 任务优先级
     * @return 任务类型
     * @author szh
     * @Date 2019/10/14 15:05
     */
    public static TaskType getTaskType(TaskDimension dimension, TaskPriority priority) {
        switch (dimension) {
            case BIG:
                switch (priority) {
                    case HIGH:
                        return BIG_HIGH_TASK;
                    case NORMAL:
                        return BIG_NORMAL_TASK;
                    case LOW:
                        return BIG_LOW_TASK;
                }
            case NORMAL:
                switch (priority) {
                    case HIGH:
                        return NORMAL_HIGH_TASK;
                    case NORMAL:
                        return NORMAL_NORMAL_TASK;
                    case LOW:
                        return NORMAL_LOW_TASK;
                }
            case SMALL:
                switch (priority) {
                    case HIGH:
                        return SMALL_HIGH_TASK;
                    case NORMAL:
                        return SMALL_NORMAL_TASK;
                    case LOW:
                        return SMALL_LOW_TASK;
                }
                break;
        }
        return NORMAL_NORMAL_TASK;
    }

    public TaskDimension getDimension() {
        return dimension;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

}
