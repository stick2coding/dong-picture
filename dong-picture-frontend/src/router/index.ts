// 这里是路由的配置文件
// 按需修改router/index.ts文件的routes配置,定义我们需要的页面路由,每个path对应一个component(要加载的组件)
// component支持直接传入组件、或者使用import按需懒加载组件,按需加载是一种优化首次打开站点性能的方式。
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import HomePage from '@/pages/HomePage.vue'
import { routes } from '@/router/routes'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  // 这里把路由配置单独放在一个文件中，方便管理，然后将路由配置映射到页面的顶部菜单中，减少代码维护
  routes,
  // routes: [
  //   {
  //     path: '/',
  //     name: 'home',
  //     // component: HomeView,
  //     component: HomePage,
  //   },
  //   {
  //     path: '/about',
  //     name: 'about',
  //     // route level code-splitting
  //     // this generates a separate chunk (About.[hash].js) for this route
  //     // which is lazy-loaded when the route is visited.
  //     component: () => import('../views/AboutView.vue'),
  //   },
  // ],
})

export default router
