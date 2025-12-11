<template>
  <div class="map-view">
    <div id="container" class="map-container"></div>
    
    <!-- 悬浮控制面板 -->
    <div class="control-panel card">
      <div class="panel-header">
        <h3>行程路线预览</h3>
        <div class="status-badge">进行中</div>
      </div>
      
      <div class="route-list">
        <div class="timeline">
          <div v-for="(point, index) in mockPoints" :key="index" class="timeline-item">
            <div class="timeline-marker" :style="{ backgroundColor: point.color }"></div>
            <div class="timeline-content">
              <div class="point-header">
                <span class="time">Day 1</span>
                <span class="location">{{ point.name }}</span>
              </div>
              <div class="point-desc">{{ point.desc }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="action-bar">
        <button class="btn btn-primary w-full" @click="startNavigation">
          <span class="icon">🧭</span> 开始导航
        </button>
      </div>
    </div>

    <!-- 地图加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <p>地图加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AMapLoader from '@amap/amap-jsapi-loader'

const router = useRouter()
const mapLoaded = ref(false)
const loading = ref(true)
let map = null

// Mock Data
const mockPoints = [
  { name: '故宫博物院', desc: '09:00 - 12:00 游览', color: '#ef4444', position: [116.397428, 39.90923] },
  { name: '四季民福烤鸭', desc: '12:30 - 14:00 午餐', color: '#f59e0b', position: [116.405285, 39.904989] },
  { name: '景山公园', desc: '14:30 - 16:30 登山', color: '#10b981', position: [116.398746, 39.914896] }
]

const startNavigation = () => {
  alert('导航功能开发中...')
}

const initMap = () => {
  AMapLoader.load({
    key: "996f69848107373e41c32af01c5de8ef",
    version: "2.0",
    plugins: ['AMap.Scale', 'AMap.ToolBar', 'AMap.ControlBar', 'AMap.MouseTool', 'AMap.MapType', 'AMap.HawkEye'],
  }).then((AMap) => {
    map = new AMap.Map("container", {
      viewMode: "3D",
      zoom: 15,
      center: [116.397428, 39.90923],
      zooms: [1, 17], // Added zoom levels as requested
    });

    // 添加控件
    map.addControl(new AMap.Scale())
    map.addControl(new AMap.ToolBar())
    map.addControl(new AMap.ControlBar())
    map.addControl(new AMap.MapType())

    // 添加标记点
    mockPoints.forEach(point => {
      const marker = new AMap.Marker({
        position: point.position,
        title: point.name,
        animation: 'AMAP_ANIMATION_DROP'
      })
      map.add(marker)
    })

    // 绘制路线
    const path = mockPoints.map(p => p.position)
    const polyline = new AMap.Polyline({
      path: path,
      strokeColor: "#667eea", 
      strokeWeight: 6,
      strokeOpacity: 0.9,
      zIndex: 50,
      bubble: true,
      showDir: true
    })
    map.add(polyline)

    // 自动缩放视野
    map.setFitView()
    
    loading.value = false
    mapLoaded.value = true

  }).catch(e => {
    console.error(e)
    loading.value = false
  })
}

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  if (map) {
    map.destroy()
  }
})
</script>

<style scoped>
.map-view {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 100%;
}

.control-panel {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 320px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a202c;
}

.status-badge {
  background: #ebf8ff;
  color: #3182ce;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.timeline {
  position: relative;
  padding-left: 20px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 10px;
  bottom: 10px;
  width: 2px;
  background: #e2e8f0;
}

.timeline-item {
  position: relative;
  margin-bottom: 24px;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-marker {
  position: absolute;
  left: -20px;
  top: 4px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  z-index: 1;
}

.timeline-content {
  background: #f7fafc;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.2s;
}

.timeline-content:hover {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.point-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.time {
  font-size: 0.8rem;
  color: #718096;
}

.location {
  font-weight: 600;
  color: #2d3748;
}

.point-desc {
  font-size: 0.85rem;
  color: #718096;
}

.action-bar {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.loading-overlay {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #3182ce;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
