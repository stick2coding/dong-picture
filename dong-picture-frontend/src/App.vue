<script setup lang="ts">
import Basiclayout from '@/layouts/Basiclayout.vue'

// 测试代码（测试接口）
import { healthUsingGet } from '@/api/mainController'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
healthUsingGet().then((res) => {
  console.log(res)
})

// 由于已经在access.ts中获取登录信息了，这里就不需要重复获取了
//const loginUserStore = useLoginUserStore()
//loginUserStore.fetchLoginUser()
</script>

<template>
  <div id="app">
    <Basiclayout />
  </div>
</template>

<style>
#qpp {}
</style>
