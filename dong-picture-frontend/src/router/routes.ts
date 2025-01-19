import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/UserLoginPage.vue'
import PictureManagePage from '@/pages/PictureManagePage.vue'
import { MailOutlined } from '@ant-design/icons-vue'
import { h } from 'vue'
import { ACCESS_ENUM } from '@/stores/LoginValidate'

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
    path: '/picture/manage',
    name: '图片管理',
    component: PictureManagePage,
    meta: {
      authCheck: ACCESS_ENUM.ADMIN,
    }
  }
]

export { routes }
