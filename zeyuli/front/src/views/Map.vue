<template>
  <div class="map-container">
    <nav class="navbar">
      <div class="navbar-brand">旅行助手</div>
      <div class="navbar-menu">
        <a href="/" class="nav-item">首页</a>
        <a href="/map" class="nav-item active">地图服务</a>
        <a href="/student" class="nav-item">学生线路</a>
      </div>
      <div class="navbar-user">
        <span class="username">{{ username }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </nav>

    <div class="map-content">
      <div class="map-header">
        <h2>地图服务</h2>
        <p>探索热门旅游目的地和景点推荐</p>
      </div>

      <div class="map-tools">
        <div class="search-section">
          <input type="text" v-model="searchQuery" placeholder="搜索城市或景点" class="search-input">
          <button class="search-btn" @click="searchLocation">搜索</button>
        </div>

        <div class="filter-section">
          <select v-model="selectedCategory" class="filter-select">
            <option value="all">全部景点</option>
            <option value="natural">自然风光</option>
            <option value="cultural">文化古迹</option>
            <option value="entertainment">娱乐休闲</option>
            <option value="shopping">购物美食</option>
          </select>
          
          <select v-model="selectedCity" class="filter-select">
            <option value="all">全部城市</option>
            <option v-for="city in cities" :key="city" :value="city">{{ city }}</option>
          </select>
        </div>
      </div>

      <div class="map-main">
        <div class="map-view">
          <!-- 地图容器 -->
          <div class="map-placeholder">
            <div class="map-message">
              <i class="fas fa-map-marked-alt"></i>
              <p>地图服务将在这里显示</p>
              <p class="hint">搜索或选择一个地点查看详情</p>
            </div>
          </div>
        </div>

        <div class="location-list">
          <h3>热门地点</h3>
          <div class="location-items">
            <div 
              v-for="location in filteredLocations" 
              :key="location.id" 
              class="location-item"
              @click="selectLocation(location)"
            >
              <div class="location-image">
                <img :src="location.image" :alt="location.name" />
              </div>
              <div class="location-info">
                <h4>{{ location.name }}</h4>
                <div class="location-meta">
                  <span class="location-city">{{ location.city }}</span>
                  <span class="location-category">{{ getCategoryName(location.category) }}</span>
                </div>
                <div class="location-rating">
                  <i class="fas fa-star"></i>
                  <span>{{ location.rating }}</span>
                </div>
                <p class="location-description">{{ truncateText(location.description, 100) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 地点详情模态框 -->
      <div v-if="selectedLocation" class="modal-overlay" @click.self="selectedLocation = null">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ selectedLocation.name }}</h3>
            <button class="close-btn" @click="selectedLocation = null">&times;</button>
          </div>
          <div class="modal-body">
            <div class="location-detail-image">
              <img :src="selectedLocation.image" :alt="selectedLocation.name" />
            </div>
            <div class="location-detail-info">
              <div class="detail-meta">
                <span class="detail-city">{{ selectedLocation.city }}</span>
                <span class="detail-category">{{ getCategoryName(selectedLocation.category) }}</span>
                <div class="detail-rating">
                  <i class="fas fa-star"></i>
                  <span>{{ selectedLocation.rating }}</span>
                </div>
              </div>
              <div class="detail-description">
                <h4>景点介绍</h4>
                <p>{{ selectedLocation.description }}</p>
              </div>
              <div class="detail-address">
                <h4>地址信息</h4>
                <p>{{ selectedLocation.address }}</p>
              </div>
              <div class="detail-hours">
                <h4>开放时间</h4>
                <p>{{ selectedLocation.openHours }}</p>
              </div>
              <div class="detail-price">
                <h4>门票价格</h4>
                <p>{{ selectedLocation.price }}</p>
              </div>
              <div class="detail-tips">
                <h4>旅行贴士</h4>
                <p>{{ selectedLocation.tips }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapAPI } from '../utils/api'
import { isLoggedIn, logout as authLogout } from '../utils/auth'

export default {
  name: 'Map',
  data() {
    return {
      username: localStorage.getItem('username') || '用户',
      searchQuery: '',
      selectedCategory: 'all',
      selectedCity: 'all',
      cities: ['北京', '上海', '广州', '深圳', '成都', '杭州', '重庆', '西安', '武汉', '南京'],
      locations: [],
      selectedLocation: null,
      loading: false,
      currentView: 'popular' // 'popular' or 'search'
    }
  },
  mounted() {
    // 检查登录状态
    if (!isLoggedIn()) {
      this.$router.push('/login')
      return
    }
    
    // 加载地点数据
    this.loadLocations()
  },
  computed: {
    // 过滤地点列表
    filteredLocations() {
      let result = [...this.locations]
      
      // 按城市过滤
      if (this.selectedCity !== 'all') {
        result = result.filter(loc => loc.city === this.selectedCity)
      }
      
      // 按分类过滤
      if (this.selectedCategory !== 'all') {
        result = result.filter(loc => loc.category === this.selectedCategory)
      }
      
      // 按搜索关键词过滤
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        result = result.filter(loc => 
          loc.name.toLowerCase().includes(query) ||
          loc.description.toLowerCase().includes(query) ||
          loc.city.toLowerCase().includes(query)
        )
      }
      
      return result
    }
  },
  methods: {
    // 加载地点数据
    async loadLocations() {
      this.loading = true
      try {
        // 调用API获取热门景点
        const response = await mapAPI.searchPlaces('热门景点')
        
        if (response && response.data) {
          this.locations = response.data
        } else {
          // 使用模拟数据作为后备
          this.loadMockLocations()
        }
      } catch (error) {
        console.error('加载地点数据失败:', error)
        // 使用模拟数据作为降级方案
        this.loadMockLocations()
      } finally {
        this.loading = false
      }
    },
    
    // 加载模拟地点数据
    loadMockLocations() {
      this.locations = [
        {
          id: 1,
          name: '故宫博物院',
          city: '北京',
          category: 'cultural',
          rating: 4.8,
          image: 'https://via.placeholder.com/400x300?text=故宫博物院',
          description: '故宫博物院是中国明清两代的皇家宫殿，旧称为紫禁城，位于北京中轴线的中心，是中国古代宫廷建筑之精华。是世界上现存规模最大、保存最为完整的木质结构古建筑之一。',
          address: '北京市东城区景山前街4号',
          openHours: '周二至周日 8:30-17:00（周一闭馆）',
          price: '旺季60元，淡季40元',
          tips: '建议提前在官网预约门票，避开节假日人流高峰。'
        },
        {
          id: 2,
          name: '长城',
          city: '北京',
          category: 'cultural',
          rating: 4.9,
          image: 'https://via.placeholder.com/400x300?text=长城',
          description: '长城是中国古代的伟大防御工程，是世界文化遗产，也是世界七大奇迹之一。八达岭长城是明长城中保存最好的一段，也是最具代表性的一段。',
          address: '北京市延庆区八达岭特区',
          openHours: '夏季 6:30-19:00，冬季 7:00-18:00',
          price: '40元',
          tips: '建议早上或傍晚前往，避开正午高温时段。'
        },
        {
          id: 3,
          name: '外滩',
          city: '上海',
          category: 'entertainment',
          rating: 4.7,
          image: 'https://via.placeholder.com/400x300?text=外滩',
          description: '外滩是上海最具代表性的景点之一，位于上海市黄浦区的黄浦江畔，是上海的金融和商业中心，也是观赏上海天际线的最佳地点。',
          address: '上海市黄浦区中山东一路',
          openHours: '全天开放',
          price: '免费',
          tips: '夜晚的外滩灯光秀非常漂亮，建议傍晚前往。'
        },
        {
          id: 4,
          name: '西湖',
          city: '杭州',
          category: 'natural',
          rating: 4.9,
          image: 'https://via.placeholder.com/400x300?text=西湖',
          description: '西湖位于浙江省杭州市西湖区龙井路1号，杭州市区西部，景区总面积49平方千米，汇水面积为21.22平方千米，湖面面积为6.38平方千米。西湖南、西、北三面环山，东临城区，南部和钱塘江隔山相望。',
          address: '浙江省杭州市西湖区龙井路1号',
          openHours: '全天开放',
          price: '免费',
          tips: '建议租自行车环湖游览，可以更好地欣赏西湖美景。'
        },
        {
          id: 5,
          name: '宽窄巷子',
          city: '成都',
          category: 'shopping',
          rating: 4.6,
          image: 'https://via.placeholder.com/400x300?text=宽窄巷子',
          description: '宽窄巷子位于四川省成都市青羊区，由宽巷子、窄巷子、井巷子平行排列组成，全为青黛砖瓦的仿古四合院落，这里也是成都遗留下来的较成规模的清朝古街道，与大慈寺、文殊院一起并称为成都三大历史文化名城保护街区。',
          address: '四川省成都市青羊区同仁路',
          openHours: '全天开放',
          price: '免费',
          tips: '这里有很多成都特色小吃，可以品尝正宗川菜。'
        }
      ]
    },
    
    // 搜索地点
    async searchLocation() {
      if (!this.searchQuery.trim()) return
      
      this.loading = true
      try {
        // 调用API搜索地点
        const response = await mapAPI.searchPlaces(this.searchQuery)
        
        if (response && response.data) {
          this.locations = response.data
        } else {
          // 本地过滤作为后备
          // 保留现有过滤逻辑
        }
        
        this.currentView = 'search'
      } catch (error) {
        console.error('搜索地点失败:', error)
        alert('搜索失败，请重试')
      } finally {
        this.loading = false
      }
    },
    
    // 选择地点
    async selectLocation(location) {
      this.loading = true
      try {
        // 调用API获取地点详情
        const response = await mapAPI.getPlaceDetail(location.id)
        
        if (response && response.data) {
          this.selectedLocation = response.data
        } else {
          // 使用传入的地点对象作为后备
          this.selectedLocation = location
        }
      } catch (error) {
        console.error('获取地点详情失败:', error)
        // 使用传入的地点对象作为降级方案
        this.selectedLocation = location
      } finally {
        this.loading = false
      }
    },
    
    // 获取分类名称
    getCategoryName(category) {
      const categoryMap = {
        'natural': '自然风光',
        'cultural': '文化古迹',
        'entertainment': '娱乐休闲',
        'shopping': '购物美食'
      }
      return categoryMap[category] || category
    },
    
    // 截断文本
    truncateText(text, length) {
      if (text.length <= length) return text
      return text.substring(0, length) + '...'
    },
    
    // 退出登录
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        authLogout()
        this.$router.push('/login')
      }
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
  padding: 30px;
}

