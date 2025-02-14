// 权限校验
import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { message } from 'ant-design-vue'

// 是否为首次获取登录用户
let firstFetchLoginUser = true;

// 这里属于是一个拦截，每次方法路由时，都要进行权限校验
// 虽然菜单的显示已经做了一次权限校验，但是用户可能已经知道了页面的请求路径，如果直接通过路径访问，那么就会绕过菜单的权限校验，所以这里需要做一次权限校验
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore();
  let loginUser = loginUserStore.loginUser;
  // 如果当前还没获取登录用户信息，就先等待进行获取
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser();
    loginUser = loginUserStore.loginUser;
    firstFetchLoginUser = false;
   }
  // 拿到路由想要到达的路径，如果路径有admin，那么就要校验用户是否有管理员权限
  const toUrl = to.fullPath;
  if(toUrl.startsWith("/admin")){
    if(!loginUser || loginUser.userRole != "admin"){
      message.error("您没有管理员权限，无法访问！");
      next(`/user/login?redirect=${to.fullPath}`);
    }
  }
  next();
});
