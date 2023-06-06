package com.cislc.shadow.queue.enums;

/**
 * 任务维度
 */
public enum TaskDimension {

    BIG("big"),
    NORMAL("normal"),
    SMALL("small");

    private String dimension;

    TaskDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDimension() {
        return dimension;
    }

}
