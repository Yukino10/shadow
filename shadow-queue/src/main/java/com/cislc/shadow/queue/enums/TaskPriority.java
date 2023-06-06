package com.cislc.shadow.queue.enums;

/**
 * 任务优先级
 */
public enum TaskPriority {

    HIGH("high", 6),
    NORMAL("normal", 3),
    LOW("low", 1);

    /** 优先级名称 */
    private String priority;
    /** 任务比例 */
    private int taskProportion;

    TaskPriority(String priority, int taskProportion) {
        this.priority = priority;
        this.taskProportion = taskProportion;
    }

    public double getTaskProportion() {
        int sum = HIGH.taskProportion + NORMAL.taskProportion + LOW.taskProportion;
        return (double) taskProportion / sum;
    }

    public String getPriority() {
        return priority;
    }

}
