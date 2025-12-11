<template>
  <div class="result-page">
    <!-- 内容区域 -->
    <div class="content-area">
      <div class="card mb-6">
        <!-- 行程基本信息 -->
        <div class="flex justify-between items-center mb-4">
          <div>
            <h2 class="text-2xl font-bold">{{ itinerary.planName }}</h2>
            <div class="text-gray-600">{{ itinerary.days }}天 · 预算: ¥{{ itinerary.totalBudget }}</div>
          </div>
          <div class="flex gap-2">
            <button type="button" class="btn btn-secondary">
              <span class="icon">📤</span>分享
            </button>
            <button type="button" class="btn btn-secondary" @click="navigateTo('/edit')">
              <span class="icon">✏️</span>编辑
            </button>
            <button type="button" class="btn btn-primary">
              <span class="icon">💾</span>保存
            </button>
          </div>
        </div>

        <!-- 天数选择器 -->
        <div class="border-b mb-4">
          <div class="flex gap-2">
            <button 
              v-for="day in itinerary.days" 
              :key="day"
              type="button" 
              :class="['py-3 px-6', currentDay === day ? 'border-b-2 border-blue-500 text-blue-500 font-medium' : 'text-gray-500 hover:text-blue-500']"
              @click="currentDay = day"
            >
              第{{ day }}天
            </button>
          </div>
        </div>

        <!-- 每日行程详情 -->
        <div class="space-y-4">
          <div 
            v-for="item in currentDayItinerary.attractions" 
            :key="item.name"
            class="flex gap-4"
          >
            <div class="w-16 text-gray-500 font-medium flex-shrink-0">09:00</div>
            <div class="flex-1">
              <div class="p-4 bg-gray-50 rounded-lg">
                <h3 class="font-medium">{{ item.name }}</h3>
                <p class="text-gray-600 mt-1">参观景点，感受当地文化</p>
                <div class="flex justify-between items-center mt-2">
                  <div class="text-orange-500 font-medium">¥{{ item.cost || 0 }}</div>
                  <button type="button" class="btn btn-text">查看详情</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 预算分解和行程概览 -->
      <div class="grid grid-cols-2 gap-6">
        <!-- 预算分解 -->
        <div class="card">
          <h3 class="font-bold text-lg mb-4">预算分解</h3>
          <div class="flex justify-center mb-6">
            <div class="w-64 h-64" ref="chartRef"></div>
          </div>
          <div class="space-y-3">
            <div class="flex justify-between items-center p-2 bg-gray-50 rounded">
              <span>门票</span>
              <span class="text-orange-500 font-medium">¥{{ itinerary.totalBudget * 0.2 }}</span>
            </div>
            <div class="flex justify-between items-center p-2 bg-gray-50 rounded">
              <span>餐饮</span>
              <span class="text-orange-500 font-medium">¥{{ itinerary.totalBudget * 0.4 }}</span>
            </div>
            <div class="flex justify-between items-center p-2 bg-gray-50 rounded">
              <span>住宿</span>
              <span class="text-orange-500 font-medium">¥{{ itinerary.totalBudget * 0.3 }}</span>
            </div>
            <div class="flex justify-between items-center p-2 bg-gray-50 rounded">
              <span>交通</span>
              <span class="text-orange-500 font-medium">¥{{ itinerary.totalBudget * 0.1 }}</span>
            </div>
          </div>
        </div>

        <!-- 行程概览 -->
        <div class="card">
          <h3 class="font-bold text-lg mb-4">行程概览</h3>
          <div class="space-y-4">
            <div class="flex justify-between items-center">
              <span class="text-gray-600">总天数</span>
              <span class="font-medium">{{ itinerary.days }}天</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-gray-600">总景点数</span>
              <span class="font-medium">{{ itinerary.dailyItineraries.reduce((sum, day) => sum + day.attractions.length, 0) }}个</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-gray-600">总餐饮数</span>
              <span class="font-medium">{{ itinerary.dailyItineraries.reduce((sum, day) => sum + (day.restaurants?.length || 0), 0) }}个</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-gray-600">总交通距离</span>
              <span class="font-medium">45.2km</span>
            </div>
            <div class="pt-4 border-t">
              <button type="button" class="btn btn-primary w-full" @click="navigateTo('/map')">
                <span class="icon">🗺️</span>查看地图
              </button>
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
import * as echarts from 'echarts'

const router = useRouter()

// 模拟行程数据
const itinerary = ref({
  planName: '北京文化三日游',
  city: '北京',
  days: 3,
  totalBudget: 1560,
  estimatedCost: 1500,
  accommodationSuggestion: '建议住在市中心，方便出行',
  dailyItineraries: [
    {
      day: 1,
      attractions: [
        { name: '故宫博物院', lat: 39.9042, lng: 116.4074, cost: 60 },
        { name: '景山公园', lat: 39.9163, lng: 116.3972, cost: 20 }
      ],
      restaurants: [
        { name: '四季民福烤鸭', cost: 120 }
      ],
      routes: [],
      dailyCost: 520,
      weather: '晴',
      suggestions: ['建议提前预约故宫门票']
    },
    {
      day: 2,
      attractions: [
        { name: '八达岭长城', lat: 40.3599, lng: 116.0208, cost: 40 },
        { name: '明十三陵', lat: 40.2277, lng: 116.1172, cost: 30 }
      ],
      restaurants: [
        { name: '长城脚下的公社', cost: 100 }
      ],
      routes: [],
      dailyCost: 490,
      weather: '多云',
      suggestions: ['长城上风大，建议穿厚衣服']
    },
    {
      day: 3,
      attractions: [
        { name: '颐和园', lat: 39.9997, lng: 116.2753, cost: 30 },
        { name: '圆明园', lat: 40.0025, lng: 116.3163, cost: 25 }
      ],
      restaurants: [
        { name: '全聚德烤鸭', cost: 150 }
      ],
      routes: [],
      dailyCost: 490,
      weather: '晴',
      suggestions: ['颐和园面积较大，建议租自行车游览']
    }
  ],
  additionalInfo: {},
  planType: 'cultural'
})

// 当前显示的天数
const currentDay = ref(1)

// 计算当前天的行程
const currentDayItinerary = computed(() => {
  return itinerary.value.dailyItineraries.find(day => day.day === currentDay.value) || itinerary.value.dailyItineraries[0]
})

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
}

// 图表引用
const chartRef = ref(null)
let chartInstance = null

// 初始化图表
const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value)
    const option = {
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 6 },
        data: [
          { value: itinerary.value.totalBudget * 0.2, name: '门票', itemStyle: { color: '#1890FF' } },
          { value: itinerary.value.totalBudget * 0.4, name: '餐饮', itemStyle: { color: '#52C41A' } },
          { value: itinerary.value.totalBudget * 0.3, name: '住宿', itemStyle: { color: '#FA8C16' } },
          { value: itinerary.value.totalBudget * 0.1, name: '交通', itemStyle: { color: '#722ED1' } }
        ]
      }]
    }
    chartInstance.setOption(option)
  }
}

// 监听窗口大小变化，调整图表
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 组件挂载时初始化图表
onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.result-page {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.content-area {
  max-width: 1200px;
  margin: 0 auto;
}

/* 图标样式 */
.icon {
  font-size: 18px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .grid-cols-2 {
    grid-template-columns: 1fr;
  }
}
</style>
