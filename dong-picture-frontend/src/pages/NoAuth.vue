<template>
  <div id="noAuth">
    <h1>{{ msg }}</h1>
  </div>
</template>

<script setup lang="ts">
const msg = "你没有权限访问此页面";
</script>

<style scoped>
#noAuth {
}
</style>
