<template>
  <div id="globalHeader">
    <!--优化导航栏的布局，采用格栅组件的自适应布局，简单说就是一行三列，左中右结构-->
    <a-row :wrap="false">
      <!--顶部栏左侧显示logo-->
      <a-col flex="200px">
        <!--增加一个可以跳转的链接，类似点击logo前往首页的功能-->
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">东东云图库</div>
          </div>
        </router-link>
      </a-col>
      <!--中间显示菜单，同时要给菜单配置跳转事件，见doMenuClick定义，同时这里需要绑定事件-->
      <a-col flex="auto">
        <a-menu v-model:selectedKeys="current" mode="horizontal" :items="items" @click="doMenuClick" />
      </a-col>
      <!--顶部栏右侧显示登录信息-->
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            {{ loginUserStore.loginUser.username ?? '无名' }}
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<!--ts脚本-->
<script lang="ts" setup>
//导入类库
import { computed, h, ref } from 'vue'
import { MailOutlined, AppstoreOutlined, SettingOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
//导入路由
import { useRouter } from 'vue-router'
//导入用户信息存储
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
//导入路由表
import { routes } from '@/router/routes'
import checkAccess from '@/stores/LoginValidate'

//定义全局变量
//定义用户信息使用
const loginUserStore = useLoginUserStore()
// 当前选中的菜单
const current = ref<string[]>([])
// 拿到用户路由
const router = useRouter()
//路由变化后，需要执行一些操作
router.afterEach((to, from, next) => {
  // 修改当前选中的菜单
  current.value = [to.path]
})
//定义菜单，这里是写死的数组，可以改用通过路由表映射来获取
/*const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(MailOutlined),
    label: 'home',
    title: 'home',
  },
  {
    key: '/about',
    icon: () => h(AppstoreOutlined),
    label: 'about',
    title: 'about',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://www.baidu.com', target: '_blank' }, 'dongdong'),
    title: 'dongdong',
  },
])*/

const items = computed(() =>
  // 遍历
  routes.filter((item) => {
    // 判断是否显示
    if(item.meta?.show == false){
      return false
    }
    // 判断权限、
    if(!checkAccess(loginUserStore.loginUser, item.meta?.authCheck as String)){
      return false
    }
    return true
  }).map((item) => {
    return {
      key: item.path,
      icon: item.meta?.icon,
      label: item.name,
      title: item.name,
    }
  }),
)

// 定义菜单的跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  // 跳转到用户点击的菜单
  router.push({
    path: key,
  })
}
</script>

<!--这里是样式-->
<style scoped>
#globalHeader .title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>
