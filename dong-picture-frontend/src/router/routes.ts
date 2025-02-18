import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue'
import { MailOutlined } from '@ant-design/icons-vue'
import { h } from 'vue'

import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import { ACCESS_ENUM } from '@/access/accessEnum'
import noAuth from '@/pages/NoAuth.vue'
import AddPicturePage from '@/pages/picture/AddPicturePage.vue'
import PictureDetailPage from '@/pages/picture/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/picture/AddPictureBatchPage.vue'

const routes = [
  {
    path: '/',
    name: '主页',
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
    meta: {
      show: false,
    }
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
    path: '/add_picture',
    name: '创建图片',
    component: AddPicturePage,
  },
  {
    path: '/add_picture/batch',
    name: '批量创建图片',
    component: AddPictureBatchPage,
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  },
  {
    path: '/picture/:id',
    name: '图片详情',
    component: PictureDetailPage,
    props: true,
    meta: {
      show: false,
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
