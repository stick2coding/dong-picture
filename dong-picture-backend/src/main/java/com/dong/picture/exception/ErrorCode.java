package com.dong.picture.exception;

import lombok.Getter;

/**
 * 自定义错误码
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "成功"),
    PARAM_ERROR(40000, "参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND(40400, "找不到资源"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    OPERATION_ERROR(50000, "操作失败"),
    SERVER_ERROR(50001, "服务器错误"),
    SYSTEM_ERROR(50000, "系统错误");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态码信息
     */
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
