package com.shadow.web.model.result;

/**
 * @Auther: wangzhendong
 * @Date: 2018/11/13 16:53
 * @Description:
 */
public enum ResultType {
    /**
     * 访问成功返回
     */
    SUCCESS(0, "success"),

    /**
     * 数据不存在返回
     */
    NOT_FOUND(3, "notFound [数据不存在 或者 数据为空]"),

    /**
     * 异常返回
     */
    ERROR(-1, "error [服务器异常]"),

    /**
     * 参数有异常返回
     */
    PARAMETER_ERROR(1, "parameter error [参数异常:参数为空或者参数类型不符]"),

    /**
     * 业务异常返回
     */
    BUSINESS_EXCEPTION(2, "业务异常");

    private Integer code;

    private String msg;

    ResultType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

}
