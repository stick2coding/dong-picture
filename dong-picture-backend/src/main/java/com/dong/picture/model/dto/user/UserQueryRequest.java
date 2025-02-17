package com.dong.picture.model.dto.user;

import com.dong.picture.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户简介
     */
    private String userProlile;

    /**
     * 用户角色
     */
    private String userRole;

    private static final long serialVersionUID = 1L;


}
