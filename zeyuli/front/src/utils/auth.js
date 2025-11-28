/**
 * 用户认证相关工具函数
 */

/**
 * 保存用户登录信息到本地存储
 * @param {Object} userInfo - 用户信息对象
 * @param {string} token - JWT token
 */
export const saveUserInfo = (userInfo, token) => {
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
  localStorage.setItem('token', token)
}

/**
 * 获取当前登录用户信息
 * @returns {Object|null} 用户信息对象或null
 */
export const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  return userInfoStr ? JSON.parse(userInfoStr) : null
}

/**
 * 获取认证token
 * @returns {string|null} token或null
 */
export const getToken = () => {
  return localStorage.getItem('token')
}

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export const isLoggedIn = () => {
  return !!getToken()
}

/**
 * 用户登出，清除本地存储的用户信息
 */
export const logout = () => {
  localStorage.removeItem('userInfo')
  localStorage.removeItem('token')
  localStorage.removeItem('sessionId') // 清除会话ID
  localStorage.removeItem('chatHistory') // 清除聊天历史
}

/**
 * 保存会话ID
 * @param {string} sessionId - 会话ID
 */
export const saveSessionId = (sessionId) => {
  localStorage.setItem('sessionId', sessionId)
}

/**
 * 获取会话ID
 * @returns {string|null} 会话ID或null
 */
export const getSessionId = () => {
  return localStorage.getItem('sessionId')
}

/**
 * 保存聊天历史
 * @param {Array} history - 聊天历史数组
 */
export const saveChatHistory = (history) => {
  localStorage.setItem('chatHistory', JSON.stringify(history))
}

/**
 * 获取聊天历史
 * @returns {Array} 聊天历史数组
 */
export const getChatHistory = () => {
  const historyStr = localStorage.getItem('chatHistory')
  return historyStr ? JSON.parse(historyStr) : []
}