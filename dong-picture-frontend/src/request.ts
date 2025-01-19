// 请求
import axios from 'axios'
import { message } from 'ant-design-vue'
// 创建一个 axios 实例
const myAxios = axios.create({
  baseURL: 'http://localhost:8123',
  timeout: 60000,
  withCredentials: true,
});

// 请求拦截器
myAxios.interceptors.request.use(
  // 请求发送前
  function (config) {
    return config;
  },
  // 请求发送失败
  function (error) {
    return Promise.reject(error);
  }
);

// 响应拦截器
myAxios.interceptors.response.use(
  // 响应成功
  function (response) {

    // 拿到返回数据
    const { data } = response;
    // 判断是否登录即
    if (data.code === 40100) {
      // 用户没有登录，需要判断当前请求不是获取用户信息，且当前的页面位置不在登录页面时，跳转到登录页面
      if(!response.request.response.includes('user/get/login')
        && !window.location.pathname.includes('/user/login')){
        message.warning('登录信息已过期，请重新登录');
        window.location.href = '/user/login';
      }
    }
    //返回响应数据
    return response;
  },
  // 响应失败
  function (error) {
    return Promise.reject(error);
  }
);

export default myAxios;
