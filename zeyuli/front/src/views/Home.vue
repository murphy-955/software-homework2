<template>
  <div class="app-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="p-6 border-b">
        <div class="font-bold text-blue-600 text-xl">TravelMate</div>
      </div>
      <nav>
        <div class="sidebar-item active" @click="navigateTo('/home')">
          <span class="icon">🏠</span>
          <span>首页</span>
        </div>
        <div class="sidebar-item" @click="navigateTo('/result')">
          <span class="icon">📅</span>
          <span>我的行程</span>
        </div>
        <div class="sidebar-item" @click="navigateTo('/map')">
          <span class="icon">🗺️</span>
          <span>地图视图</span>
        </div>
        <div class="sidebar-item" @click="navigateTo('/budget')">
          <span class="icon">💰</span>
          <span>预算管理</span>
        </div>
        <div class="sidebar-item" @click="navigateTo('/profile')">
          <span class="icon">👤</span>
          <span>个人中心</span>
        </div>
      </nav>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="flex items-center gap-4">
          <div class="relative">
            <input 
              type="text" 
              placeholder="搜索行程、景点..." 
              class="input pl-10 w-64"
            />
            <span class="icon absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500">🔍</span>
          </div>
        </div>
        <div class="flex items-center gap-4">
          <button type="button" class="btn btn-text">
            <span class="icon">🔔</span>
          </button>
          <div class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center">
              <span class="icon">👤</span>
            </div>
            <span class="text-sm font-medium">张三</span>
          </div>
        </div>
      </header>

      <!-- 内容区域 -->
      <div class="content-area">
        <div class="card mb-6">
          <div class="text-center mb-6">
            <h1 class="text-3xl font-bold mb-2">智能规划您的完美旅程</h1>
            <p class="text-gray-600">告诉我您的旅行需求，3步生成专属行程</p>
          </div>
          <div class="flex justify-center mb-6">
            <div class="w-32 h-32 rounded-full bg-blue-100 flex items-center justify-center">
              <span class="icon text-6xl">🗺️</span>
            </div>
          </div>
          <div class="bg-gray-50 rounded-xl p-4 mb-6">
            <textarea 
              class="w-full h-40 bg-transparent text-gray-700 resize-none outline-none" 
              placeholder="例如：'上海三日游，2000元预算，喜欢美食和艺术'" 
              style="font-size: 16px;"
              v-model="travelRequest"
            ></textarea>
          </div>
          <div class="flex gap-3 flex-wrap mb-6">
            <button type="button" class="btn btn-secondary" @click="selectTemplate('北京三日游 1500元')">北京三日游 1500元</button>
            <button type="button" class="btn btn-secondary" @click="selectTemplate('杭州两日游 美食之旅')">杭州两日游 美食之旅</button>
            <button type="button" class="btn btn-secondary" @click="selectTemplate('成都四日游 文化体验')">成都四日游 文化体验</button>
            <button type="button" class="btn btn-secondary" @click="selectTemplate('西安三日游 历史文化')">西安三日游 历史文化</button>
            <button type="button" class="btn btn-secondary" @click="selectTemplate('厦门两日游 海滨风光')">厦门两日游 海滨风光</button>
          </div>
          <div class="flex justify-center">
            <button type="button" class="btn btn-primary px-12" @click="startPlanning">开始规划</button>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-6">
          <!-- 最近规划 -->
          <div class="card">
            <h3 class="font-bold text-lg mb-4">最近规划</h3>
            <div class="space-y-3">
              <div class="p-3 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100" @click="navigateTo('/result')">
                <div class="font-medium">北京文化三日游</div>
                <div class="text-gray-600">3天 · ¥1560</div>
              </div>
              <div class="p-3 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100" @click="navigateTo('/result')">
                <div class="font-medium">杭州西湖两日游</div>
                <div class="text-gray-600">2天 · ¥980</div>
              </div>
              <div class="p-3 bg-gray-50 rounded-lg cursor-pointer hover:bg-gray-100" @click="navigateTo('/result')">
                <div class="font-medium">成都四日游 文化体验</div>
                <div class="text-gray-600">4天 · ¥2100</div>
              </div>
            </div>
          </div>

          <!-- 热门推荐 -->
          <div class="card">
            <h3 class="font-bold text-lg mb-4">热门推荐</h3>
            <div class="grid grid-cols-2 gap-3">
              <div class="p-3 border border-gray-200 rounded-lg cursor-pointer hover:border-blue-500 transition">
                <h4 class="font-medium">故宫博物院</h4>
                <p class="text-xs text-gray-600 mt-1">北京必去景点</p>
                <div class="text-orange-500 mt-2">¥60</div>
              </div>
              <div class="p-3 border border-gray-200 rounded-lg cursor-pointer hover:border-blue-500 transition">
                <h4 class="font-medium">西湖</h4>
                <p class="text-xs text-gray-600 mt-1">杭州标志性景点</p>
                <div class="text-orange-500 mt-2">免费</div>
              </div>
              <div class="p-3 border border-gray-200 rounded-lg cursor-pointer hover:border-blue-500 transition">
                <h4 class="font-medium">兵马俑</h4>
                <p class="text-xs text-gray-600 mt-1">世界第八大奇迹</p>
                <div class="text-orange-500 mt-2">¥120</div>
              </div>
              <div class="p-3 border border-gray-200 rounded-lg cursor-pointer hover:border-blue-500 transition">
                <h4 class="font-medium">鼓浪屿</h4>
                <p class="text-xs text-gray-600 mt-1">厦门海上花园</p>
                <div class="text-orange-500 mt-2">¥90</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { planByBudget } from '../api/itinerary'

const router = useRouter()

// 旅行需求输入
const travelRequest = ref('')

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
}

// 选择推荐模板
const selectTemplate = (template) => {
  travelRequest.value = template
}

// 开始规划行程
const startPlanning = async () => {
  try {
    // 这里可以添加解析旅行需求的逻辑
    // 暂时使用模拟数据
    const params = {
      city: '北京',
      days: 3,
      budget: 1500
    }
    
    const result = await planByBudget(params, {})
    // 保存行程结果到本地存储
    localStorage.setItem('currentItinerary', JSON.stringify(result))
    // 跳转到结果页面
    router.push('/result')
  } catch (error) {
    console.error('规划行程失败:', error)
  }
}
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
}

/* 图标样式 */
.icon {
  font-size: 18px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .app-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    position: relative;
  }
  
  .main-content {
    margin-left: 0;
  }
  
  .grid-cols-2 {
    grid-template-columns: 1fr;
  }
}
</style>
