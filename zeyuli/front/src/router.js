import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Login from './views/Login.vue'
import Register from './views/Register.vue'
import Student from './views/Student.vue'
import Itinerary from './views/Itinerary.vue'
import MapView from './views/MapView.vue'
import { isLoggedIn } from './utils/auth.js'

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/student',
    name: 'student',
    component: Student,
    meta: { requiresAuth: true }
  },
  {
    path: '/itinerary',
    name: 'itinerary',
    component: Itinerary,
    meta: { requiresAuth: true }
  },
  {
    path: '/map',
    name: 'map',
    component: MapView,
    meta: { requiresAuth: true }
  },
  // 默认重定向到登录页
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const userLoggedIn = isLoggedIn()

  if (requiresAuth && !userLoggedIn) {
    // 需要登录但未登录，重定向到登录页
    next('/login')
  } else if (!requiresAuth && userLoggedIn) {
    // 不需要登录但已登录，重定向到首页
    next('/')
  } else {
    // 其他情况正常放行
    next()
  }
})

export default router