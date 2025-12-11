<template>
  <div class="profile-page">
    <div class="content-area">
      <!-- Header Section with Gradient Background -->
      <div class="profile-header relative overflow-hidden rounded-3xl shadow-xl mb-8">
        <div class="absolute inset-0 bg-gradient-to-r from-violet-600 to-indigo-600"></div>
        <div class="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-20"></div>
        <div class="absolute -right-20 -top-20 w-96 h-96 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob"></div>
        <div class="absolute -left-20 -bottom-20 w-96 h-96 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000"></div>
        
        <div class="relative z-10 p-8 md:p-12 flex flex-col md:flex-row items-center gap-8">
          <div class="relative group">
            <div class="w-32 h-32 rounded-full p-1 bg-white/30 backdrop-blur-sm">
              <div class="w-full h-full rounded-full bg-white flex items-center justify-center overflow-hidden shadow-inner">
                <span class="text-6xl">👨‍🎓</span>
              </div>
            </div>
            <div class="absolute bottom-2 right-2 bg-yellow-400 text-yellow-900 text-xs font-bold px-3 py-1 rounded-full border-2 border-white shadow-lg transform group-hover:scale-110 transition-transform">
              VIP
            </div>
          </div>
          
          <div class="text-center md:text-left flex-1 text-white">
            <h1 class="text-4xl font-bold mb-2 tracking-tight">张三</h1>
            <p class="text-blue-100 text-lg mb-6 font-light">探索世界，记录美好生活</p>
            <div class="flex flex-wrap justify-center md:justify-start gap-3">
              <span class="badge bg-white/20 backdrop-blur-md border border-white/10">✨ 资深旅行家</span>
              <span class="badge bg-white/20 backdrop-blur-md border border-white/10">📍 北京</span>
              <span class="badge bg-white/20 backdrop-blur-md border border-white/10">🏆 积分 850</span>
            </div>
          </div>

          <div class="flex gap-6 text-white/90">
            <div class="text-center px-6 py-3 rounded-2xl bg-white/10 backdrop-blur-md border border-white/10 hover:bg-white/20 transition-colors cursor-pointer">
              <div class="text-3xl font-bold">12</div>
              <div class="text-xs uppercase tracking-wider opacity-80">行程</div>
            </div>
            <div class="text-center px-6 py-3 rounded-2xl bg-white/10 backdrop-blur-md border border-white/10 hover:bg-white/20 transition-colors cursor-pointer">
              <div class="text-3xl font-bold">35</div>
              <div class="text-xs uppercase tracking-wider opacity-80">足迹</div>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <!-- Left Sidebar Navigation -->
        <div class="lg:col-span-4 space-y-6">
          <!-- Wallet Card -->
          <div class="card bg-gradient-to-br from-gray-900 to-gray-800 text-white border-none shadow-lg transform hover:-translate-y-1 transition-transform duration-300">
            <div class="p-6">
              <div class="flex justify-between items-start mb-8">
                <div>
                  <p class="text-gray-400 text-sm font-medium mb-1">账户余额</p>
                  <h3 class="text-3xl font-bold tracking-tight">¥128.00</h3>
                </div>
                <div class="w-10 h-10 rounded-full bg-white/10 flex items-center justify-center">
                  <span class="text-xl">💳</span>
                </div>
              </div>
              <div class="flex gap-3">
                <button class="flex-1 py-2 bg-blue-600 hover:bg-blue-500 rounded-lg text-sm font-medium transition-colors">充值</button>
                <button class="flex-1 py-2 bg-white/10 hover:bg-white/20 rounded-lg text-sm font-medium transition-colors">提现</button>
              </div>
            </div>
          </div>

          <!-- Menu -->
          <div class="card overflow-hidden border-0 shadow-lg">
            <div class="p-2">
              <button @click="navigateTo('/favorites')" class="menu-item group">
                <span class="icon-box bg-rose-50 text-rose-500 group-hover:bg-rose-500 group-hover:text-white">❤️</span>
                <span class="flex-1 font-medium text-gray-700 group-hover:text-gray-900">我的收藏</span>
                <span class="text-gray-300 group-hover:translate-x-1 transition-transform">›</span>
              </button>
              <button @click="navigateTo('/history')" class="menu-item group">
                <span class="icon-box bg-amber-50 text-amber-500 group-hover:bg-amber-500 group-hover:text-white">📜</span>
                <span class="flex-1 font-medium text-gray-700 group-hover:text-gray-900">旅行历史</span>
                <span class="text-gray-300 group-hover:translate-x-1 transition-transform">›</span>
              </button>
              <button @click="navigateTo('/preferences')" class="menu-item group">
                <span class="icon-box bg-violet-50 text-violet-500 group-hover:bg-violet-500 group-hover:text-white">⚙️</span>
                <span class="flex-1 font-medium text-gray-700 group-hover:text-gray-900">偏好设置</span>
                <span class="text-gray-300 group-hover:translate-x-1 transition-transform">›</span>
              </button>
              <button @click="navigateTo('/help')" class="menu-item group">
                <span class="icon-box bg-emerald-50 text-emerald-500 group-hover:bg-emerald-500 group-hover:text-white">❓</span>
                <span class="flex-1 font-medium text-gray-700 group-hover:text-gray-900">帮助与反馈</span>
                <span class="text-gray-300 group-hover:translate-x-1 transition-transform">›</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Right Content -->
        <div class="lg:col-span-8 space-y-6">
          <!-- Stats Grid -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="stat-card group">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-gray-500 text-sm font-medium">本月预算</p>
                  <h4 class="text-2xl font-bold text-gray-800 mt-1">¥3,000</h4>
                </div>
                <span class="p-2 bg-blue-50 text-blue-600 rounded-lg group-hover:bg-blue-600 group-hover:text-white transition-colors">💰</span>
              </div>
              <div class="mt-4 w-full bg-gray-100 rounded-full h-1.5">
                <div class="bg-blue-500 h-1.5 rounded-full" style="width: 45%"></div>
              </div>
              <p class="text-xs text-gray-400 mt-2">已使用 45%</p>
            </div>

            <div class="stat-card group">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-gray-500 text-sm font-medium">节省金额</p>
                  <h4 class="text-2xl font-bold text-gray-800 mt-1">¥2,400</h4>
                </div>
                <span class="p-2 bg-green-50 text-green-600 rounded-lg group-hover:bg-green-600 group-hover:text-white transition-colors">📉</span>
              </div>
              <p class="text-xs text-green-600 mt-4 flex items-center">
                <span class="mr-1">↑</span> 比上月多省 12%
              </p>
            </div>

            <div class="stat-card group">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-gray-500 text-sm font-medium">即将出行</p>
                  <h4 class="text-2xl font-bold text-gray-800 mt-1">3 天后</h4>
                </div>
                <span class="p-2 bg-purple-50 text-purple-600 rounded-lg group-hover:bg-purple-600 group-hover:text-white transition-colors">✈️</span>
              </div>
              <p class="text-xs text-purple-600 mt-4">北京 · 文化之旅</p>
            </div>
          </div>

          <!-- Recent Activity -->
          <div class="card border-0 shadow-lg">
            <div class="p-6 border-b border-gray-100 flex justify-between items-center">
              <h3 class="font-bold text-lg text-gray-800 flex items-center gap-2">
                <span class="w-1.5 h-6 bg-indigo-500 rounded-full"></span>
                最近动态
              </h3>
              <button class="text-sm text-indigo-600 hover:text-indigo-700 font-medium">查看全部</button>
            </div>
            <div class="p-2">
              <div class="activity-item hover:bg-gray-50 p-4 rounded-xl transition-colors cursor-pointer flex gap-4">
                <div class="w-12 h-12 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-xl shrink-0">
                  🗺️
                </div>
                <div class="flex-1">
                  <div class="flex justify-between items-start">
                    <h4 class="font-bold text-gray-800">生成了北京文化三日游</h4>
                    <span class="text-xs text-gray-400">14:30</span>
                  </div>
                  <p class="text-sm text-gray-600 mt-1 line-clamp-1">包含故宫、天坛、颐和园等 8 个热门景点，深度体验京城文化。</p>
                  <div class="flex gap-2 mt-2">
                    <span class="tag">文化之旅</span>
                    <span class="tag">¥1,560</span>
                  </div>
                </div>
              </div>

              <div class="activity-item hover:bg-gray-50 p-4 rounded-xl transition-colors cursor-pointer flex gap-4">
                <div class="w-12 h-12 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center text-xl shrink-0">
                  ❤️
                </div>
                <div class="flex-1">
                  <div class="flex justify-between items-start">
                    <h4 class="font-bold text-gray-800">收藏了故宫博物院</h4>
                    <span class="text-xs text-gray-400">10:15</span>
                  </div>
                  <p class="text-sm text-gray-600 mt-1">已添加到"必去清单"</p>
                </div>
              </div>

              <div class="activity-item hover:bg-gray-50 p-4 rounded-xl transition-colors cursor-pointer flex gap-4">
                <div class="w-12 h-12 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center text-xl shrink-0">
                  ✅
                </div>
                <div class="flex-1">
                  <div class="flex justify-between items-start">
                    <h4 class="font-bold text-gray-800">完成了杭州西湖两日游</h4>
                    <span class="text-xs text-gray-400">昨天</span>
                  </div>
                  <p class="text-sm text-gray-600 mt-1">行程圆满结束，发表了 3 条评价</p>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.profile-page {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
  background-color: #f8fafc;
}

.content-area {
  max-width: 1200px;
  margin: 0 auto;
}

.badge {
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.menu-item {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  transition: all 0.2s ease;
  text-align: left;
}

.menu-item:hover {
  background-color: #f8fafc;
}

.icon-box {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 1.25rem;
  transition: all 0.3s ease;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.tag {
  padding: 2px 8px;
  background-color: #f1f5f9;
  color: #64748b;
  border-radius: 4px;
  font-size: 0.75rem;
}

/* Animation */
@keyframes blob {
  0% { transform: translate(0px, 0px) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0px, 0px) scale(1); }
}

.animate-blob {
  animation: blob 7s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}
</style>
