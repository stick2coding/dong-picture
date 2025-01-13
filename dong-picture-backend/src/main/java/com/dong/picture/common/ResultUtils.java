package com.dong.picture.common;

import com.dong.picture.exception.ErrorCode;

public class ResultUtils {

    /**
     * 返回成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败，返回错误信息(有自定义状态码)
     * @param errorCode
     * @return
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败，返回错误码和错误信息（无自定义状态码）
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse<?> error(int code, String message){
        return new BaseResponse<>(code,null,message);
    }


    /**
     * 失败，返回错误码和错误信息（有自定义状态码，但重写返回信息）
     * @param errorCode
     * @param message
     * @return
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message){
        return new BaseResponse<>(errorCode.getCode(),null,message);
    }



}
