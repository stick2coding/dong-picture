import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUserUsingGet } from '@/api/userController'

export const useLoginUserStore = defineStore('loginUser', () => {
  // ref<any> 表示 loginUser 是一个可变的响应式对象，其中的值可以动态地改变。
  // 目前我们已经知道后端返回的数据类型，这里就可以修改
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  // 刷新用户信息
  async function fetchLoginUser() {
    // 获取登录用户信息
    // 通过使用后端接口来获取登录用户信息
    const res = await getLoginUserUsingGet()
    if (res.data.code == 0  && res.data.data) {
      loginUser.value = res.data.data
      console.log('loginUser.value', loginUser.value)
    }
    // 模拟用户登录
    // setTimeout(() => {
    //   loginUser.value = {
    //     userName: 'admin',
    //     id: 1,
    //     userRole: 'admin',
    //   }
    // }, 3000)
  }

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  return { loginUser, setLoginUser, fetchLoginUser }
})
