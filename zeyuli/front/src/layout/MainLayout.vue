<template>
  <!-- Grid：sidebar 展开=260px；收起=0px（主区吃满） -->
  <div class="app-layout" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-brand">
        <div class="brand-row" @click="navigateTo('/home')" title="回到首页">
          <div class="brand-logo">🧳</div>
          <div class="brand-text">TravelMate</div>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div class="sidebar-item" :class="{ active: currentPath === '/home' }" @click="navigateTo('/home')">
          <span class="icon">🏠</span>
          <span class="label">首页</span>
        </div>
        <div class="sidebar-item" :class="{ active: currentPath === '/result' }" @click="navigateTo('/result')">
          <span class="icon">📅</span>
          <span class="label">我的行程</span>
        </div>
        <div class="sidebar-item" :class="{ active: currentPath === '/map' }" @click="navigateTo('/map')">
          <span class="icon">🗺️</span>
          <span class="label">地图视图</span>
        </div>
        <div class="sidebar-item" :class="{ active: currentPath === '/budget' }" @click="navigateTo('/budget')">
          <span class="icon">💰</span>
          <span class="label">预算管理</span>
        </div>
        <div class="sidebar-item" :class="{ active: currentPath === '/profile' }" @click="navigateTo('/profile')">
          <span class="icon">👤</span>
          <span class="label">个人中心</span>
        </div>
      </nav>
    </aside>

    <!-- Main -->
    <main class="main-content">
      <!-- ✅ 永远贴着 sidebar/main 的分界线显示（收起时也在最左边） -->
      <button
          class="edge-toggle"
          type="button"
          @click="toggleSidebar"
          :aria-label="sidebarCollapsed ? '打开侧边栏' : '收起侧边栏'"
          :title="sidebarCollapsed ? '打开侧边栏' : '收起侧边栏'"
      >
        <span class="chev" :class="{ right: sidebarCollapsed }"></span>
      </button>

      <!-- 子页面内容 -->
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const sidebarCollapsed = ref(false);
const toggleSidebar = () => (sidebarCollapsed.value = !sidebarCollapsed.value);

const currentPath = computed(() => route.path);

const navigateTo = (path) => router.push(path);
</script>

<style>
/* 全局样式变量 - 放在这里让子组件也能用 */
:root {
  --purple-1: #667eea;
  --purple-2: #764ba2;
  --ink: #0f172a;
  --stroke: rgba(15, 23, 42, 0.08);
  --glass: rgba(255, 255, 255, 0.78);
  --glass-2: rgba(255, 255, 255, 0.62);
  --sidebar-w: 260px;
}
</style>

<style scoped>
/* ====== 布局：Grid，sidebar 收起时列宽=0，主区吃满视口 ====== */
.app-layout {
  display: grid;
  grid-template-columns: var(--sidebar-w) 1fr;
  min-height: 100vh;
  gap: 0;
  column-gap: 0;

  background:
      radial-gradient(circle at 15% 10%, rgba(255, 255, 255, 0.10), transparent 45%),
      radial-gradient(circle at 85% 30%, rgba(255, 255, 255, 0.08), transparent 40%),
      linear-gradient(135deg, var(--purple-1) 0%, var(--purple-2) 100%);
  position: relative;
  overflow: hidden;
  isolation: isolate;
}

/* 背景光晕层 */
.app-layout::before {
  content: "";
  position: absolute;
  inset: -120px;
  background:
      radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.12), transparent 45%),
      radial-gradient(circle at 70% 60%, rgba(255, 255, 255, 0.08), transparent 48%);
  filter: blur(18px);
  pointer-events: none;
  z-index: -1;
}

.app-layout.sidebar-collapsed {
  --sidebar-w: 0px;
}

/* ====== Sidebar ====== */
.sidebar {
  grid-column: 1;
  min-width: 0;
  height: 100vh;
  background: #ffffff;
  box-shadow: 0 14px 40px rgba(15, 23, 42, 0.12);
  border-right: 1px solid rgba(15, 23, 42, 0.06);
  z-index: 10;
  transition: opacity 0.2s ease, transform 0.25s ease;
}

.sidebar.collapsed {
  opacity: 0;
  pointer-events: none;
  transform: translateX(-12px);
}

.sidebar-brand {
  padding: 18px 16px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.06);
}
.brand-row {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}
.brand-logo {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--purple-1), var(--purple-2));
  box-shadow: 0 16px 36px rgba(102, 126, 234, 0.26);
  border: 1px solid rgba(255, 255, 255, 0.18);
  font-size: 20px; /* Added for emoji size */
}
.brand-text {
  font-weight: 900;
  letter-spacing: 0.02em;
  color: #1d4ed8;
  white-space: nowrap;
  font-size: 20px;
}

.sidebar-nav {
  padding: 8px 0;
}
.sidebar-item {
  padding: 14px 18px;
  margin: 8px 14px;
  border-radius: 16px;
  color: #334155;
  cursor: pointer;
  transition: all 0.22s ease;
  display: flex;
  align-items: center;
  gap: 14px;
  user-select: none;
}
.sidebar-item:hover {
  background: rgba(15, 23, 42, 0.04);
  transform: translateY(-1px);
}
.sidebar-item.active {
  background: linear-gradient(135deg, var(--purple-1), var(--purple-2));
  color: #fff;
  box-shadow: 0 16px 34px rgba(102, 126, 234, 0.26);
  border: 1px solid rgba(255, 255, 255, 0.18);
}
.icon {
  font-size: 18px;
}
.label {
  white-space: nowrap;
  font-size: 16px;
}

/* ====== Main ====== */
.main-content {
  grid-column: 2;
  min-width: 0;
  z-index: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  background:
      radial-gradient(circle at 18% 10%, rgba(102, 126, 234, 0.28), transparent 46%),
      radial-gradient(circle at 82% 30%, rgba(118, 75, 162, 0.22), transparent 48%),
      radial-gradient(circle at 60% 90%, rgba(102, 126, 234, 0.16), transparent 52%),
      linear-gradient(135deg, rgba(102, 126, 234, 0.22), rgba(118, 75, 162, 0.18));
}

.main-content::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 1px;
  background: rgba(255, 255, 255, 0.85);
  pointer-events: none;
}

/* ====== Toggle Button ====== */
.edge-toggle {
  position: absolute;
  left: 0;
  top: 18px;
  transform: translateX(-50%);
  z-index: 50;
  width: 42px;
  height: 42px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.55);
  background: rgba(255, 255, 255, 0.55);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.14), inset 0 1px 0 rgba(255, 255, 255, 0.8);
  cursor: pointer;
  display: grid;
  place-items: center;
  transition: transform 0.2s ease, background 0.2s ease, box-shadow 0.2s ease;
}
.edge-toggle:hover {
  background: rgba(255, 255, 255, 0.75);
  box-shadow: 0 22px 52px rgba(15, 23, 42, 0.16), inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.chev {
  width: 10px;
  height: 10px;
  border-right: 3px solid rgba(76, 29, 149, 0.75);
  border-bottom: 3px solid rgba(76, 29, 149, 0.75);
  transform: rotate(135deg);
  transition: transform 0.2s ease;
}
.chev.right {
  transform: rotate(-45deg);
}

.app-layout.sidebar-collapsed .edge-toggle {
  transform: translateX(6px);
}

/* Transition for router-view */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>