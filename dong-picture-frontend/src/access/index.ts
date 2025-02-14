// 全局的权限管理
// 逻辑如下：
// 1.首判断页面是否需要登录权限，如果不需要，直接放行。
// 2.如果页面需要登录权限
// i.如果用户未登录，则跳转到登录页面。
// ii.如果已登录，判断登录用户的权限是否符合要求，否则跳转到401无权限页面。
import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { message } from 'ant-design-vue'
import { ACCESS_ENUM } from '@/access/accessEnum'
import checkAccess from '@/access/checkAeecss'

router.beforeEach(async (to, from, next) => {
  //必须保证 pinia 初始化在这段代码执行前
  const loginUserStore = useLoginUserStore();
  let loginUser = loginUserStore.loginUser;
  console.log("loginUser",loginUser)

  // 如果没有登录过，自动登录
  if (!loginUser || !loginUser.userRole) {
    await loginUserStore.fetchLoginUser();
    loginUser = loginUserStore.loginUser;
  }
  console.log("loginUser",loginUser)

  // 拿到页面所需权限，默认是不需要登录即可显示
  const needAccess = (to.meta?.authCheck as string) ?? ACCESS_ENUM.NOT_LOGIN;
  // 如果需要登录，单独进行判断
  if (needAccess != ACCESS_ENUM.NOT_LOGIN) {
    // 如果需要登录，但是用户还没登录（或者没任何权限或者用户权限是未登录，全部重新登录），就跳转到登录页面
    if (!loginUser || !loginUser.userRole || loginUser.userRole === ACCESS_ENUM.NOT_LOGIN) {
      message.error("您还没有登录，请先登录！");
      next(`/user/login?redirect=${to.fullPath}`);
      return
    }
    // 如果已经登录了，那么还需要判断权限是否满足
    if (!checkAccess(loginUser, needAccess)){
      next('/noAuth')
      return
    }
  }

  next();
});
