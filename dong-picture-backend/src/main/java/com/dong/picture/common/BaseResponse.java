package com.dong.picture.common;

import com.dong.picture.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 封装返回结果，通用返回结果
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 状态码、数据、信息封装
     * @param code
     * @param data
     * @param message
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 状态码、数据封装
     * @param code
     * @param data
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * 错误信息封装
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMsg());
    }


}
