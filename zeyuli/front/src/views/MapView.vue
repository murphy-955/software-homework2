<template>
  <div class="map-container">
    <nav class="navbar">
      <div class="navbar-brand">旅行助手</div>
      <div class="navbar-menu">
        <a href="/" class="nav-item">首页</a>
        <a href="/map" class="nav-item active">地图服务</a>
        <a href="/student" class="nav-item">学生线路</a>
        <a href="/itinerary" class="nav-item">行程规划</a>
      </div>
      <div class="navbar-user">
        <span class="username">{{ username }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </nav>

    <div class="map-content">
      <div class="map-sidebar">
        <div class="search-section">
          <h3>搜索地点</h3>
          <div class="search-input-wrapper">
            <input 
              type="text" 
              v-model="searchQuery"
              placeholder="输入地点名称或关键词"
              class="search-input"
              @input="handleSearch"
            />
            <button class="search-btn" @click="searchLocations">搜索</button>
          </div>
          <div v-if="searchResults.length > 0" class="search-results">
            <div 
              v-for="(result, index) in searchResults" 
              :key="index"
              class="search-result-item"
              @click="selectLocation(result)"
            >
              <div class="result-name">{{ result.name }}</div>
              <div class="result-address">{{ result.address }}</div>
            </div>
          </div>
        </div>

        <div class="route-section">
          <h3>路线规划</h3>
          <div class="route-inputs">
            <div class="input-group">
              <label>起点：</label>
              <select v-model="routeForm.start" class="location-select">
                <option value="">选择起点</option>
                <option v-for="(loc, index) in selectedLocations" :key="index" :value="loc.name">
                  {{ loc.name }}
                </option>
              </select>
            </div>
            <div class="input-group">
              <label>终点：</label>
              <select v-model="routeForm.end" class="location-select">
                <option value="">选择终点</option>
                <option v-for="(loc, index) in selectedLocations" :key="index" :value="loc.name">
                  {{ loc.name }}
                </option>
              </select>
            </div>
            <div class="input-group">
              <label>出行方式：</label>
              <select v-model="routeForm.transportType" class="transport-select">
                <option value="driving">驾车</option>
                <option value="walking">步行</option>
                <option value="transit">公共交通</option>
                <option value="bicycling">骑行</option>
              </select>
            </div>
            <button class="route-btn" @click="getRoute">规划路线</button>
          </div>
        </div>

        <div class="poi-section">
          <h3>周边POI</h3>
          <div class="poi-categories">
            <button 
              v-for="category in poiCategories" 
              :key="category.value"
              class="category-btn"
              :class="{ active: selectedCategory === category.value }"
              @click="selectCategory(category.value)"
            >
              {{ category.name }}
            </button>
          </div>
          <div v-if="poiResults.length > 0" class="poi-results">
            <div 
              v-for="(poi, index) in poiResults" 
              :key="index"
              class="poi-item"
              @click="viewPOIDetail(poi)"
            >
              <div class="poi-name">{{ poi.name }}</div>
              <div class="poi-info">
                <span class="poi-distance">{{ poi.distance }}</span>
                <span class="poi-rating" v-if="poi.rating">⭐ {{ poi.rating }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="selectedLocation" class="location-detail">
          <h3>地点详情</h3>
          <div class="detail-header">
            <h4>{{ selectedLocation.name }}</h4>
            <button class="close-detail" @click="selectedLocation = null">×</button>
          </div>
          <div class="detail-content">
            <div class="detail-item">
              <strong>地址：</strong>{{ selectedLocation.address || '未知' }}
            </div>
            <div class="detail-item" v-if="selectedLocation.description">
              <strong>描述：</strong>{{ selectedLocation.description }}
            </div>
            <div class="detail-item" v-if="selectedLocation.phone">
              <strong>电话：</strong>{{ selectedLocation.phone }}
            </div>
            <div class="detail-item" v-if="selectedLocation.openingHours">
              <strong>营业时间：</strong>{{ selectedLocation.openingHours }}
            </div>
            <div class="detail-item" v-if="selectedLocation.rating">
              <strong>评分：</strong>{{ selectedLocation.rating }}
            </div>
            <div class="detail-actions">
              <button class="add-btn" @click="addLocation(selectedLocation)">添加到线路</button>
              <button class="navigate-btn" @click="navigateToLocation(selectedLocation)">导航到这里</button>
            </div>
          </div>
        </div>
      </div>

      <div class="map-main">
        <div class="map-wrapper">
          <div class="map-placeholder">
            <div class="map-icon">🗺️</div>
            <p>地图将在这里显示</p>
            <p class="map-hint">点击搜索结果以查看地点，规划路线以显示路径</p>
          </div>
        </div>
        
        <div v-if="currentRoute" class="route-info">
          <div class="route-summary">
            <div class="route-stats">
              <div class="stat-item">
                <span class="stat-label">距离</span>
                <span class="stat-value">{{ currentRoute.distance }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">时间</span>
                <span class="stat-value">{{ currentRoute.duration }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">费用</span>
                <span class="stat-value">{{ currentRoute.estimatedCost || '暂无' }}</span>
              </div>
            </div>
            <button class="clear-route" @click="clearRoute">清除路线</button>
          </div>
          <div class="route-steps">
            <h4>路线指引</h4>
            <ol>
              <li v-for="(step, index) in currentRoute.steps" :key="index">
                {{ step.instruction }}
              </li>
            </ol>
          </div>
        </div>
      </div>
    </div>

    <!-- POI详情模态框 -->
    <div v-if="showPOIDetail" class="modal-overlay" @click.self="closePOIDetail">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ poiDetail.name }}</h3>
          <button class="close-btn" @click="closePOIDetail">&times;</button>
        </div>
        <div class="modal-body">
          <div v-if="poiDetail.images && poiDetail.images.length > 0" class="poi-images">
            <img v-for="(img, index) in poiDetail.images" :key="index" :src="img" :alt="poiDetail.name" class="poi-image">
          </div>
          <div class="poi-info-full">
            <div class="info-row">
              <strong>地址：</strong>{{ poiDetail.address || '未知' }}
            </div>
            <div class="info-row" v-if="poiDetail.description">
              <strong>描述：</strong>{{ poiDetail.description }}
            </div>
            <div class="info-row" v-if="poiDetail.phone">
              <strong>电话：</strong>{{ poiDetail.phone }}
            </div>
            <div class="info-row" v-if="poiDetail.openingHours">
              <strong>营业时间：</strong>{{ poiDetail.openingHours }}
            </div>
            <div class="info-row" v-if="poiDetail.rating">
              <strong>评分：</strong>{{ poiDetail.rating }}
            </div>
            <div class="info-row" v-if="poiDetail.priceRange">
              <strong>价格：</strong>{{ poiDetail.priceRange }}
            </div>
            <div class="info-row" v-if="poiDetail.tags && poiDetail.tags.length > 0">
              <strong>标签：</strong>
              <span v-for="(tag, index) in poiDetail.tags" :key="index" class="tag">
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
  </div>
</template>

<script>
import { getUserInfo, logout } from '../utils/auth.js'
import { mapAPI } from '../utils/api.js'

export default {
  name: 'MapView',
  data() {
    return {
      username: localStorage.getItem('username') || '用户',
      searchQuery: '',
      searchResults: [],
      selectedLocation: null,
      selectedLocations: [],
      routeForm: {
        start: '',
        end: '',
        transportType: 'driving'
      },
      currentRoute: null,
      poiCategories: [
        { name: '餐厅', value: 'restaurant' },
        { name: '酒店', value: 'hotel' },
        { name: '景点', value: 'attraction' },
        { name: '购物', value: 'shopping' },
        { name: '娱乐', value: 'entertainment' },
        { name: '银行', value: 'bank' },
        { name: '医院', value: 'hospital' },
        { name: '加油站', value: 'gas_station' }
      ],
      selectedCategory: 'restaurant',
      poiResults: [],
      showPOIDetail: false,
      poiDetail: {},
      loading: false,
      error: null
    }
  },
  mounted() {
    this.checkLoginStatus()
    this.initMap()
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const userInfo = getUserInfo()
      if (userInfo) {
        this.username = userInfo.username || '用户'
      }
    },
    
    // 初始化地图
    initMap() {
      // 在实际环境中，这里会初始化地图API
      console.log('初始化地图')
    },
    
    // 搜索地点
    async handleSearch() {
      if (this.searchQuery.length < 2) {
        this.searchResults = []
        return
      }
      
      try {
        const response = await mapAPI.searchLocations({
          keyword: this.searchQuery
        })
        this.searchResults = response
      } catch (err) {
        console.error('搜索地点失败:', err)
        this.useMockSearchResults()
      }
    },
    
    // 搜索按钮点击事件
    searchLocations() {
      if (this.searchQuery.trim()) {
        this.handleSearch()
      }
    },
    
    // 选择地点
    async selectLocation(location) {
      this.selectedLocation = location
      
      try {
        // 获取地点详情
        const detail = await mapAPI.getLocationDetail({ locationId: location.id })
        this.selectedLocation = { ...location, ...detail }
      } catch (err) {
        console.error('获取地点详情失败:', err)
        this.useMockLocationDetail()
      }
    },
    
    // 添加地点到已选列表
    addLocation(location) {
      const exists = this.selectedLocations.some(loc => loc.id === location.id)
      if (!exists) {
        this.selectedLocations.push(location)
      }
    },
    
    // 导航到指定地点
    navigateToLocation(location) {
      // 在实际环境中，这里会触发地图导航
      console.log('导航到:', location.name)
    },
    
    // 获取路线规划
    async getRoute() {
      if (!this.routeForm.start || !this.routeForm.end) {
        this.error = '请选择起点和终点'
        return
      }
      
      this.loading = true
      this.error = null
      
      try {
        const response = await mapAPI.getRoute({
          origin: this.routeForm.start,
          destination: this.routeForm.end,
          mode: this.routeForm.transportType
        })
        this.currentRoute = response
      } catch (err) {
        console.error('获取路线失败:', err)
        this.error = '路线规划失败，请稍后重试'
        this.useMockRoute()
      } finally {
        this.loading = false
      }
    },
    
    // 清除路线
    clearRoute() {
      this.currentRoute = null
    },
    
    // 选择POI分类
    async selectCategory(category) {
      this.selectedCategory = category
      
      if (this.selectedLocation) {
        try {
          const response = await mapAPI.searchNearbyPOI({
            lat: this.selectedLocation.lat,
            lng: this.selectedLocation.lng,
            category: category,
            radius: 1000
          })
          this.poiResults = response
        } catch (err) {
          console.error('搜索周边POI失败:', err)
          this.useMockPOIResults()
        }
      }
    },
    
    // 查看POI详情
    viewPOIDetail(poi) {
      this.poiDetail = poi
      this.showPOIDetail = true
    },
    
    // 关闭POI详情
    closePOIDetail() {
      this.showPOIDetail = false
      this.poiDetail = {}
    },
    
    // 处理退出登录
    handleLogout() {
      logout()
      this.$router.push('/login')
    },
    
    // 模拟搜索结果数据
    useMockSearchResults() {
      this.searchResults = [
        {
          id: 1,
          name: '西湖',
          address: '浙江省杭州市西湖区龙井路1号',
          lat: 30.2429,
          lng: 120.1484,
          type: 'attraction'
        },
        {
          id: 2,
          name: '杭州东站',
          address: '浙江省杭州市上城区站东路',
          lat: 30.2857,
          lng: 120.2187,
          type: 'transport'
        },
        {
          id: 3,
          name: '杭州西湖区图书馆',
          address: '浙江省杭州市西湖区文三西路488号',
          lat: 30.2517,
          lng: 120.1234,
          type: 'building'
        }
      ]
    },
    
    // 模拟地点详情数据
    useMockLocationDetail() {
      this.selectedLocation = {
        ...this.selectedLocation,
        description: '中国浙江省杭州市的一个淡水湖，位于浙江省杭州市西湖区龙井路1号，杭州市区西部，景区总面积49平方千米，汇水面积为21.22平方千米，湖面面积为6.38平方千米。',
        phone: '0571-87179617',
        openingHours: '全天开放',
        rating: 4.9,
        ticketPrice: '免费'
      }
    },
    
    // 模拟路线数据
    useMockRoute() {
      this.currentRoute = {
        distance: '约15.2公里',
        duration: '约35分钟',
        estimatedCost: '约40元',
        steps: [
          { instruction: '从起点出发，沿西湖大道向东行驶约200米' },
          { instruction: '右转进入南山路，沿南山路向南行驶约3.5公里' },
          { instruction: '左转进入中河高架路，沿高架路向东行驶约8.5公里' },
          { instruction: '从杭州东站出口离开高架路' },
          { instruction: '沿站东路行驶约500米，到达终点' }
        ]
      }
    },
    
    // 模拟POI数据
    useMockPOIResults() {
      const categoryMap = {
        restaurant: [
          { id: 101, name: '楼外楼', distance: '500米', rating: 4.7, address: '杭州市西湖区孤山路30号' },
          { id: 102, name: '外婆家', distance: '800米', rating: 4.5, address: '杭州市西湖区湖滨银泰in77' },
          { id: 103, name: '绿茶餐厅', distance: '1.2公里', rating: 4.3, address: '杭州市西湖区西溪印象城' }
        ],
        hotel: [
          { id: 201, name: '杭州西湖国宾馆', distance: '1.5公里', rating: 4.9, address: '杭州市西湖区杨公堤18号' },
          { id: 202, name: '杭州凯悦酒店', distance: '2.1公里', rating: 4.7, address: '杭州市上城区湖滨路28号' },
          { id: 203, name: '杭州西溪悦榕庄', distance: '3.2公里', rating: 4.8, address: '杭州市西湖区紫金港路西溪天堂' }
        ],
        attraction: [
          { id: 301, name: '断桥残雪', distance: '800米', rating: 4.8, address: '杭州市西湖区白堤路' },
          { id: 302, name: '雷峰塔', distance: '1.2公里', rating: 4.6, address: '杭州市西湖区南山路15号' },
          { id: 303, name: '灵隐寺', distance: '4.5公里', rating: 4.9, address: '杭州市西湖区灵隐路法云弄1号' }
        ]
      }
      
      this.poiResults = categoryMap[this.selectedCategory] || categoryMap.restaurant
    }
  }
}
</script>

<style scoped>
.map-container {
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

.map-content {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
}

.map-sidebar {
  width: 350px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.map-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.map-wrapper {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  min-height: 500px;
  position: relative;
}

.map-placeholder {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
  text-align: center;
}

.map-icon {
  font-size: 5rem;
  margin-bottom: 20px;
}

.map-hint {
  font-size: 0.9rem;
  margin-top: 10px;
}

.search-section, .route-section, .poi-section {
  margin-bottom: 30px;
}

.search-section h3, .route-section h3, .poi-section h3 {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.2rem;
  border-bottom: 2px solid #2575fc;
  padding-bottom: 8px;
}

.search-input-wrapper {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
}

.search-input:focus {
  border-color: #2575fc;
  outline: none;
}

.search-btn {
  padding: 10px 20px;
  background: #2575fc;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  background: #1a68e3;
}

.search-results, .poi-results {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #eee;
  border-radius: 8px;
}

.search-result-item, .poi-item {
  padding: 12px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-result-item:hover, .poi-item:hover {
  background-color: #f8f9fa;
}

.search-result-item:last-child, .poi-item:last-child {
  border-bottom: none;
}

.result-name, .poi-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.result-address {
  font-size: 0.9rem;
  color: #666;
}

.poi-info {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
  color: #666;
}

.route-inputs {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.input-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.location-select, .transport-select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
}

.route-btn {
  padding: 12px;
  background: linear-gradient(135deg, #2575fc, #6a11cb);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.route-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(37, 117, 252, 0.4);
}

.poi-categories {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-bottom: 15px;
}

.category-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
}

.category-btn:hover, .category-btn.active {
  background: #2575fc;
  color: white;
  border-color: #2575fc;
}

.location-detail {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin-top: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.detail-header h4 {
  margin: 0;
  color: #333;
}

.close-detail {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
  line-height: 1;
  padding: 0;
}

.detail-item {
  margin-bottom: 10px;
  color: #666;
  font-size: 0.95rem;
}

.detail-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.add-btn, .navigate-btn {
  flex: 1;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
  font-weight: 500;
}

.add-btn {
  background: #2ecc71;
  color: white;
}

.add-btn:hover {
  background: #27ae60;
}

.navigate-btn {
  background: #3498db;
  color: white;
}

.navigate-btn:hover {
  background: #2980b9;
}

.route-info {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-height: 300px;
  overflow-y: auto;
}

.route-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.route-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2575fc;
}

.clear-route {
  padding: 8px 16px;
  background: #f44336;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.clear-route:hover {
  background: #d32f2f;
}

.route-steps h4 {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.1rem;
}

.route-steps ol {
  padding-left: 20px;
  margin: 0;
}

.route-steps li {
  margin-bottom: 8px;
  color: #666;
  line-height: 1.4;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
  transition: color 0.3s;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.poi-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.poi-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.poi-info-full {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  color: #666;
  line-height: 1.5;
}

.tag {
  display: inline-block;
  background: #e3f2fd;
  color: #1976d2;
  padding: 4px 10px;
  border-radius: 12px;
  margin-right: 8px;
  margin-bottom: 8px;
  font-size: 0.85rem;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 1024px) {
  .map-content {
    flex-direction: column;
  }
  
  .map-sidebar {
    width: 100%;
    max-height: 400px;
  }
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 15px;
  }
  
  .map-content {
    padding: 15px;
  }
  
  .poi-categories {
    grid-template-columns: 1fr;
  }
  
  .route-stats {
    flex-direction: column;
    gap: 10px;
  }
}
</style>