import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<any>({
    userName: '未登录',
  })

  // 刷新用户信息
  async function fetchLoginUser() {
    // TODO: 获取登录用户信息
    // 模拟用户登录
    setTimeout(() => {
      loginUser.value = {
        userName: 'admin',
        id: 1,
        userRole: 'admin',
      }
    }, 3000)
  }

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  return { loginUser, setLoginUser, fetchLoginUser }
})
