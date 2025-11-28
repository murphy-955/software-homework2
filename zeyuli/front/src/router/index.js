import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
const Login = () => import('../views/Login.vue')
const Home = () => import('../views/Home.vue')
const MapService = () => import('../views/MapService.vue')
const Itinerary = () => import('../views/Itinerary.vue')
const StudentRoute = () => import('../views/StudentRoute.vue')
const NotFound = () => import('../views/NotFound.vue')

// 路由配置
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/map',
    name: 'MapService',
    component: MapService,
    meta: { requiresAuth: true }
  },
  {
    path: '/itinerary',
    name: 'Itinerary',
    component: Itinerary,
    meta: { requiresAuth: true }
  },
  {
    path: '/student-route',
    name: 'StudentRoute',
    component: StudentRoute,
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫，检查用户是否登录
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const token = localStorage.getItem('token')
  
  if (requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
