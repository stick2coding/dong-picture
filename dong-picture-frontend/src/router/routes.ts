import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import PictureManagePage from '@/pages/PictureManagePage.vue'
import { MailOutlined } from '@ant-design/icons-vue'
import { h } from 'vue'

import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import { ACCESS_ENUM } from '@/access/accessEnum'
import noAuth from '@/pages/NoAuth.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    // component: HomeView,
    component: HomePage,
    meta: {
      icon: () => h(MailOutlined),
    },
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('../views/AboutView.vue'),
  },
  {
    path: '/user/login',
    name: '用户登录',
    component: UserLoginPage,
    meta: {
      show: false,
    }
  },
  {
    path: '/user/register',
    name: '用户注册',
    component: UserRegisterPage,
  },
  {
    path: '/admin/userManage',
    name: '用户管理',
    component: UserManagePage,
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/picture/manage',
    name: '图片管理',
    component: PictureManagePage,
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/noAuth',
    name: '无权限页面',
    component: noAuth,
    meta: {
      show: false,
    }
  },
]

export { routes }
