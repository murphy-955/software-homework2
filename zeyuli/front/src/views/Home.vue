<template>
  <div class="home-container">
    <nav class="navbar">
      <div class="navbar-brand">旅行助手</div>
      <div class="navbar-menu">
        <a href="/" class="nav-item active">首页</a>
        <a href="/map" class="nav-item">地图服务</a>
        <a href="/student" class="nav-item">学生线路</a>
      </div>
      <div class="navbar-user">
        <span class="username">{{ username }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </nav>

    <div class="main-content">
      <div class="left-panel">
        <div class="plan-form">
          <h2>行程规划</h2>
          <form @submit.prevent="generatePlan" class="plan-form-content">
            <div class="form-group">
              <label for="departure">出发城市</label>
              <select id="departure" v-model="planForm.departure" required>
                <option value="" disabled>选择出发城市</option>
                <option v-for="city in cities" :key="city" :value="city">{{ city }}</option>
              </select>
            </div>
            <div class="form-group">
              <label for="destination">目的城市</label>
              <select id="destination" v-model="planForm.destination" required>
                <option value="" disabled>选择目的城市</option>
                <option v-for="city in cities" :key="city" :value="city">{{ city }}</option>
              </select>
            </div>
            <div class="form-group">
              <label for="startDate">开始日期</label>
              <input type="date" id="startDate" v-model="planForm.startDate" required>
            </div>
            <div class="form-group">
              <label for="endDate">结束日期</label>
              <input type="date" id="endDate" v-model="planForm.endDate" required>
            </div>
            <div class="form-group">
              <label for="requirements">旅行需求</label>
              <textarea id="requirements" v-model="planForm.requirements" placeholder="请描述您的旅行偏好、兴趣点、预算等信息" rows="4"></textarea>
            </div>
            <button type="submit" class="generate-btn" :disabled="isGenerating">
              {{ isGenerating ? '生成中...' : '生成行程' }}
            </button>
          </form>
        </div>

        <div v-if="currentSession && currentSession.session_id" class="chat-form">
          <h3>继续完善行程</h3>
          <form @submit.prevent="sendMessage" class="chat-form-content">
            <textarea v-model="chatMessage" placeholder="输入您的问题或需求" rows="2"></textarea>
            <button type="submit" class="send-btn" :disabled="!chatMessage.trim() || isSending">
              {{ isSending ? '发送中...' : '发送' }}
            </button>
          </form>
        </div>
      </div>

      <div class="right-panel">
        <div class="plan-result">
          <div v-if="!currentSession || !planGenerated" class="empty-state">
            <h3>欢迎使用旅行助手</h3>
            <p>请在左侧填写您的旅行信息，生成个性化的旅行计划</p>
          </div>
          
          <div v-else-if="planGenerated" class="result-content">
            <div class="result-header">
              <h3>旅行计划</h3>
              <div class="session-info">
                <span>会话ID: {{ currentSession.session_id }}</span>
                <button class="clear-btn" @click="clearHistory">清除历史</button>
              </div>
            </div>
            
            <div class="messages-container">
              <div v-for="msg in messages" :key="msg.id" class="message-wrapper">
                <div v-if="msg.type === 'user'" class="user-message">
                  <div class="message-header">
                    <span class="user-label">您</span>
                    <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
                  </div>
                  <div class="message-content">
                    {{ msg.content }}
                  </div>
                </div>
                <div v-else-if="msg.type === 'assistant'" class="assistant-message">
                  <div class="message-header">
                    <span class="assistant-label">旅行助手</span>
                    <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
                  </div>
                  <div class="message-content markdown-content" v-html="renderMarkdown(msg.content)"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import MarkdownIt from 'markdown-it'
import { travelAPI } from '../utils/api'
import { isLoggedIn, logout as authLogout, getChatHistory, saveChatHistory, getSessionId, saveSessionId } from '../utils/auth'

const router = useRouter()
const md = new MarkdownIt({
  html: true,
  breaks: true,
  linkify: true
})

// 表单数据
const formData = ref({
  startCity: '',
  endCity: '',
  startDate: '',
  endDate: '',
  requirements: ''
})

// 城市列表
const cities = ref([
  '北京', '上海', '广州', '深圳', '杭州', '南京', '成都', '重庆', '西安', '武汉',
  '天津', '苏州', '郑州', '长沙', '青岛', '厦门', '大连', '济南', '哈尔滨', '石家庄'
])

// 对话历史
const chatHistory = ref([])
const currentResponse = ref('')
const isGenerating = ref(false)
const userQuery = ref('')
const loading = ref(false)

// 计算属性：渲染后的Markdown内容
const renderedResponses = computed(() => {
  return chatHistory.value.map(item => ({
    ...item,
    renderedContent: item.role === 'assistant' ? md.render(item.content) : item.content
  }))
})

// 当前响应的渲染结果
const renderedCurrentResponse = computed(() => {
  return currentResponse.value ? md.render(currentResponse.value) : ''
})

// 检查登录状态
onMounted(() => {
  if (!isLoggedIn()) {
    router.push('/login')
    return
  }
  
  // 加载聊天历史
  const savedHistory = getChatHistory()
  if (savedHistory.length > 0) {
    chatHistory.value = savedHistory
  }
})

// 生成旅行计划
const generatePlan = async () => {
  // 验证表单
  if (!formData.value.startCity || !formData.value.endCity || !formData.value.startDate || !formData.value.endDate) {
    alert('请填写必要的旅行信息')
    return
  }
  
  isGenerating.value = true
  currentResponse.value = ''
  loading.value = true
  
  try {
    const requestData = {
      startCity: formData.value.startCity,
      endCity: formData.value.endCity,
      startDate: formData.value.startDate,
      endDate: formData.value.endDate,
      requirements: formData.value.requirements,
      sessionId: getSessionId()
    }
    
    // 调用API生成旅行计划
    const response = await travelAPI.generatePlan(requestData)
    
    if (response.sessionId) {
      saveSessionId(response.sessionId)
    }
    
    // 添加用户消息到历史
    chatHistory.value.push({
      role: 'user',
      content: `从${formData.value.startCity}到${formData.value.endCity}的旅行计划，日期：${formData.value.startDate}到${formData.value.endDate}，需求：${formData.value.requirements}`
    })
    
    // 处理AI回复
    if (response.content) {
      // 模拟打字效果
      await typeWriter(response.content)
      
      // 添加AI回复到历史
      chatHistory.value.push({
        role: 'assistant',
        content: currentResponse.value
      })
      
      // 保存到localStorage
      saveChatHistory(chatHistory.value)
    }
  } catch (error) {
    console.error('生成旅行计划失败:', error)
    alert('生成旅行计划失败，请重试')
  } finally {
    isGenerating.value = false
    loading.value = false
  }
}

// 继续对话
const continueConversation = async () => {
  if (!userQuery.value.trim()) return
  
  const sessionId = getSessionId()
  if (!sessionId) {
    alert('请先生成一个旅行计划')
    return
  }
  
  isGenerating.value = true
  currentResponse.value = ''
  loading.value = true
  
  try {
    // 保存用户消息
    const userMessage = userQuery.value
    chatHistory.value.push({
      role: 'user',
      content: userMessage
    })
    
    // 调用API继续对话
    const response = await travelAPI.continueConversation({
      query: userMessage,
      sessionId: sessionId
    })
    
    // 处理AI回复
    if (response.content) {
      // 模拟打字效果
      await typeWriter(response.content)
      
      // 添加AI回复到历史
      chatHistory.value.push({
        role: 'assistant',
        content: currentResponse.value
      })
      
      // 清空输入框
      userQuery.value = ''
      
      // 保存到localStorage
      saveChatHistory(chatHistory.value)
    }
  } catch (error) {
    console.error('继续对话失败:', error)
    alert('继续对话失败，请重试')
  } finally {
    isGenerating.value = false
    loading.value = false
  }
}

// 打字效果
const typeWriter = async (text) => {
  return new Promise(resolve => {
    let i = 0
    const speed = 30 // 加快打字速度
    
    function type() {
      if (i < text.length) {
        currentResponse.value += text.charAt(i)
        i++
        setTimeout(type, speed)
      } else {
        resolve()
      }
    }
    
    type()
  })
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 清除历史记录
const clearHistory = () => {
  chatHistory.value = []
  currentResponse.value = ''
  saveChatHistory([])
}

// 登出
const logout = () => {
  authLogout()
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.navbar {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2575fc;
}

.navbar-menu {
  display: flex;
  gap: 20px;
}

.nav-item {
  padding: 8px 16px;
  text-decoration: none;
  color: #666;
  border-radius: 6px;
  transition: all 0.3s;
}

.nav-item:hover, .nav-item.active {
  color: #2575fc;
  background-color: #e6f0ff;
}

.navbar-user {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-weight: 500;
  color: #333;
}

.logout-btn {
  padding: 8px 16px;
  background: #f44336;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #d32f2f;
}

.main-content {
  flex: 1;
  display: flex;
  padding: 30px;
  gap: 30px;
}

.left-panel {
  flex: 0 0 450px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-panel {
  flex: 1;
  min-height: 600px;
}

.plan-form, .chat-form {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.plan-form h2, .chat-form h3 {
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

.form-group input, .form-group select, .form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
}

.form-group input:focus, .form-group select:focus, .form-group textarea:focus {
  border-color: #2575fc;
  box-shadow: 0 0 0 3px rgba(37, 117, 252, 0.1);
  outline: none;
}

.generate-btn, .send-btn {
  background: linear-gradient(135deg, #2575fc, #6a11cb);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  width: 100%;
}

.generate-btn:hover:not(:disabled), .send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(37, 117, 252, 0.4);
}

.generate-btn:disabled, .send-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.chat-form-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.plan-result {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  min-height: 100%;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #888;
}

.empty-state h3 {
  color: #333;
  margin-bottom: 10px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #eee;
}

.result-header h3 {
  color: #333;
  margin: 0;
}

.session-info {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 0.9rem;
  color: #666;
}

.clear-btn {
  padding: 6px 12px;
  background: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.85rem;
}

.clear-btn:hover {
  background: #d32f2f;
}

.messages-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: 500px;
  overflow-y: auto;
}

.message-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-message, .assistant-message {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-message .message-content {
  background: #e6f0ff;
  color: #2575fc;
  padding: 15px;
  border-radius: 12px;
  max-width: 90%;
  align-self: flex-end;
}

.assistant-message .message-content {
  background: #f5f5f5;
  color: #333;
  padding: 15px;
  border-radius: 12px;
  max-width: 90%;
}

.message-header {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  font-weight: 500;
}

.user-label {
  color: #2575fc;
}

.assistant-label {
  color: #6a11cb;
}

.message-time {
  color: #999;
}

/* Markdown样式 */
.markdown-content {
  line-height: 1.6;
}

.markdown-content h1, .markdown-content h2, .markdown-content h3 {
  margin-top: 20px;
  margin-bottom: 10px;
}

.markdown-content h1 { font-size: 1.8rem; }
.markdown-content h2 { font-size: 1.5rem; border-bottom: 1px solid #eee; padding-bottom: 5px; }
.markdown-content h3 { font-size: 1.3rem; }

.markdown-content ul, .markdown-content ol {
  padding-left: 25px;
  margin: 10px 0;
}

.markdown-content li {
  margin-bottom: 5px;
}

.markdown-content blockquote {
  border-left: 4px solid #2575fc;
  padding-left: 15px;
  color: #666;
  margin: 15px 0;
}

.markdown-content code {
  background: #f8f8f8;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

.markdown-content pre {
  background: #f8f8f8;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
}

.markdown-content pre code {
  background: none;
  padding: 0;
}

@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
  }
  
  .left-panel {
    flex: none;
    width: 100%;
  }
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 15px;
  }
  
  .navbar-menu {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .main-content {
    padding: 15px;
    gap: 15px;
  }
  
  .plan-form, .chat-form, .plan-result {
    padding: 20px;
  }
}
</style>
