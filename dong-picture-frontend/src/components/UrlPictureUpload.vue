<template>
  <div class="url-picture-upload">
    <a-input-group compact style="margin-bottom: 16px">
      <a-input v-model:value="fileUrl" style="width: calc(100% - 120px)" placeholder="请输入图片 URL" />
      <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px">提交</a-button>
    </a-input-group>
    <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
  </div>

</template>

<script setup lang="ts">


import { ref } from 'vue'
import { uploadPictureByUrlUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'

const loading = ref<boolean>(false)
const fileUrl = ref<string>()

//该pictureUpload组件为受控组件，由父组件（图片创建页面）来管理，需要定义属性
//picture 就是已上传的图片信息，会展示出来；onSuccess 是上传成功后，需要将得到的新图片信息返回给父组件，来更新 picture 的值。
interface Props {
  picture?: API.PictureVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

/**
 * 上传
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
      // 将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('图片上传失败')
  } finally {
    loading.value = false
  }
}


</script>
