<template>
  <div class="app-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="p-6 border-b">
        <div class="font-bold text-blue-600 text-xl">TravelMate</div>
      </div>
      <nav>
        <div class="sidebar-item" @click="navigateTo('/home')">
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
        <div class="sidebar-item active" @click="navigateTo('/profile')">
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
        <div class="grid grid-cols-3 gap-6">
          <!-- 左侧个人信息和导航 -->
          <div class="col-span-1">
            <div class="card">
              <!-- 用户信息 -->
              <div class="flex items-center gap-4 p-4 bg-blue-50 rounded-lg mb-4">
                <div class="w-16 h-16 rounded-full bg-blue-100 flex items-center justify-center">
                  <span class="icon text-2xl">👤</span>
                </div>
                <div>
                  <div class="font-bold">张三</div>
                  <div class="text-sm text-blue-500">VIP会员</div>
                </div>
              </div>
              
              <!-- 导航菜单 -->
              <div class="space-y-2">
                <button type="button" class="w-full p-3 text-left border border-gray-200 rounded-lg hover:bg-gray-50 text-sm" @click="navigateTo('/favorites')">
                  <span class="icon">❤️</span>我的收藏
                </button>
                <button type="button" class="w-full p-3 text-left border border-gray-200 rounded-lg hover:bg-gray-50 text-sm" @click="navigateTo('/history')">
                  <span class="icon">📜</span>旅行历史
                </button>
                <button type="button" class="w-full p-3 text-left border border-gray-200 rounded-lg hover:bg-gray-50 text-sm" @click="navigateTo('/preferences')">
                  <span class="icon">⚙️</span>偏好设置
                </button>
                <button type="button" class="w-full p-3 text-left border border-gray-200 rounded-lg hover:bg-gray-50 text-sm" @click="navigateTo('/help')">
                  <span class="icon">❓</span>帮助与反馈
                </button>
              </div>
              
              <!-- 钱包与积分 -->
              <div class="mt-4 p-4 bg-gray-50 rounded-lg">
                <div class="font-medium mb-3">钱包与积分</div>
                <div class="flex justify-between mb-2">
                  <span>账户余额</span>
                  <span class="font-medium">¥128</span>
                </div>
                <div class="flex justify-between">
                  <span>积分余额</span>
                  <span class="font-medium">850</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 右侧内容区 -->
          <div class="col-span-2">
            <!-- 我的数据 -->
            <div class="card mb-6">
              <h3 class="font-bold text-lg mb-4">我的数据</h3>
              <div class="grid grid-cols-3 gap-4">
                <div class="p-4 bg-gray-50 rounded-lg text-center">
                  <div class="text-3xl font-bold">12</div>
                  <div class="text-gray-600 mt-1">已规划行程</div>
                </div>
                <div class="p-4 bg-gray-50 rounded-lg text-center">
                  <div class="text-3xl font-bold">35</div>
                  <div class="text-gray-600 mt-1">旅程天数</div>
                </div>
                <div class="p-4 bg-gray-50 rounded-lg text-center">
                  <div class="text-3xl font-bold">¥2400</div>
                  <div class="text-gray-600 mt-1">节省预算</div>
                </div>
              </div>
            </div>
            
            <!-- 最近活动 -->
            <div class="card">
              <h3 class="font-bold text-lg mb-4">最近活动</h3>
              <div class="space-y-3">
                <div class="p-3 border-l-4 border-blue-500 bg-gray-50 rounded">
                  <div class="font-medium">生成了北京文化三日游</div>
                  <div class="text-sm text-gray-500 mt-1">2025年11月20日 14:30</div>
                </div>
                <div class="p-3 border-l-4 border-green-500 bg-gray-50 rounded">
                  <div class="font-medium">收藏了故宫博物院</div>
                  <div class="text-sm text-gray-500 mt-1">2025年11月20日 10:15</div>
                </div>
                <div class="p-3 border-l-4 border-orange-500 bg-gray-50 rounded">
                  <div class="font-medium">完成了杭州西湖两日游</div>
                  <div class="text-sm text-gray-500 mt-1">2025年11月18日 18:45</div>
                </div>
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

const router = useRouter()

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
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
  
  .grid-cols-3 {
    grid-template-columns: 1fr;
  }
  
  .col-span-1,
  .col-span-2 {
    grid-column: span 1;
  }
}
</style>
