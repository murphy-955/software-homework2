import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080', // 假设后端API地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器，添加token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    // 处理401错误（未授权）
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// API接口
export const authAPI = {
  // 用户登录
  login: (data) => api.post('/user/login', data),
  // 用户注册
  register: (data) => api.post('/user/register', data),
  // 获取用户信息
  getUserInfo: () => api.get('/api/user/info')
}

export const travelAPI = {
  // 生成旅行计划
  generatePlan: (data) => api.post('/api/travel/generate', data),
  // 继续对话
  continueConversation: (data) => api.post('/api/travel/conversation', data)
}

export const mapAPI = {
  // 获取地图中心点
  getCenter: (location) => api.get('/map/center', { params: { location } }),
  // 搜索位置
  searchLocations: (params) => api.get('/map/search-locations', { params }),
  // 获取路线
  getRoute: (params) => api.get('/map/route', { params }),
  // 获取周边POI
  getSurroundingPOIs: (params) => api.get('/map/surrounding-pois', { params }),
  // 获取地图截图
  getScreenshot: (params) => api.get('/map/screenshot', { params }),
  // 获取天气信息
  getWeather: (cityCode) => api.get('/map/weather', { params: { cityCode } }),
  // 根据坐标获取地址
  getAddressByLocation: (lat, lng) => api.get('/map/address-by-location', { params: { lat, lng } }),
  // 获取两点距离
  getDistance: (params) => api.get('/map/distance', { params }),
  // 获取景点详情
  getPOIDetails: (poiId) => api.get(`/map/poi-details/${poiId}`),
  // 获取城市景点
  getAttractionsByCity: (params) => api.get('/map/attractions-by-city', { params })
}

export const studentAPI = {
  // 获取学生专属线路
  getStudentRoutes: (params) => api.get('/api/student/routes', { params }),
  // 获取推荐的学生专属线路
  getRecommendedRoutes: (params) => api.get('/student-route/recommended', { params })
}

// 行程规划API
export const itineraryAPI = {
  // 按预算规划行程
  planByBudget: (params) => api.post('/itinerary/plan-by-budget', {}, { params }),
  // 根据条件调整行程
  adjustByCondition: (params, data) => api.post('/itinerary/adjust-by-condition', data, { params }),
  // 生成人格化行程
  generatePersonality: (params) => api.get('/itinerary/generate-personality', { params }),
  // 获取学生专属行程
  getStudentItinerary: (params) => api.get('/itinerary/student-itinerary', { params }),
  // 生成搭子行程
  generateCompanion: (params) => api.get('/itinerary/generate-companion', { params }),
  // 计算行程费用
  calculateCost: (params, data) => api.post('/itinerary/calculate-cost', data, { params }),
  // 优化景点选择
  optimizeAttractions: (params) => api.get('/itinerary/optimize-attractions', { params }),
  // 优化交通方式
  optimizeTransportation: (params) => api.get('/itinerary/optimize-transportation', { params })
}

export default api