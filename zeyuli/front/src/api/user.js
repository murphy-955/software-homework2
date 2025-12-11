import request from '../utils/request'

// MOCK模式开关
const MOCK_MODE = true;

// 用户登录
export const login = (data) => {
  if (MOCK_MODE) {
    console.log('Mock Login:', data);
    return Promise.resolve({
      code: 200,
      msg: 'success',
      token: 'mock-token-123456',
      data: {
        token: 'mock-token-123456',
        userInfo: {
          id: 1,
          username: data.username || 'admin',
          avatar: ''
        }
      }
    })
  }
  return request({
    url: '/user/login',
    method: 'POST',
    data
  })
}

// 用户注册
export const register = (data) => {
  if (MOCK_MODE) {
    return Promise.resolve({
      code: 200,
      msg: 'success',
      data: {
        id: 1,
        username: data.username
      }
    })
  }
  return request({
    url: '/user/register',
    method: 'POST',
    data
  })
}