.map-header {
  text-align: center;
  margin-bottom: 30px;
}

.map-header h2 {
  color: #333;
  margin-bottom: 10px;
}

.map-header p {
  color: #666;
  font-size: 1.1rem;
}

.map-tools {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
}

.search-input:focus {
  border-color: #2575fc;
  outline: none;
}

.search-btn {
  padding: 12px 24px;
  background: #2575fc;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  background: #1a65e0;
}

.filter-section {
  display: flex;
  gap: 15px;
}

.filter-select {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  background: white;
}

.filter-select:focus {
  border-color: #2575fc;
  outline: none;
}

.map-main {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 30px;
  min-height: 600px;
}

.map-view {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
}

.map-placeholder {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8f9fa;
}

.map-message {
  text-align: center;
  color: #666;
}

.map-message i {
  font-size: 3rem;
  margin-bottom: 20px;
  color: #2575fc;
}

.map-message p {
  margin-bottom: 10px;
  font-size: 1.1rem;
}

.hint {
  font-size: 0.9rem;
  color: #999;
}

.location-list {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
}

.location-list h3 {
  margin-bottom: 20px;
  color: #333;
}

.location-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 15px;
}

.location-item:hover {
  border-color: #2575fc;
  box-shadow: 0 5px 15px rgba(37, 117, 252, 0.1);
  transform: translateY(-2px);
}

