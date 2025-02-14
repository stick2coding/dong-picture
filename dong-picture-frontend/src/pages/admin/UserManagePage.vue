<template>
  <!--这里是搜索栏，绑定的模型就是搜索参数，执行动作就是执行搜索-->
  <a-form layout="inline" :model="searchParams" @finish="doSearch">
    <a-form-item label="账号">
      <a-input v-model:value="searchParams.userAccount" placeholder="输入账号"></a-input>
    </a-form-item>
    <a-form-item label="用户名">
      <a-input v-model:value="searchParams.userName" placeholder="输入用户名"></a-input>
    </a-form-item>
    <a-form-item>
      <a-button type="primary" html-type="submit">搜索</a-button>
    </a-form-item>
  </a-form>
  <!--这里标签是进行一些数据绑定，比如列名、数据来源、当前分页信息、以及表格变化时的分页参数绑定  -->
  <a-table
    :columns="columns"
    :data-source="dataList"
    :pagination="pagination"
    @change="doTableChange"
  >
    <template #headerCell="{ column }">
      <template v-if="column.key === 'userName'">
        <span>
          <smile-outlined />
          Name
        </span>
      </template>
    </template>
    <!--     渲染数据 可以自定义样式-->
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'userAvatar'">
        <a-image :src="record.userAvatar" :width="120" />
      </template>
      <template v-else-if="column.dataIndex === 'userRole'">
        <div v-if="record.userRole === 'admin'">
          <a-tag color="green">管理员</a-tag>
        </div>
        <div v-else>
          <a-tag color="blue">普通用户</a-tag>
        </div>
      </template>
      <!--时间显示处理-->
      <template v-else-if="column.dataIndex === 'createTime'">
        {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
      </template>
      <!--增加操作按钮-->
      <template v-else-if="column.key === 'action'">
        <a-button danger @click="doDelete(record.id)">删除</a-button>
      </template>
    </template>
  </a-table>
</template>
<script lang="ts" setup>
import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
// 列名
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 从后端获取数据

const dataList = ref([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 搜索数据
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({
    ...searchParams,
  })
  console.log(res)
  if (res.data.code == 0) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
}

// 当页面加载的时候请求一次
onMounted(() => {
  fetchData()
})

// 分页参数（需要在表格中进行绑定）这里其实就是给搜索参数进行赋值
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 条`,
  }
})

// 表格分页变化处理（需要在表格中进行绑定）
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  // 重新请求一下数据
  fetchData()
}

// 手动执行一次搜索（这里是给搜索使用的，当用户填了条件点击搜索就执行一次）
const doSearch = () => {
  // 这里需要重置一下页码为第一页
  searchParams.current = 1
  fetchData()
}

// 删除操作（需要在表格中给操作绑定事件）
const doDelete = async (id:String) => {
  if(!id){
    return
  }
  // 调用方法
  const res = await deleteUserUsingPost({id})
  if (res.data.code == 0) {
    message.success('删除成功')
    fetchData()
  } else {
    message.error('删除失败' + res.data.message)
  }
}
</script>
