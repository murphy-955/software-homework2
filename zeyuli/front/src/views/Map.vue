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
        <div class="sidebar-item active" @click="navigateTo('/map')">
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
        <div class="card">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-2xl font-bold">地图视图</h2>
            <div class="flex gap-2">
              <button type="button" class="btn btn-secondary" @click="navigateTo('/result')">
                <span class="icon">📋</span>列表视图
              </button>
              <button type="button" class="btn btn-primary">
                <span class="icon">🧭</span>导航
              </button>
            </div>
          </div>
          
          <!-- 地图容器 -->
          <div class="relative h-96 bg-gradient-to-br from-blue-100 to-green-100 rounded-lg overflow-hidden">
            <!-- 地图模拟内容 -->
            <div class="absolute top-4 left-4 bg-white p-3 rounded-lg shadow-md">
              <h3 class="font-medium mb-2">行程点</h3>
              <div class="space-y-2 text-sm">
                <div class="flex items-center gap-2">
                  <div class="w-3 h-3 rounded-full bg-blue-500"></div>
                  <span>故宫博物院</span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-3 h-3 rounded-full bg-green-500"></div>
                  <span>四季民福烤鸭</span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-3 h-3 rounded-full bg-orange-500"></div>
                  <span>景山公园</span>
                </div>
              </div>
            </div>
            
            <!-- 地图信息 -->
            <div class="absolute bottom-4 right-4 bg-white p-3 rounded-lg shadow-md">
              <div class="text-sm">
                <div class="font-medium">总距离</div>
                <div class="text-blue-500">12.5km</div>
                <div class="font-medium mt-2">预计时间</div>
                <div class="text-blue-500">45分钟</div>
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
// todo 待完善
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
}
</style>
