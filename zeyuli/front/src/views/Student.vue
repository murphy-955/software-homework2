<template>
  <div class="student-container">
    <nav class="navbar">
      <div class="navbar-brand">旅行助手</div>
      <div class="navbar-menu">
        <a href="/" class="nav-item">首页</a>
        <a href="/map" class="nav-item">地图服务</a>
        <a href="/student" class="nav-item active">学生线路</a>
      </div>
      <div class="navbar-user">
        <span class="username">{{ username }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </nav>

    <div class="student-content">
      <div class="student-header">
        <h2>学生专属线路</h2>
        <p>为学生量身定制的经济实惠、寓教于乐的旅行线路</p>
      </div>

      <div class="student-tools">
        <div class="filter-section">
          <select v-model="selectedCity" class="filter-select">
            <option value="all">全部城市</option>
            <option v-for="city in cities" :key="city" :value="city">{{ city }}</option>
          </select>
          
          <select v-model="selectedTheme" class="filter-select">
            <option value="all">全部主题</option>
            <option v-for="theme in themes" :key="theme.value" :value="theme.value">{{ theme.label }}</option>
          </select>
          
          <select v-model="selectedDuration" class="filter-select">
            <option value="all">全部天数</option>
            <option value="1">1天</option>
            <option value="2">2天</option>
            <option value="3">3天</option>
            <option value="7+">7天以上</option>
          </select>
        </div>

        <div class="sort-section">
          <label for="sort">排序方式：</label>
          <select id="sort" v-model="sortBy" class="sort-select">
            <option value="popularity">按热门程度</option>
            <option value="price_asc">价格从低到高</option>
            <option value="price_desc">价格从高到低</option>
            <option value="rating">按评分排序</option>
          </select>
        </div>
      </div>

      <div class="discount-banner">
        <div class="discount-content">
          <i class="fas fa-tags"></i>
          <div class="discount-text">
            <h4>学生专享优惠</h4>
            <p>凭学生证可享受8折优惠，部分景点免费入场</p>
          </div>
          <a href="#" class="discount-btn">了解详情</a>
        </div>
      </div>

      <div class="routes-container">
        <div 
          v-for="route in sortedRoutes" 
          :key="route.id" 
          class="route-card"
          @click="viewRouteDetails(route)"
        >
          <div class="route-image">
            <img :src="route.image" :alt="route.name" />
            <div class="route-duration">
              <i class="far fa-calendar-alt"></i>
              <span>{{ route.duration }}</span>
            </div>
            <div v-if="route.discount" class="route-discount">
              {{ route.discount }}折
            </div>
          </div>
          
          <div class="route-info">
            <div class="route-header">
              <h3>{{ route.name }}</h3>
              <div class="route-rating">
                <i class="fas fa-star"></i>
                <span>{{ route.rating }}</span>
              </div>
            </div>
            
            <div class="route-meta">
              <span class="route-city">{{ route.city }}</span>
              <span class="route-theme">{{ getThemeLabel(route.theme) }}</span>
              <span class="route-students">{{ route.studentsJoined }}名学生已参加</span>
            </div>
            
            <div class="route-highlights">
              <h4>亮点</h4>
              <ul>
                <li v-for="(highlight, index) in route.highlights.slice(0, 3)" :key="index">
                  <i class="fas fa-check"></i>
                  {{ highlight }}
                </li>
              </ul>
            </div>
            
            <div class="route-footer">
              <div class="route-price">
                <span class="price-tag">¥</span>
                <span class="price-amount">{{ route.price }}</span>
                <span class="price-unit">/人起</span>
              </div>
              <button class="view-btn">查看详情</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 线路详情模态框 -->
      <div v-if="selectedRoute" class="modal-overlay" @click.self="selectedRoute = null">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ selectedRoute.name }}</h3>
            <button class="close-btn" @click="selectedRoute = null">&times;</button>
          </div>
          
          <div class="modal-body">
            <div class="route-detail-images">
              <img v-for="(image, index) in selectedRoute.images" :key="index" :src="image" :alt="selectedRoute.name" />
            </div>
            
            <div class="route-detail-info">
              <div class="detail-meta">
                <span class="detail-city">{{ selectedRoute.city }}</span>
                <span class="detail-theme">{{ getThemeLabel(selectedRoute.theme) }}</span>
                <span class="detail-duration">{{ selectedRoute.duration }}</span>
                <div class="detail-rating">
                  <i class="fas fa-star"></i>
                  <span>{{ selectedRoute.rating }}</span>
                  <span class="rating-count">({{ selectedRoute.reviews }}条评价)</span>
                </div>
              </div>
              
              <div class="detail-price">
                <span class="price-tag">¥</span>
                <span class="price-amount">{{ selectedRoute.price }}</span>
                <span class="price-unit">/人起</span>
                <span v-if="selectedRoute.originalPrice" class="original-price">¥{{ selectedRoute.originalPrice }}</span>
              </div>
              
              <div class="detail-description">
                <h4>线路介绍</h4>
                <p>{{ selectedRoute.description }}</p>
              </div>
              
              <div class="detail-itinerary">
                <h4>行程安排</h4>
                <div v-for="(day, index) in selectedRoute.itinerary" :key="index" class="itinerary-day">
                  <h5>第{{ index + 1 }}天</h5>
                  <div class="itinerary-activities">
                    <div v-for="(activity, actIndex) in day.activities" :key="actIndex" class="activity-item">
                      <div class="activity-time">{{ activity.time }}</div>
                      <div class="activity-content">
                        <h6>{{ activity.name }}</h6>
                        <p>{{ activity.description }}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="detail-includes">
                <h4>费用包含</h4>
                <ul>
                  <li v-for="(item, index) in selectedRoute.includes" :key="index">
                    <i class="fas fa-check-circle"></i>
                    {{ item }}
                  </li>
                </ul>
              </div>
              
              <div class="detail-notice">
                <h4>注意事项</h4>
                <ul>
                  <li v-for="(item, index) in selectedRoute.notices" :key="index">
                    <i class="fas fa-exclamation-circle"></i>
                    {{ item }}
                  </li>
                </ul>
              </div>
              
              <div class="detail-actions">
                <button class="book-btn">立即预订</button>
                <button class="favorite-btn" :class="{ active: selectedRoute.isFavorite }" @click.stop="toggleFavorite(selectedRoute)">
                  <i class="far fa-heart"></i>
                  {{ selectedRoute.isFavorite ? '已收藏' : '收藏' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { getUserInfo, logout } from '../utils/auth.js'

export default {
  name: 'Student',
  data() {
    return {
      username: localStorage.getItem('username') || '用户',
      selectedCity: 'all',
      selectedTheme: 'all',
      selectedDuration: 'all',
      sortBy: 'popularity',
      loading: false,
      error: null,
      cities: ['北京', '上海', '广州', '深圳', '成都', '杭州', '重庆', '西安', '武汉', '南京'],
      themes: [
        { value: 'culture', label: '文化古迹' },
        { value: 'nature', label: '自然风光' },
        { value: 'education', label: '教育考察' },
        { value: 'leisure', label: '休闲娱乐' }
      ],
      routes: [],
      selectedRoute: null,
      favoriteRoutes: [],
      showDiscountModal: false
    }
  },
  mounted() {
    // 检查登录状态
    this.checkLoginStatus()
    // 加载收藏数据
    this.loadFavorites()
    // 加载模拟线路数据
    this.loadRoutes()
  },
  computed: {
    // 过滤和排序线路
    sortedRoutes() {
      let result = [...this.routes]
      
      // 按城市过滤
      if (this.selectedCity !== 'all') {
        result = result.filter(route => route.city === this.selectedCity)
      }
      
      // 按主题过滤
      if (this.selectedTheme !== 'all') {
        result = result.filter(route => route.theme === this.selectedTheme)
      }
      
      // 按天数过滤
      if (this.selectedDuration !== 'all') {
        if (this.selectedDuration === '7+') {
          result = result.filter(route => route.days >= 7)
        } else {
          result = result.filter(route => route.days === parseInt(this.selectedDuration))
        }
      }
      
      // 排序
      switch (this.sortBy) {
        case 'price_asc':
          result.sort((a, b) => a.minBudget - b.minBudget)
          break
        case 'price_desc':
          result.sort((a, b) => b.minBudget - a.minBudget)
          break
        case 'rating':
          result.sort((a, b) => b.rating - a.rating)
          break
        case 'popularity':
        default:
          result.sort((a, b) => b.viewCount - a.viewCount)
          break
      }
      
      return result
    }
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const userInfo = getUserInfo()
      if (userInfo) {
        this.username = userInfo.username || '用户'
      }
    },
    
    // 加载线路数据
    async loadRoutes() {
      this.loading = true
      this.error = null
      
      try {
        // 根据过滤条件构建请求参数
        const params = {
          city: this.selectedCity === 'all' ? '' : this.selectedCity,
          studentType: 'college', // 假设默认为大学生
          days: this.selectedDuration === 'all' ? 0 : this.selectedDuration === '7+' ? 7 : parseInt(this.selectedDuration),
          limit: 10
        }
        
        const response = await studentAPI.getRecommendedRoutes(params)
        this.routes = response.data || []
      } catch (err) {
        console.error('加载线路数据失败:', err)
        this.error = '加载线路数据失败，请稍后重试'
        // 如果API调用失败，使用模拟数据
        this.useMockData()
      } finally {
        this.loading = false
      }
    },
    
    // 使用模拟数据
    useMockData() {
      this.routes = [
        {
          routeId: '1',
          routeName: '北京高校文化之旅',
          city: '北京',
          routeType: 'education',
          days: 3,
          minBudget: 599,
          maxBudget: 799,
          rating: 4.8,
          ratingCount: 1256,
          viewCount: 8952,
          recommendedReasons: [
            '参观北京大学、清华大学等名校',
            '故宫博物院学生特惠票',
            '专业讲解员带队'
          ],
          description: '专为学生设计的北京文化之旅，探访中国顶尖学府，感受浓厚的学术氛围，同时游览北京著名的文化景点，增长见识，开拓视野。',
          studentDiscounts: [
            {
              discountName: '学生特惠',
              description: '凭有效学生证享受7.5折优惠',
              discountValue: 7.5,
              requiredDocuments: '学生证',
              isValid: true
            }
          ],
          dailySchedules: [
            {
              day: 1,
              morning: {
                activity: '北京大学参观，游览未名湖、博雅塔等标志性建筑',
                timeRange: '09:00-12:00'
              },
              afternoon: {
                activity: '清华大学参观，感受顶尖学府的学术氛围',
                timeRange: '14:00-17:00'
              },
              evening: {
                activity: '王府井步行街自由活动，体验北京繁华的商业街区',
                timeRange: '18:00-20:00'
              }
            },
            {
              day: 2,
              morning: {
                activity: '故宫博物院参观，了解中国古代皇家历史',
                timeRange: '08:30-12:00'
              },
              afternoon: {
                activity: '景山公园游览，俯瞰故宫全景',
                timeRange: '13:30-16:00'
              },
              evening: {
                activity: '南锣鼓巷游览，品尝特色小吃',
                timeRange: '18:00-20:30'
              }
            }
          ]
        },
        {
          routeId: '2',
          routeName: '上海科技探索营',
          city: '上海',
          routeType: 'education',
          days: 2,
          minBudget: 499,
          maxBudget: 599,
          rating: 4.7,
          ratingCount: 845,
          viewCount: 6723,
          recommendedReasons: [
            '上海科技馆互动体验',
            '上海博物馆文化之旅',
            '复旦大学参观交流'
          ],
          description: '探索上海的科技与文化，参观上海科技馆，了解最新科技发展，同时感受上海的城市魅力。',
          studentDiscounts: [
            {
              discountName: '学生优惠',
              description: '凭有效学生证享受8折优惠',
              discountValue: 8,
              requiredDocuments: '学生证',
              isValid: true
            }
          ]
        },
        {
          routeId: '3',
          routeName: '成都熊猫基地一日游',
          city: '成都',
          routeType: 'nature',
          days: 1,
          minBudget: 199,
          maxBudget: 299,
          rating: 4.9,
          ratingCount: 2314,
          viewCount: 12536,
          recommendedReasons: [
            '熊猫基地学生票半价',
            '含专业讲解',
            '市内交通接送'
          ],
          description: '一日游熊猫基地，近距离观赏国宝大熊猫，了解大熊猫保护知识。',
          studentDiscounts: [
            {
              discountName: '学生半价',
              description: '熊猫基地门票学生半价',
              discountValue: 5,
              requiredDocuments: '学生证',
              isValid: true
            }
          ]
        }
      ]
    },
    
    // 获取主题标签
    getThemeLabel(themeValue) {
      const theme = this.themes.find(t => t.value === themeValue)
      return theme ? theme.label : themeValue
    },
    
    // 获取线路图片（默认占位图）
    getRouteImage(route) {
      // 在实际应用中，这里应该返回真实的图片URL
      return `https://via.placeholder.com/600x400?text=${encodeURIComponent(route.routeName)}`
    },
    
    // 查看线路详情
    viewRouteDetails(route) {
      this.selectedRoute = route
    },
    
    // 关闭线路详情
    closeRouteDetails() {
      this.selectedRoute = null
    },
    
    // 显示优惠详情
    showDiscountDetails() {
      this.showDiscountModal = true
    },
    
    // 加载收藏数据
    loadFavorites() {
      const favorites = localStorage.getItem('favoriteRoutes')
      if (favorites) {
        try {
          this.favoriteRoutes = JSON.parse(favorites)
        } catch (e) {
          this.favoriteRoutes = []
        }
      }
    },
    
    // 保存收藏数据
    saveFavorites() {
      localStorage.setItem('favoriteRoutes', JSON.stringify(this.favoriteRoutes))
    },
    
    // 检查是否已收藏
    isFavorite(routeId) {
      return this.favoriteRoutes.includes(routeId)
    },
    
    // 切换收藏状态
    toggleFavorite(route) {
      const index = this.favoriteRoutes.indexOf(route.routeId)
      if (index > -1) {
        this.favoriteRoutes.splice(index, 1)
      } else {
        this.favoriteRoutes.push(route.routeId)
      }
      this.saveFavorites()
    },
    
    // 处理退出登录
    handleLogout() {
      logout()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.student-container {
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

.student-content {
  flex: 1;
  padding: 30px;
}

.student-header {
  text-align: center;
  margin-bottom: 30px;
}

.student-header h2 {
  color: #333;
  margin-bottom: 10px;
}

.student-header p {
  color: #666;
  font-size: 1.1rem;
}

.student-tools {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.filter-section {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
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

.sort-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-select {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 0.9rem;
  background: white;
}

.discount-banner {
  background: linear-gradient(135deg, #ff6b6b, #ffa502);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  color: white;
}

.discount-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.discount-content i {
  font-size: 2.5rem;
}

.discount-text h4 {
  margin: 0 0 5px 0;
  font-size: 1.2rem;
}

.discount-text p {
  margin: 0;
  opacity: 0.9;
}

.discount-btn {
  background: white;
  color: #ff6b6b;
  padding: 10px 20px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
}

.discount-btn:hover {
  background: #fff8e1;
  color: #ffa502;
}

.routes-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
}

.route-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.route-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.route-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.route-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.route-card:hover .route-image img {
  transform: scale(1.05);
}

.route-duration {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(37, 117, 252, 0.9);
  color: white;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 5px;
}

.route-discount {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #ff6b6b;
  color: white;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 0.9rem;
  font-weight: 600;
}

.route-info {
  padding: 20px;
}

.route-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.route-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.2rem;
  flex: 1;
  margin-right: 10px;
}

.route-rating {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #ff9800;
  font-weight: 600;
  white-space: nowrap;
}

.route-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.route-city, .route-theme, .route-students {
  font-size: 0.85rem;
  padding: 4px 10px;
  border-radius: 12px;
}

.route-city {
  background: #e6f0ff;
  color: #2575fc;
}

.route-theme {
  background: #f3e8ff;
  color: #6a11cb;
}

.route-students {
  background: #fff3cd;
  color: #856404;
}

.route-highlights {
  margin-bottom: 20px;
}

.route-highlights h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 1rem;
}

.route-highlights ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.route-highlights li {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 5px;
  color: #666;
  font-size: 0.9rem;
}

.route-highlights i {
  color: #2ecc71;
  margin-top: 3px;
}

.route-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.route-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-tag {
  color: #ff6b6b;
  font-size: 0.9rem;
  font-weight: 600;
}

.price-amount {
  color: #ff6b6b;
  font-size: 1.8rem;
  font-weight: 700;
}

.price-unit {
  color: #999;
  font-size: 0.85rem;
}

.view-btn {
  padding: 8px 16px;
  background: #2575fc;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
}

.view-btn:hover {
  background: #1a65e0;
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
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
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

.route-detail-images {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 10px;
  margin-bottom: 20px;
}

.route-detail-images img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
}

.detail-city, .detail-theme, .detail-duration {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
}

.detail-city {
  background: #e6f0ff;
  color: #2575fc;
}

.detail-theme {
  background: #f3e8ff;
  color: #6a11cb;
}

.detail-duration {
  background: #d4edda;
  color: #155724;
}

.detail-rating {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #ff9800;
}

.rating-count {
  color: #999;
  font-size: 0.9rem;
  font-weight: normal;
}

.detail-price {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-bottom: 20px;
}

.detail-price .price-tag {
  font-size: 1rem;
}

.detail-price .price-amount {
  font-size: 2.5rem;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 1.2rem;
}

.route-detail-info h4 {
  color: #333;
  margin: 20px 0 10px 0;
}

.route-detail-info p {
  color: #666;
  line-height: 1.6;
  margin: 0 0 15px 0;
}

.itinerary-day {
  margin-bottom: 20px;
}

.itinerary-day h5 {
  color: #2575fc;
  margin: 0 0 15px 0;
  font-size: 1.1rem;
  border-left: 4px solid #2575fc;
  padding-left: 10px;
}

.activity-item {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.activity-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.activity-time {
  flex: 0 0 80px;
  background: #f8f9fa;
  color: #666;
  padding: 5px 10px;
  border-radius: 6px;
  font-weight: 500;
  text-align: center;
}

.activity-content h6 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 1rem;
}

.activity-content p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.detail-includes ul, .detail-notice ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.detail-includes li, .detail-notice li {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 10px;
  color: #666;
}

.detail-includes i {
  color: #2ecc71;
  margin-top: 3px;
}

.detail-notice i {
  color: #f39c12;
  margin-top: 3px;
}

.detail-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.book-btn {
  flex: 1;
  background: linear-gradient(135deg, #ff6b6b, #ffa502);
  color: white;
  border: none;
  padding: 15px 20px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.book-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 107, 107, 0.4);
}

.favorite-btn {
  padding: 15px 20px;
  background: #f8f9fa;
  color: #666;
  border: 2px solid #ddd;
  border-radius: 8px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.favorite-btn:hover {
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.favorite-btn.active {
  background: #ff6b6b;
  color: white;
  border-color: #ff6b6b;
}

.favorite-btn.active i {
  color: white;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 15px;
  }
  
  .student-content {
    padding: 15px;
  }
  
  .filter-section {
    flex-direction: column;
  }
  
  .sort-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .discount-content {
    flex-direction: column;
    text-align: center;
  }
  
  .routes-container {
    grid-template-columns: 1fr;
  }
  
  .route-detail-images {
    grid-template-columns: 1fr;
  }
  
  .activity-item {
    flex-direction: column;
  }
  
  .activity-time {
    flex: none;
    width: fit-content;
  }
  
  .detail-actions {
    flex-direction: column;
  }
}
</style>

