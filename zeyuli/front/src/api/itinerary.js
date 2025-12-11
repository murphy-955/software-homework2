import request from '../utils/request'

// MOCK模式开关
const MOCK_MODE = true;

// 按预算规划行程
export const planByBudget = (params, data) => {
    if (MOCK_MODE) {
        console.log('Mock planByBudget:', params);
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({
                    title: `${params.city || '北京'} ${params.days || 3}日游`,
                    days: Array.from({ length: params.days || 3 }, (_, i) => ({
                        day: i + 1,
                        date: new Date(Date.now() + i * 86400000).toISOString().split('T')[0],
                        activities: [
                            { time: "09:00", title: "景点A", description: "参观著名景点", cost: 50 },
                            { time: "12:00", title: "午餐", description: "当地特色美食", cost: 80 },
                            { time: "14:00", title: "景点B", description: "游览历史古迹", cost: 40 },
                            { time: "18:00", title: "晚餐", description: "夜市小吃", cost: 60 }
                        ]
                    })),
                    totalBudget: params.budget || 2000,
                    estimatedCost: (params.budget || 2000) * 0.8
                });
            }, 1500); // 模拟网络延迟
        });
    }
    return request({
        url: '/itinerary/plan-by-budget',
        method: 'POST',
        params,
        data
    })
}

// 生成旅行计划（流式）
export const planItinerary = (params, onChunk) => {
    return new Promise((resolve, reject) => {
        // 获取token
        const token = localStorage.getItem('token')

        // 构建请求头
        const headers = {
            'Content-Type': 'application/json'
        }

        // 如果token存在，添加Authorization头
        if (token) {
            headers.Authorization = `Bearer ${token}`
        }

        // 使用原生fetch API实现流式接收
        // todo 注意要使用baseUrl
        fetch(`http://localhost:8080/deekseek/planItinerary?${new URLSearchParams(params)}`, {
            method: 'GET',
            headers
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`)
                }

                // 获取ReadableStream
                const reader = response.body.getReader()
                const decoder = new TextDecoder('utf-8')
                let result = ''

                // 递归读取流数据
                const readStream = () => {
                    return reader.read().then(({done, value}) => {
                        if (done) {
                            resolve(result)
                            return
                        }

                        // 解码并处理数据
                        const chunk = decoder.decode(value, {stream: true})
                        result += chunk

                        // 实时回调处理数据
                        if (onChunk && typeof onChunk === 'function') {
                            onChunk(chunk)
                        }

                        // 继续读取
                        return readStream()
                    })
                }

                return readStream()
            })
            .catch(error => {
                reject(error)
            })
    })
}

// 根据条件调整行程
export const adjustByCondition = (params, data) => {
    return request({
        url: '/itinerary/adjust-by-condition',
        method: 'POST',
        params,
        data
    })
}

// 生成人格化行程
export const generatePersonality = (params) => {
    return request({
        url: '/itinerary/generate-personality',
        method: 'GET',
        params
    })
}

// 获取学生专属行程
export const studentItinerary = (params) => {
    return request({
        url: '/itinerary/student-itinerary',
        method: 'GET',
        params
    })
}

// 生成搭子行程
export const generateCompanion = (params) => {
    return request({
        url: '/itinerary/generate-companion',
        method: 'GET',
        params
    })
}

// 计算行程费用
export const calculateCost = (params, data) => {
    return request({
        url: '/itinerary/calculate-cost',
        method: 'POST',
        params,
        data
    })
}

// 优化景点选择
export const optimizeAttractions = (params) => {
    return request({
        url: '/itinerary/optimize-attractions',
        method: 'GET',
        params
    })
}

// 优化交通方式
export const optimizeTransportation = (params) => {
    return request({
        url: '/itinerary/optimize-transportation',
        method: 'GET',
        params
    })
}