.location-image {
  flex: 0 0 120px;
  height: 90px;
  overflow: hidden;
  border-radius: 8px;
}

.location-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.location-item:hover .location-image img {
  transform: scale(1.1);
}

.location-info {
  flex: 1;
  min-width: 0;
}

.location-info h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 1.1rem;
}

.location-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
  font-size: 0.85rem;
}

.location-city {
  color: #2575fc;
  background: #e6f0ff;
  padding: 2px 8px;
  border-radius: 12px;
}

.location-category {
  color: #6a11cb;
  background: #f3e8ff;
  padding: 2px 8px;
  border-radius: 12px;
}

.location-rating {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 8px;
  color: #ff9800;
  font-weight: 500;
}

.location-description {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
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
  max-width: 800px;
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

.location-detail-image {
  width: 100%;
  height: 300px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 20px;
}

.location-detail-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-meta {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.detail-city {
  color: #2575fc;
  background: #e6f0ff;
  padding: 6px 12px;
  border-radius: 16px;
  font-weight: 500;
}

.detail-category {
  color: #6a11cb;
  background: #f3e8ff;
  padding: 6px 12px;
  border-radius: 16px;
  font-weight: 500;
}

.detail-rating {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #ff9800;
  font-weight: 600;
  font-size: 1.1rem;
}

.location-detail-info h4 {
  color: #333;
  margin: 20px 0 10px 0;
}

.location-detail-info p {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

@media (max-width: 1024px) {
  .map-main {
    grid-template-columns: 1fr;
  }
  
  .location-list {
    max-height: 500px;
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
  
  .search-section {
    flex-direction: column;
  }
  
  .filter-section {
    flex-direction: column;
  }
  
  .location-item {
    flex-direction: column;
  }
  
  .location-image {
    width: 100%;
    flex: none;
  }
}
</style>
