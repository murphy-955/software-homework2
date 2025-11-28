<template>
  <div class="login-container">
    <div class="container">
      <div class="welcome-section">
        <h1>欢迎使用我们的平台</h1>
        <p>登录或注册账户以享受我们提供的所有功能和服务。我们致力于为您提供最佳的用户体验。</p>
        <ul class="features">
          <li><i class="fas fa-check-circle"></i> 安全可靠的身份验证</li>
          <li><i class="fas fa-check-circle"></i> 个性化用户体验</li>
          <li><i class="fas fa-check-circle"></i> 为您提供旅游攻略、景点推荐、交通工具查询等一站式ai服务的平台</li>
        </ul>
      </div>

      <div class="form-section">
        <div class="form-container">
          <div class="form-toggle">
            <button class="toggle-btn" :class="{ active: activeForm === 'login' }" @click="activeForm = 'login'">登录</button>
            <button class="toggle-btn" :class="{ active: activeForm === 'register' }" @click="activeForm = 'register'">注册</button>
          </div>

          <form v-show="activeForm === 'login'" class="form" @submit.prevent="handleLogin">
            <h2>登录您的账户</h2>
            <div class="input-group">
              <label for="login-username">用户名</label>
              <input type="text" id="login-username" v-model="loginForm.username" placeholder="请输入用户名" required>
            </div>
            <div class="input-group">
              <label for="login-password">密码</label>
              <input type="password" id="login-password" v-model="loginForm.password" placeholder="请输入密码" required>
            </div>
            <button type="submit" class="submit-btn" :disabled="isLoginLoading">
              <span class="btn-text">{{ isLoginLoading ? '<span class="loading"></span>处理中...' : '登录' }}</span>
            </button>
            <div v-if="loginMessage" class="message" :class="{ success: loginMessageType === 'success', error: loginMessageType === 'error' }">{{ loginMessage }}</div>
          </form>

          <form v-show="activeForm === 'register'" class="form" @submit.prevent="handleRegister">
            <h2>创建新账户</h2>
            <div class="input-group">
              <label for="register-username">用户名</label>
              <input type="text" id="register-username" v-model="registerForm.username" placeholder="请输入用户名" required>
            </div>
            <div class="input-group">
              <label for="register-password">密码</label>
              <input type="password" id="register-password" v-model="registerForm.password" placeholder="请输入密码" required>
            </div>
            <div class="input-group">
              <label for="confirm-password">确认密码</label>
              <input type="password" id="confirm-password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码" required>
            </div>
            <button type="submit" class="submit-btn" :disabled="isRegisterLoading">
              <span class="btn-text">{{ isRegisterLoading ? '<span class="loading"></span>处理中...' : '注册' }}</span>
            </button>
            <div v-if="registerMessage" class="message" :class="{ success: registerMessageType === 'success', error: registerMessageType === 'error' }">{{ registerMessage }}</div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { authAPI } from '../utils/api'
import { encryptPassword } from '../utils/crypto'
import { saveUserInfo } from '../utils/auth'

export default {
  name: 'Login',
  data() {
    return {
      activeForm: 'login',
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        password: '',
        confirmPassword: ''
      },
      isLoginLoading: false,
      isRegisterLoading: false,
      loginMessage: '',
      loginMessageType: '',
      registerMessage: '',
      registerMessageType: ''
    }
  },
  methods: {
    // 显示消息
    showMessage(type, message, messageType) {
      if (type === 'login') {
        this.loginMessage = message
        this.loginMessageType = messageType
      } else {
        this.registerMessage = message
        this.registerMessageType = messageType
      }
      
      setTimeout(() => {
        if (type === 'login') {
          this.loginMessage = ''
        } else {
          this.registerMessage = ''
        }
      }, 5000)
    },
    
    // 处理登录
    async handleLogin() {
      const { username, password } = this.loginForm
      
      if (!username || !password) {
        this.showMessage('login', '请输入用户名和密码', 'error')
        return
      }
      
      this.isLoginLoading = true
      
      try {
        // 使用工具函数进行密码加密和API调用
        const encryptedPassword = encryptPassword(password)
        const response = await authAPI.login({
          username: username,
          password: encryptedPassword,
          loginType: 'ACCOUNT_SECRET_LOGIN'
        })
        
        if (response.token) {
          this.showMessage('login', '登录成功！正在跳转...', 'success')
          
          // 使用工具函数保存用户信息
          saveUserInfo(response.userInfo || { username }, response.token)
          
          // 跳转到首页
          setTimeout(() => {
            this.$router.push('/')
          }, 1500)
        } else {
          this.showMessage('login', response.message || '登录失败', 'error')
        }
      } catch (error) {
        this.showMessage('login', error.response?.data?.message || '网络错误，请稍后重试', 'error')
      } finally {
        this.isLoginLoading = false
      }
    },
    
    // 处理注册
    async handleRegister() {
      const { username, password, confirmPassword } = this.registerForm
      
      if (!username || !password || !confirmPassword) {
        this.showMessage('register', '请填写所有字段', 'error')
        return
      }
      
      if (password !== confirmPassword) {
        this.showMessage('register', '两次输入的密码不一致', 'error')
        return
      }
      
      if (password.length < 6) {
        this.showMessage('register', '密码长度至少为6位', 'error')
        return
      }
      
      this.isRegisterLoading = true
      
      try {
        // 使用工具函数进行密码加密和API调用
        const encryptedPassword = encryptPassword(password)
        const response = await authAPI.register({
          username: username,
          password: encryptedPassword
        })
        
        if (response.success) {
          this.showMessage('register', '注册成功！请登录', 'success')
          
          // 切换到登录表单
          setTimeout(() => {
            this.activeForm = 'login'
            this.loginForm.username = username
          }, 2000)
        } else {
          this.showMessage('register', response.message || '注册失败', 'error')
        }
      } catch (error) {
        this.showMessage('register', error.response?.data?.message || '网络错误，请稍后重试', 'error')
      } finally {
        this.isRegisterLoading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.container {
  width: 100%;
  max-width: 900px;
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
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(37, 117, 252, 0.4);
}

.submit-btn:active {
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
  to { transform: rotate(360deg); }
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
