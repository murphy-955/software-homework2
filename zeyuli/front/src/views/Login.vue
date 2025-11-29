<template>
  <div class="login-container">
    <div class="login-box">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-blue-600 mb-2">TravelMate</h1>
        <p class="text-gray-600">智能旅行规划助手</p>
      </div>
      <form @submit.prevent="handleLogin">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <input 
            type="text"
            v-model="loginForm.username"
            placeholder="用户名"
            required 
            class="input"
          />
        </div>
        <div class="form-item">
          <label class="form-label">密码</label>
          <input 
            type="password" 
            v-model="loginForm.password" 
            placeholder="请输入密码" 
            required 
            class="input"
          />
        </div>
        <div class="form-item flex gap-3">
          <div class="flex-1">
            <label class="form-label">验证码</label>
            <input 
              type="text" 
              v-model="loginForm.verifyCode" 
              placeholder="验证码" 
              class="input"
            />
          </div>
          <div class="flex items-end">
            <button type="button" class="btn btn-secondary" @click="sendVerifyCode">发送验证码</button>
          </div>
        </div>
        <div class="form-item flex gap-4">
          <button type="button" class="btn btn-secondary flex-1">
            <span class="icon">📱</span>微信登录
          </button>
          <button type="button" class="btn btn-secondary flex-1">
            <span class="icon">🐧</span>QQ登录
          </button>
        </div>
        <div class="form-item flex justify-between items-center">
          <button type="button" class="btn-text">忘记密码</button>
          <button type="submit" class="btn btn-primary">登录</button>
        </div>
        <div class="text-center text-sm mt-4">
          <span class="text-gray-600">没有账户？</span>
          <button type="button" class="btn-text" @click="$router.push('/register')">立即注册</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/user'

const router = useRouter()

// 登录表单数据
const loginForm = ref({
  username: '',
  password: '',
  verifyCode: '',
  loginType: 'ACCOUNT_SECRET_LOGIN'
})

// 发送验证码
const sendVerifyCode = () => {
  // 这里可以添加发送验证码的逻辑
  console.log('发送验证码')
}

// 处理登录
const handleLogin = async () => {
  try {
    const response = await login(loginForm.value)
    // 保存token到localStorage
    localStorage.setItem('token', response.token || 'mock-token')
    // 跳转到首页
    router.push('/home')
  } catch (error) {
    console.error('登录失败:', error)
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(to bottom right, #e6f7ff, #f0f5ff);
}

.login-box {
  max-width: 480px;
  width: 100%;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 图标样式 */
.icon {
  font-size: 16px;
}
</style>
