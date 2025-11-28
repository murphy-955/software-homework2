<template>
  <div class="itinerary-container">
    <nav class="navbar">
      <div class="navbar-brand">旅行助手</div>
      <div class="navbar-menu">
        <a href="/" class="nav-item">首页</a>
        <a href="/map" class="nav-item">地图服务</a>
        <a href="/student" class="nav-item">学生线路</a>
        <a href="/itinerary" class="nav-item active">行程规划</a>
      </div>
      <div class="navbar-user">
        <span class="username">{{ username }}</span>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </nav>

    <div class="itinerary-content">
      <div class="itinerary-header">
        <h2>行程规划</h2>
        <p>根据您的需求定制完美的旅行计划</p>
      </div>

      <div class="plan-options">
        <div class="option-card" @click="activeOption = 'budget'">
          <div class="option-icon">💰</div>
          <h3>按预算规划</h3>
          <p>根据您的预算生成性价比最高的行程</p>
        </div>
        <div class="option-card" @click="activeOption = 'personality'">
          <div class="option-icon">🎭</div>
          <h3>人格化行程</h3>
          <p>根据您的性格特点定制专属行程</p>
        </div>
        <div class="option-card" @click="activeOption = 'student'">
          <div class="option-icon">🎓</div>
          <h3>学生专属</h3>
          <p>为学生打造经济实惠的行程方案</p>
        </div>
        <div class="option-card" @click="activeOption = 'companion'">
          <div class="option-icon">👥</div>
          <h3>搭子行程</h3>
          <p>根据旅行伙伴类型生成合适的行程</p>
        </div>
      </div>

      <div class="plan-form" v-show="activeOption">
        <div v-if="activeOption === 'budget'" class="form-content">
          <h3>按预算规划行程</h3>
          <div class="form-group">
            <label>目的地城市：</label>
            <select v-model="budgetForm.city" class="form-select">
              <option value="">请选择城市</option>
              <option v-for="city in cities" :key="city" :value="city">{{ city }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>行程天数：</label>
            <input type="number" v-model="budgetForm.days" min="1" max="30" class="form-input" placeholder="请输入天数" />
          </div>
          <div class="form-group">
            <label>预算金额（元）：</label>
            <input type="number" v-model="budgetForm.budget" min="100" class="form-input" placeholder="请输入预算" />
          </div>
          <button class="submit-btn" @click="generateBudgetPlan" :disabled="loading">
            {{ loading ? '生成中...' : '生成行程' }}
          </button>
        </div>

        <div v-if="activeOption === 'personality'" class="form-content">
          <h3>人格化行程</h3>
          <div class="form-group">
            <label>目的地城市：</label>
            <select v-model="personalityForm.city" class="form-select">
              <option value="">请选择城市</option>
              <option v-for="city in cities" :key="city" :value="city">{{ city }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>行程天数：</label>
            <input type="number" v-model="personalityForm.days" min="1" max="30" class="form-input" placeholder="请输入天数" />
          </div>
          <div class="form-group">
            <label>人格类型：</label>
            <select v-model="personalityForm.personalityType" class="form-select">
              <option value="">请选择人格类型</option>
              <option value="adventurous">冒险家</option>
              <option value="cultural">文化探索者</option>
              <option value="relaxed">休闲度假型</option>
              <option value="foodie">美食爱好者</option>
              <option value="photographer">摄影发烧友</option>
            </select>
          </div>
          <button class="submit-btn" @click="generatePersonalityPlan" :disabled="loading">
            {{ loading ? '生成中...' : '生成行程' }}
          </button>
        </div>

        <div v-if="activeOption === 'student'" class="form-content">
          <h3>学生专属行程</h3>
          <div class="form-group">
            <label>学校名称：</label>
            <input type="text" v-model="studentForm.university" class="form-input" placeholder="请输入学校名称" />
          </div>
          <div class="form-group">
            <label>行程天数：</label>
            <input type="number" v-model="studentForm.days" min="1" max="30" class="form-input" placeholder="请输入天数" />
          </div>
          <div class="form-group">
            <label>最大预算（元）：</label>
            <input type="number" v-model="studentForm.maxBudget" min="100" class="form-input" placeholder="请输入最大预算" />
          </div>
          <button class="submit-btn" @click="generateStudentPlan" :disabled="loading">
            {{ loading ? '生成中...' : '生成行程' }}
          </button>
        </div>

        <div v-if="activeOption === 'companion'" class="form-content">
          <h3>搭子行程</h3>
          <div class="form-group">
            <label>目的地城市：</label>
            <select v-model="companionForm.city" class="form-select">
              <option value="">请选择城市</option>
              <option v-for="city in cities" :key="city" :value="city">{{ city }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>行程天数：</label>
            <input type="number" v-model="companionForm.days" min="1" max="30" class="form-input" placeholder="请输入天数" />
          </div>
          <div class="form-group">
            <label>搭子类型：</label>
            <select v-model="companionForm.companionType" class="form-select">
              <option value="">请选择搭子类型</option>
              <option value="family">家庭</option>
              <option value="friends">朋友</option>
              <option value="couple">情侣</option>
              <option value="classmates">同学</option>
              <option value="colleagues">同事</option>
            </select>
          </div>
          <button class="submit-btn" @click="generateCompanionPlan" :disabled="loading">
            {{ loading ? '生成中...' : '生成行程' }}
          </button>
        </div>
      </div>

      <div v-if="itineraryPlan" class="plan-result">
        <div class="plan-header">
          <h3>{{ itineraryPlan.planName }}</h3>
          <div class="plan-meta">
            <span>{{ itineraryPlan.city }}</span>
            <span>{{ itineraryPlan.days }}天</span>
            <span>预算：¥{{ itineraryPlan.totalBudget }}</span>
          </div>
        </div>

        <div class="plan-summary">
          <div class="summary-item">
            <h4>预计花费</h4>
            <p>¥{{ itineraryPlan.estimatedCost }}</p>
          </div>
          <div class="summary-item">
            <h4>住宿建议</h4>
            <p>{{ itineraryPlan.accommodationSuggestion }}</p>
          </div>
        </div>

        <div class="daily-itineraries">
          <div v-for="daily in itineraryPlan.dailyItineraries" :key="daily.day" class="daily-plan">
            <h4>第{{ daily.day }}天</h4>
            <div class="day-weather">天气：{{ daily.weather }}</div>
            
            <div class="attractions">
              <h5>景点：</h5>
              <ul>
                <li v-for="(attraction, index) in daily.attractions" :key="index">
                  {{ attraction.name }}
                </li>
              </ul>
            </div>

            <div class="suggestions">
              <h5>建议：</h5>
              <ul>
                <li v-for="(suggestion, index) in daily.suggestions" :key="index">
                  {{ suggestion }}
                </li>
              </ul>
            </div>

            <div class="daily-cost">今日花费：¥{{ daily.dailyCost }}</div>
          </div>
        </div>

        <div class="plan-actions">
          <button class="save-btn">保存行程</button>
          <button class="share-btn">分享行程</button>
          <button class="adjust-btn" @click="showAdjustForm = true">调整行程</button>
        </div>
      </div>

      <!-- 调整行程表单 -->
      <div v-if="showAdjustForm" class="modal-overlay" @click.self="showAdjustForm = false">
        <div class="modal-content">
          <div class="modal-header">
            <h3>调整行程</h3>
            <button class="close-btn" @click="showAdjustForm = false">&times;</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>天气情况：</label>
              <select v-model="adjustForm.weatherCondition" class="form-select">
                <option value="sunny">晴天</option>
                <option value="rainy">雨天</option>
                <option value="cloudy">多云</option>
                <option value="windy">大风</option>
              </select>
            </div>
            <div class="form-group">
              <label>交通情况：</label>
              <select v-model="adjustForm.trafficCondition" class="form-select">
                <option value="smooth">畅通</option>
                <option value="moderate">一般</option>
                <option value="congested">拥堵</option>
              </select>
            </div>
            <button class="submit-btn" @click="adjustItinerary">调整行程</button>
          </div>
        </div>
      </div>

      <!-- 错误提示 -->
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script>
import { getUserInfo, logout } from '../utils/auth.js'
import { itineraryAPI } from '../utils/api.js'

export default {
  name: 'Itinerary',
  data() {
    return {
      username: localStorage.getItem('username') || '用户',
      activeOption: null,
      loading: false,
      error: null,
      itineraryPlan: null,
      showAdjustForm: false,
      cities: ['北京', '上海', '广州', '深圳', '成都', '杭州', '重庆', '西安', '武汉', '南京'],
      budgetForm: {
        city: '',
        days: null,
        budget: null
      },
      personalityForm: {
        city: '',
        days: null,
        personalityType: ''
      },
      studentForm: {
        university: '',
        days: null,
        maxBudget: null
      },
      companionForm: {
        city: '',
        days: null,
        companionType: ''
      },
      adjustForm: {
        weatherCondition: 'sunny',
        trafficCondition: 'smooth'
      }
    }
  },
  mounted() {
    this.checkLoginStatus()
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const userInfo = getUserInfo()
      if (userInfo) {
        this.username = userInfo.username || '用户'
      }
    },
    
    // 生成按预算规划的行程
    async generateBudgetPlan() {
      this.loading = true
      this.error = null
      
      try {
        const response = await itineraryAPI.planByBudget(this.budgetForm)
        this.itineraryPlan = response
      } catch (err) {
        console.error('生成预算行程失败:', err)
        this.error = '生成行程失败，请稍后重试'
        // 使用模拟数据
        this.useMockBudgetPlan()
      } finally {
        this.loading = false
      }
    },
    
    // 生成人格化行程
    async generatePersonalityPlan() {
      this.loading = true
      this.error = null
      
      try {
        const response = await itineraryAPI.generatePersonality(this.personalityForm)
        this.itineraryPlan = response
      } catch (err) {
        console.error('生成人格化行程失败:', err)
        this.error = '生成行程失败，请稍后重试'
        // 使用模拟数据
        this.useMockPersonalityPlan()
      } finally {
        this.loading = false
      }
    },
    
    // 生成学生专属行程
    async generateStudentPlan() {
      this.loading = true
      this.error = null
      
      try {
        const response = await itineraryAPI.getStudentItinerary(this.studentForm)
        this.itineraryPlan = response
      } catch (err) {
        console.error('生成学生行程失败:', err)
        this.error = '生成行程失败，请稍后重试'
        // 使用模拟数据
        this.useMockStudentPlan()
      } finally {
        this.loading = false
      }
    },
    
    // 生成搭子行程
    async generateCompanionPlan() {
      this.loading = true
      this.error = null
      
      try {
        const response = await itineraryAPI.generateCompanion(this.companionForm)
        this.itineraryPlan = response
      } catch (err) {
        console.error('生成搭子行程失败:', err)
        this.error = '生成行程失败，请稍后重试'
        // 使用模拟数据
        this.useMockCompanionPlan()
      } finally {
        this.loading = false
      }
    },
    
    // 调整行程
    async adjustItinerary() {
      this.loading = true
      this.error = null
      
      try {
        const response = await itineraryAPI.adjustByCondition(this.adjustForm, this.itineraryPlan)
        this.itineraryPlan = response
        this.showAdjustForm = false
      } catch (err) {
        console.error('调整行程失败:', err)
        this.error = '调整行程失败，请稍后重试'
      } finally {
        this.loading = false
      }
    },
    
    // 模拟数据 - 预算行程
    useMockBudgetPlan() {
      this.itineraryPlan = {
        planName: '上海经济实惠3日游',
        city: '上海',
        days: 3,
        totalBudget: 1500,
        estimatedCost: 1350,
        accommodationSuggestion: '推荐入住青年旅舍，人均每晚80-120元',
        dailyItineraries: [
          {
            day: 1,
            attractions: [
              { name: '外滩', lat: 31.2397, lng: 121.4998 },
              { name: '南京路步行街', lat: 31.2341, lng: 121.4742 },
              { name: '豫园', lat: 31.2272, lng: 121.4923 }
            ],
            weather: '晴转多云',
            dailyCost: 450,
            suggestions: [
              '外滩建议傍晚前往，可以欣赏夜景',
              '南京路可以品尝上海特色小吃'
            ]
          },
          {
            day: 2,
            attractions: [
              { name: '上海博物馆', lat: 31.2304, lng: 121.4737 },
              { name: '人民广场', lat: 31.2304, lng: 121.4737 },
              { name: '田子坊', lat: 31.2197, lng: 121.4721 }
            ],
            weather: '多云',
            dailyCost: 400,
            suggestions: [
              '上海博物馆周一闭馆，请注意安排时间',
              '田子坊有很多特色小店，可以淘到有趣的纪念品'
            ]
          },
          {
            day: 3,
            attractions: [
              { name: '上海科技馆', lat: 31.2163, lng: 121.5809 },
              { name: '世纪公园', lat: 31.2138, lng: 121.5661 }
            ],
            weather: '晴',
            dailyCost: 500,
            suggestions: [
              '科技馆建议预留半天时间参观',
              '世纪公园可以野餐，建议自带午餐'
            ]
          }
        ],
        planType: 'budget'
      }
    },
    
    // 模拟数据 - 人格化行程
    useMockPersonalityPlan() {
      this.itineraryPlan = {
        planName: '北京文化探索之旅',
        city: '北京',
        days: 4,
        totalBudget: 2000,
        estimatedCost: 1800,
        accommodationSuggestion: '推荐入住四合院特色民宿',
        dailyItineraries: [
          {
            day: 1,
            attractions: [
              { name: '故宫博物院', lat: 39.9163, lng: 116.3972 },
              { name: '景山公园', lat: 39.9146, lng: 116.3972 }
            ],
            weather: '晴',
            dailyCost: 500,
            suggestions: [
              '故宫建议提前预约门票',
              '从景山公园可以俯瞰故宫全景'
            ]
          },
          {
            day: 2,
            attractions: [
              { name: '颐和园', lat: 39.9997, lng: 116.2751 },
              { name: '圆明园', lat: 40.0065, lng: 116.2756 }
            ],
            weather: '多云',
            dailyCost: 400,
            suggestions: [
              '颐和园面积较大，建议租自行车游览',
              '圆明园遗址公园可以了解历史文化'
            ]
          }
        ],
        planType: 'personality'
      }
    },
    
    // 模拟数据 - 学生行程
    useMockStudentPlan() {
      this.itineraryPlan = {
        planName: '成都学生特惠3日游',
        city: '成都',
        days: 3,
        totalBudget: 1200,
        estimatedCost: 1000,
        accommodationSuggestion: '推荐入住四川大学附近的经济型酒店',
        dailyItineraries: [
          {
            day: 1,
            attractions: [
              { name: '成都大熊猫繁育研究基地', lat: 30.7373, lng: 104.1827 },
              { name: '锦里古街', lat: 30.6630, lng: 104.0452 }
            ],
            weather: '多云',
            dailyCost: 350,
            suggestions: [
              '熊猫基地学生证半价优惠',
              '锦里古街晚上夜景很美，还有很多小吃'
            ]
          },
          {
            day: 2,
            attractions: [
              { name: '武侯祠', lat: 30.6644, lng: 104.0448 },
              { name: '宽窄巷子', lat: 30.6682, lng: 104.0598 }
            ],
            weather: '晴',
            dailyCost: 300,
            suggestions: [
              '武侯祠学生证有优惠',
              '宽窄巷子可以体验成都慢生活'
            ]
          }
        ],
        planType: 'student'
      }
    },
    
    // 模拟数据 - 搭子行程
    useMockCompanionPlan() {
      this.itineraryPlan = {
        planName: '杭州情侣浪漫2日游',
        city: '杭州',
        days: 2,
        totalBudget: 1800,
        estimatedCost: 1600,
        accommodationSuggestion: '推荐入住西湖边的精品民宿',
        dailyItineraries: [
          {
            day: 1,
            attractions: [
              { name: '西湖', lat: 30.2429, lng: 120.1484 },
              { name: '断桥残雪', lat: 30.2591, lng: 120.1434 },
              { name: '雷峰塔', lat: 30.2317, lng: 120.1497 }
            ],
            weather: '晴',
            dailyCost: 800,
            suggestions: [
              '西湖建议租自行车环湖游览',
              '傍晚可以在湖边欣赏夕阳'
            ]
          },
          {
            day: 2,
            attractions: [
              { name: '灵隐寺', lat: 30.2406, lng: 120.0989 },
              { name: '宋城', lat: 30.1945, lng: 120.1436 }
            ],
            weather: '多云',
            dailyCost: 800,
            suggestions: [
              '灵隐寺环境幽静，适合情侣游览',
              '宋城千古情演出值得一看'
            ]
          }
        ],
        planType: 'companion'
      }
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
.itinerary-container {
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

.itinerary-content {
  flex: 1;
  padding: 30px;
}

.itinerary-header {
  text-align: center;
  margin-bottom: 40px;
}

.itinerary-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 2rem;
}

.itinerary-header p {
  color: #666;
  font-size: 1.1rem;
}

.plan-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.option-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.option-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.option-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.option-card h3 {
  color: #333;
  margin-bottom: 10px;
}

.option-card p {
  color: #666;
  font-size: 0.95rem;
}

.plan-form {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-content h3 {
  color: #333;
  margin-bottom: 20px;
  border-bottom: 2px solid #2575fc;
  padding-bottom: 10px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-input, .form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
}

.form-input:focus, .form-select:focus {
  border-color: #2575fc;
  outline: none;
}

.submit-btn {
  background: linear-gradient(135deg, #2575fc, #6a11cb);
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(37, 117, 252, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.plan-result {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.plan-header {
  text-align: center;
  margin-bottom: 30px;
}

.plan-header h3 {
  color: #333;
  margin-bottom: 10px;
  font-size: 1.8rem;
}

.plan-meta {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.plan-meta span {
  background: #e6f0ff;
  color: #2575fc;
  padding: 6px 16px;
  border-radius: 20px;
  font-weight: 500;
}

.plan-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.summary-item {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.summary-item h4 {
  color: #666;
  margin-bottom: 10px;
  font-size: 1rem;
}

.summary-item p {
  color: #333;
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
}

.daily-itineraries {
  margin-bottom: 30px;
}

.daily-plan {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.daily-plan:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.daily-plan h4 {
  color: #2575fc;
  margin-bottom: 15px;
  font-size: 1.3rem;
  border-left: 4px solid #2575fc;
  padding-left: 10px;
}

.day-weather {
  background: #e3f2fd;
  color: #1976d2;
  padding: 8px 16px;
  border-radius: 20px;
  display: inline-block;
  margin-bottom: 15px;
}

.attractions, .suggestions {
  margin-bottom: 15px;
}

.attractions h5, .suggestions h5 {
  color: #333;
  margin-bottom: 10px;
  font-size: 1.1rem;
}

.attractions ul, .suggestions ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.attractions li, .suggestions li {
  padding: 8px 0;
  padding-left: 20px;
  position: relative;
  color: #666;
}

.attractions li:before, .suggestions li:before {
  content: '•';
  position: absolute;
  left: 0;
  color: #2575fc;
  font-size: 1.2em;
}

.daily-cost {
  background: #fff3cd;
  color: #856404;
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 600;
  text-align: right;
}

.plan-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.save-btn, .share-btn, .adjust-btn {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.save-btn {
  background: #2ecc71;
  color: white;
}

.save-btn:hover {
  background: #27ae60;
  transform: translateY(-2px);
}

.share-btn {
  background: #3498db;
  color: white;
}

.share-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
}

.adjust-btn {
  background: #f39c12;
  color: white;
}

.adjust-btn:hover {
  background: #e67e22;
  transform: translateY(-2px);
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
  max-width: 500px;
  width: 100%;
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

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 15px;
  border-radius: 8px;
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 15px;
  }
  
  .itinerary-content {
    padding: 15px;
  }
  
  .plan-options {
    grid-template-columns: 1fr;
  }
  
  .plan-actions {
    flex-direction: column;
  }
}
</style>