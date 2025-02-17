package com.dong.picture.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dong.picture.exception.BusinessException;
import com.dong.picture.exception.ErrorCode;
import com.dong.picture.model.dto.user.UserQueryRequest;
import com.dong.picture.model.vo.LoginUserVO;
import com.dong.picture.model.entity.User;
import com.dong.picture.model.enums.UserRoleEnum;
import com.dong.picture.model.vo.UserVO;
import com.dong.picture.service.UserService;
import com.dong.picture.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dong.picture.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author sunbin
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-01-20 18:29:29
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 根据请求来构建数据库查询对象
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProlile();
        String userRole = userQueryRequest.getUserRole();
        String userAccount = userQueryRequest.getUserAccount();
        // 分页
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        // 构建查询对象
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), "asc".equals(sortOrder), sortField);
        return queryWrapper;
    }

    /**
     * 批量获取脱敏后的用户信息
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null){
            return null;
        }

        // 把数据库实体字段拷贝给脱敏后视图，跳过脱敏字段
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user,userVO);
        return userVO;

    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已经登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }

        // 如果已经登录，就移出登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取当前登录用户
     * 就是从session中拿到用户信息，然后通过用户ID到数据库查询最新的数据
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断当前用户是否已经登录，就是查看session中是否存在登录态
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 转类为用户
        User currentUser = (User) userObj;
        // 校验
        if(currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 然后从数据库查询最新的登录信息
        long userId = currentUser.getId();
        currentUser = this.baseMapper.selectById(userId);
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 用户登录成功后，将用户信息脱敏，然后放入要返回的数据中
     * 就是按照返回字段进行数据复制，如果返回字段没有，就跳过
     * @param user
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    /**
     * 用户登录
     * 根据用户名和密码到数据库进行匹配
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

        // 加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 记住用户
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        // 这里是获取脱敏的数据
        return this.getLoginUserVO(user);
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if(userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 两次密码不一致
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        // 检查用户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 新用户数据入库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(userAccount);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean save = this.save(user);
        if (!save){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败");
        }
        return user.getId();
    }

    /**
     * 密码加密
     * @param userPassword
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值
        final String SALT = "dong";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }
}




