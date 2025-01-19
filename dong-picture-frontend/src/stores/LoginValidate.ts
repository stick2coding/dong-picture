const ACCESS_ENUM = {
  ADMIN: 'admin',
  USER: 'user',
  NOT_LOGIN: 'notLogin',
};

export { ACCESS_ENUM };

//定义校验方法
const checkAccess = (loginUser: any, needAccess = ACCESS_ENUM.NOT_LOGIN) => {
  // 获取当前用户的具体权限信息，如果获取不到就是没有登录
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  if (needAccess === ACCESS_ENUM.NOT_LOGIN){
    return true;
  }

  // 如果用户登录才能访问
  if (needAccess === ACCESS_ENUM.USER) {
    //这个时候，如果用户的权限是没有登录，就不可以访问
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN){
      return false;
    }
  }

  // 如果需要管理员才能访问
  if (needAccess === ACCESS_ENUM.ADMIN) {
    //这个时候，如果用户的权限不是管理员，就不可以访问
    if (loginUserAccess !== ACCESS_ENUM.ADMIN){
      return false;
    }
  }

  return true;
}

export default checkAccess;
