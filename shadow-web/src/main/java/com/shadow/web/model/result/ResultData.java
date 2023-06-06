package com.shadow.web.model.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: wangzhendong
 * @Date: 2018/11/13 16:52
 * @Description:
 */
@Data
public class ResultData<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    public ResultData() {
    }


    public ResultData(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

}
