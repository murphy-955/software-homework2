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


          <!-- 旅行基本信息输入 -->
          <div class="space-y-4 mb-6">
            <!-- 起始城市选择 -->
            <div class="flex gap-4">
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">起始省份</label>
                <select
                    class="w-full px-4 py-2 border rounded-lg"
                    v-model="selectedStartProvince"
                    :disabled="isLoading || isLoadingCities"
                    @change="startCity = ''"
                >
                  <option value="">请选择起始省份</option>
                  <option v-for="province in provinceList" :key="province" :value="province">{{ province }}</option>
                </select>
              </div>
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">起始城市</label>
                <select
                    class="w-full px-4 py-2 border rounded-lg"
                    v-model="startCity"
                    :disabled="isLoading || isLoadingCities || !selectedStartProvince"
                >
                  <option value="">请选择起始城市</option>
                  <option v-for="city in getCitiesByProvince(selectedStartProvince)" :key="city" :value="city">{{
                      city
                    }}
                  </option>
                </select>
              </div>
            </div>

            <!-- 结束城市选择 -->
            <div class="flex gap-4">
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">结束省份</label>
                <select
                    class="w-full px-4 py-2 border rounded-lg"
                    v-model="selectedEndProvince"
                    :disabled="isLoading || isLoadingCities"
                    @change="endCity = ''"
                >
                  <option value="">请选择结束省份</option>
                  <option v-for="province in provinceList" :key="province" :value="province">{{ province }}</option>
                </select>
              </div>
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">结束城市</label>
                <select
                    class="w-full px-4 py-2 border rounded-lg"
                    v-model="endCity"
                    :disabled="isLoading || isLoadingCities || !selectedEndProvince"
                >
                  <option value="">请选择结束城市</option>
                  <option v-for="city in getCitiesByProvince(selectedEndProvince)" :key="city" :value="city">{{
                      city
                    }}
                  </option>
                </select>
              </div>
            </div>
            <!-- 日期选择 -->
            <div class="flex gap-4">
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">起始日期</label>
                <input
                    type="date"
                    class="w-full px-4 py-2 border rounded-lg"
                    v-model="startDate"
                    :disabled="isLoading"
                />
              </div>
              <div class="flex-1">
                <label class="block text-sm font-medium text-gray-700 mb-1">结束日期</label>
                <input
                    type="date"
                    class="w-full px-4 py-2 border rounded-lg"
                    v-model="endDate"
                    :disabled="isLoading"
                />
              </div>
            </div>
          </div>

          <!-- 旅行需求输入 -->
          <div class="mb-8 flex justify-center">
            <textarea
                class="w-full h-56 px-8 py-6 bg-gray-50 rounded-2xl text-gray-700 resize-none outline-none shadow-sm focus:shadow-md transition-shadow"
                placeholder="例如：'上海三日游，2000元预算，喜欢美食和艺术'"
                style="font-size: 16px; line-height: 1.8;width: 500px;height: 120px;margin: 16px"
                v-model="travelRequest"
                :disabled="isLoading"
            ></textarea>
          </div>

          <div class="flex gap-4 flex-wrap mb-8">
            <button type="button"
                    class="px-5 py-2 bg-blue-100 text-blue-700 rounded-full text-sm font-medium hover:bg-blue-200 transition-colors"
                    @click="selectTemplate('北京三日游 1500元')" :disabled="isLoading">北京三日游 1500元
            </button>
            <button type="button"
                    class="px-5 py-2 bg-blue-100 text-blue-700 rounded-full text-sm font-medium hover:bg-blue-200 transition-colors"
                    @click="selectTemplate('杭州两日游 美食之旅')" :disabled="isLoading">杭州两日游 美食之旅
            </button>
            <button type="button"
                    class="px-5 py-2 bg-blue-100 text-blue-700 rounded-full text-sm font-medium hover:bg-blue-200 transition-colors"
                    @click="selectTemplate('成都四日游 文化体验')" :disabled="isLoading">成都四日游 文化体验
            </button>
            <button type="button"
                    class="px-5 py-2 bg-blue-100 text-blue-700 rounded-full text-sm font-medium hover:bg-blue-200 transition-colors"
                    @click="selectTemplate('西安三日游 历史文化')" :disabled="isLoading">西安三日游 历史文化
            </button>
            <button type="button"
                    class="px-5 py-2 bg-blue-100 text-blue-700 rounded-full text-sm font-medium hover:bg-blue-200 transition-colors"
                    @click="selectTemplate('厦门两日游 海滨风光')" :disabled="isLoading">厦门两日游 海滨风光
            </button>
          </div>

          <div class="flex justify-center">
            <button type="button"
                    class="w-full max-w-xs py-3 bg-blue-600 text-white rounded-xl font-medium text-lg hover:bg-blue-700 transition-colors shadow-md hover:shadow-lg"
                    @click="startPlanning" :disabled="isLoading">
              <span v-if="isLoading">生成中...</span>
              <span v-else>开始规划</span>
            </button>
          </div>

          <!-- 流式数据显示区域 -->
          <div v-if="isLoading" class="mt-6 p-4 bg-gray-50 rounded-xl">
            <h3 class="font-medium mb-2">生成中...</h3>
            <div class="text-sm text-gray-600 whitespace-pre-wrap">{{ streamingData }}</div>
            <div class="mt-2 flex items-center text-sm text-gray-500">
              <span class="animate-pulse mr-2">● ● ●</span>
              <span>正在为您生成专属行程...</span>
            </div>
          </div>
          <div v-if="!isLoading && streamingData" class="mt-6 p-4 bg-gray-50 rounded-xl">
            <h3 class="font-medium mb-2">生成结果</h3>
            <div class="text-sm text-gray-600 whitespace-pre-wrap">{{ streamingData }}</div>
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
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {planItinerary} from '../api/itinerary'
// todo 待完善
const router = useRouter()

