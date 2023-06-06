package com.shadow.web.model.result;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huhongyuan
 * Date: 2018-08-09
 * Time: 17:18
 * Description:
 */
public class ResultUtils {


    private ResultUtils() {
    }

    /**
     * 数据交互成功返回
     *
     * @param data 返回数据对象
     */
    public static <T> ResultData<T> success(T data) {
        return new ResultData<>(ResultType.SUCCESS.getCode(), ResultType.SUCCESS.getMsg(), data);
    }

    public static ResultData<String> success() {
        return ResultUtils.success("");
    }

    /**
     * 数据交互
     */
    public static ResultData<String> notFound() {
        return ResultUtils.notFound(ResultType.NOT_FOUND.getMsg());
    }

    /**
     * 数据交互
     */
    public static ResultData<String> notFound(String message) {
        return new ResultData<>(ResultType.NOT_FOUND.getCode(), message, "");
    }

    /**
     * 参数异常
     */
    public static ResultData<String> parameterError() {
        return ResultUtils.parameterError(ResultType.PARAMETER_ERROR.getMsg());
    }

    public static ResultData<String> parameterError(String meaasge) {
        return new ResultData<>(ResultType.PARAMETER_ERROR.getCode(), meaasge, "");
    }

    /**
     * 系统异常
     */
    public static ResultData<String> systemError() {
        return ResultUtils.systemError(ResultType.ERROR.getMsg());
    }

    public static ResultData<String> systemError(String message) {
        return new ResultData<>(ResultType.ERROR.getCode(), message, "");
    }

    /**
     * 业务异常
     */
    public static ResultData<String> businessException(String message) {
        return new ResultData<>(ResultType.BUSINESS_EXCEPTION.getCode(), message, "");
    }

    public static ResultData<String> businessException() {
        return businessException(ResultType.BUSINESS_EXCEPTION.getMsg());
    }

}