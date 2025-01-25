package com.dong.picture.aop;

import com.dong.picture.annotation.AuthCheck;
import com.dong.picture.exception.BusinessException;
import com.dong.picture.exception.ErrorCode;
import com.dong.picture.model.entity.User;
import com.dong.picture.model.enums.UserRoleEnum;
import com.dong.picture.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.servlet.http.HttpServletRequest;

/**
 * 两个注解
 * 一个标识是切面类
 * 一个标识被spring容器加载
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截，使用around环绕通知
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 拿到必须的角色名称
        String mustRole = authCheck.mustRole();
        // 这里应该说是通过上下文拿到请求
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 这里转换成具体的http请求
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        // 然后通过request获取当前的用户
        User loginUser = userService.getLoginUser(request);

        // 获取必须要的角色枚举类
        UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 判断是否需要放行，如果获取不到，就放行
        if (mustUserRoleEnum == null){
            // 执行方法
            return joinPoint.proceed();
        }
        // 如果需要进行权限校验
        // 获取当前用户的角色，就进行校验
        UserRoleEnum currentUserRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        // 如果当前角色没有角色，跳出
        if (currentUserRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 如果需要的是管理员权限，但是用户只有用户权限，则跳出
        if (currentUserRoleEnum.equals(UserRoleEnum.USER) && mustUserRoleEnum.equals(UserRoleEnum.ADMIN)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 通过权限校验，放行
        return joinPoint.proceed();
    }


}