// 旅行基本信息
const startCity = ref('')
const endCity = ref('')
const startDate = ref('')
const endDate = ref('')

// 省份和城市数据
const provinceList = ref([])
const cityData = ref({})
const isLoadingCities = ref(false)

// 选中的省份
const selectedStartProvince = ref('')
const selectedEndProvince = ref('')

// 旅行需求输入
const travelRequest = ref('')
// 加载状态
const isLoading = ref(false)
// 流式接收的数据
const streamingData = ref('')

// 获取省份和城市列表
const fetchCityList = async () => {
  try {
    isLoadingCities.value = true
    const response = await fetch('http://localhost:8080/city.json')
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    const data = await response.json()

    // 解析省份和城市数据
    const provinces = []
    const cities = {}

    // 处理直辖市
    if (data.直辖市 && Array.isArray(data.直辖市)) {
      provinces.push(...data.直辖市)
      data.直辖市.forEach(city => {
        cities[city] = [city]
      })
    }

    // 处理省份
    if (data.省 && typeof data.省 === 'object') {
      Object.keys(data.省).forEach(province => {
        provinces.push(province)
        cities[province] = data.省[province]
      })
    }

    // 处理自治区
    if (data.自治区 && typeof data.自治区 === 'object') {
      Object.keys(data.自治区).forEach(autonomousRegion => {
        provinces.push(autonomousRegion)
        cities[autonomousRegion] = data.自治区[autonomousRegion]
      })
    }

    // 处理特别行政区
    if (data.特别行政区 && Array.isArray(data.特别行政区)) {
      provinces.push(...data.特别行政区)
      data.特别行政区.forEach(city => {
        cities[city] = [city]
      })
    }

    provinceList.value = provinces
    cityData.value = cities
  } catch (error) {
    console.error('获取城市列表失败:', error)
    // 如果获取失败，使用默认数据
    provinceList.value = ['北京市', '上海市', '广州市', '深圳市', '杭州市', '成都市', '西安市', '厦门市', '南京市', '武汉市']
    cityData.value = {
      '北京市': ['北京市'],
      '上海市': ['上海市'],
      '广州市': ['广州市'],
      '深圳市': ['深圳市'],
      '杭州市': ['杭州市'],
      '成都市': ['成都市'],
      '西安市': ['西安市'],
      '厦门市': ['厦门市'],
      '南京市': ['南京市'],
      '武汉市': ['武汉市']
    }
  } finally {
    isLoadingCities.value = false
  }
}

// 组件加载时获取城市列表
fetchCityList()

// 获取当前选中省份的城市列表
const getCitiesByProvince = (province) => {
  return cityData.value[province] || []
}

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
    // 表单验证
    if (!startCity.value.trim()) {
      alert('请输入起始城市')
      return
    }
    if (!endCity.value.trim()) {
      alert('请输入结束城市')
      return
    }
    if (!startDate.value) {
      alert('请选择起始日期')
      return
    }
    if (!endDate.value) {
      alert('请选择结束日期')
      return
    }
    if (new Date(startDate.value) > new Date(endDate.value)) {
      alert('起始日期不能晚于结束日期')
      return
    }

    isLoading.value = true
    streamingData.value = ''

    // 使用真实的用户输入数据
    const params = {
      startCity: startCity.value.trim(),
      endCity: endCity.value.trim(),
      startDate: startDate.value,
      endDate: endDate.value,
      userInput: travelRequest.value.trim()
      // token不再作为URL参数传递，而是通过请求头传递
    }

    // 使用流式API生成行程，传入回调函数实时更新数据
    const result = await planItinerary(params, (chunk) => {
      // 实时更新流式数据
      streamingData.value += chunk
    })

    // 保存行程结果到本地存储
    localStorage.setItem('currentItinerary', result)

    // 跳转到结果页面
    router.push('/result')
  } catch (error) {
    console.error('规划行程失败:', error)
    streamingData.value = '规划行程失败，请重试'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f8fafc;
}

/* 卡片样式 */
.card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 32px;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.card.mb-6 {
  margin-bottom: 24px;
}

/* 输入框样式 */
input[type="text"],
input[type="date"],
select,
textarea {
  transition: all 0.2s ease;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background-color: white;
}

input[type="text"]:focus,
input[type="date"]:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 按钮样式 */
.btn-primary {
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 24px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-primary:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background-color: #f1f5f9;
  color: #334155;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #e2e8f0;
  transform: translateY(-1px);
}

.btn-secondary:disabled {
  background-color: #f8fafc;
  cursor: not-allowed;
  transform: none;
}

/* 图标样式 */
.icon {
  font-size: 18px;
}

/* 流式数据显示区域 */
.animate-pulse {
  animation: pulse 1.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
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

  .card {
    padding: 20px;
  }
}
</style>
