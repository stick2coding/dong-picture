package com.dong.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dong.picture.model.dto.UserQueryRequest;
import com.dong.picture.model.vo.LoginUserVO;
import com.dong.picture.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dong.picture.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author sunbin
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-01-20 18:29:29
*/
public interface UserService extends IService<User> {

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取脱敏后的用户列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取脱敏后的用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 用户注销
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 从请求携带的session中获取到之前登录的用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    String getEncryptPassword(String userPassword);
}
