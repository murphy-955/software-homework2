<template>
  <div class="container">
    <div class="welcome-section">
      <h1>欢迎使用我们的平台</h1>
      <p>注册账户以享受我们提供的所有功能和服务。我们致力于为您提供最佳的用户体验。</p>
      <ul class="features">
        <li><i class="fas fa-check-circle"></i> 安全可靠的身份验证</li>
        <li><i class="fas fa-check-circle"></i> 个性化用户体验</li>
        <li><i class="fas fa-check-circle"></i> 为您提供旅游攻略、景点推荐、交通工具查询等一站式ai服务的平台</li>
      </ul>
    </div>

    <div class="form-section">
      <div class="form-container">
        <div class="form-toggle">
          <button class="toggle-btn" @click="goToLogin">登录</button>
          <button class="toggle-btn active">注册</button>
        </div>

        <form class="form" @submit.prevent="handleRegister">
          <h2>创建新账户</h2>
          <div class="input-group">
            <label for="username">用户名</label>
            <input 
              type="text" 
              id="username" 
              v-model="username" 
              placeholder="请输入用户名" 
              required 
            >
          </div>
          <div class="input-group">
            <label for="password">密码</label>
            <input 
              type="password" 
              id="password" 
              v-model="password" 
              placeholder="请输入密码" 
              required 
            >
          </div>
          <div class="input-group">
            <label for="confirmPassword">确认密码</label>
            <input 
              type="password" 
              id="confirmPassword" 
              v-model="confirmPassword" 
              placeholder="请再次输入密码" 
              required 
            >
          </div>
          <button 
            type="submit" 
            class="submit-btn" 
            :disabled="isLoading"
          >
            <span v-if="isLoading" class="loading"></span>
            <span>{{ isLoading ? '处理中...' : '注册' }}</span>
          </button>
          <div v-if="message" :class="['message', messageType]">
            {{ message }}
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { authAPI } from '../utils/api';
import CryptoJS from 'crypto-js';

export default {
  name: 'Register',
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      isLoading: false,
      message: '',
      messageType: ''
    };
  },
  methods: {
    // 密码SHA256加密函数
    sha256(password) {
      return CryptoJS.SHA256(password).toString();
    },

    // 显示消息
    showMessage(message, type) {
      this.message = message;
      this.messageType = type;
      setTimeout(() => {
        this.message = '';
      }, 5000);
    },

    // 跳转到登录页面
    goToLogin() {
      this.$router.push('/login');
    },

    // 处理注册
    async handleRegister() {
      // 表单验证
      if (!this.username || !this.password || !this.confirmPassword) {
        this.showMessage('请填写所有字段', 'error');
        return;
      }

      if (this.password !== this.confirmPassword) {
        this.showMessage('两次输入的密码不一致', 'error');
        return;
      }

      if (this.password.length < 6) {
        this.showMessage('密码长度至少为6位', 'error');
        return;
      }

      this.isLoading = true;

      try {
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 1000));

        // 调用注册API
        const response = await authAPI.register({
          userName: this.username,
          password: this.sha256(this.password)
        });

        // 由于是模拟环境，直接返回成功
        // 实际环境中应该根据API返回判断
        this.showMessage('注册成功！正在跳转到登录页面...', 'success');
        
        // 跳转到登录页面
        setTimeout(() => {
          this.goToLogin();
        }, 2000);
      } catch (error) {
        console.error('注册失败:', error);
        this.showMessage('注册失败，请稍后重试', 'error');
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.container {
  width: 100%;
  min-height: 100vh;
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  border-radius: 20px;
  overflow: hidden;
}

.welcome-section {
  flex: 1;
  background: linear-gradient(135deg, rgba(106, 17, 203, 0.9), rgba(37, 117, 252, 0.9));
  color: white;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.welcome-section h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.welcome-section p {
  font-size: 1.1rem;
  line-height: 1.6;
  margin-bottom: 30px;
}

.features {
  list-style: none;
}

.features li {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.features i {
  margin-right: 10px;
  font-size: 1.2rem;
}

.form-section {
  flex: 1;
  background: white;
  padding: 40px;
}

.form-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.form-toggle {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.toggle-btn {
  padding: 10px 20px;
  background: none;
  border: none;
  font-size: 1.2rem;
  font-weight: 600;
  color: #777;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.toggle-btn.active {
  color: #2575fc;
}

.toggle-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 3px;
  background: #2575fc;
  border-radius: 3px 3px 0 0;
}

.form {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form h2 {
  margin-bottom: 25px;
  color: #333;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

.input-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
}

.input-group input:focus {
  border-color: #2575fc;
  box-shadow: 0 0 0 2px rgba(37, 117, 252, 0.2);
  outline: none;
}

.submit-btn {
  background: linear-gradient(135deg, #6a11cb, #2575fc);
  color: white;
  border: none;
  padding: 14px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(37, 117, 252, 0.4);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.message {
  margin-top: 20px;
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  font-weight: 500;
}

.message.success {
  background: #e7f7ef;
  color: #2ecc71;
}

.message.error {
  background: #fde8e8;
  color: #e74c3c;
}

.loading {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, .3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
  margin-right: 10px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }

  .welcome-section {
    padding: 30px;
  }

  .form-section {
    padding: 30px;
  }
}
</style>