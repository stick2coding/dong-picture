package com.dong.picture.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String UserName;

    /**
     * 账号
     */
    private String UserAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;


    /**
     * 用户角色
     */
    private String userRole;

    private static final long serialVersionUID = 1L;

}
