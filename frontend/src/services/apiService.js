import axios from 'axios'
import API_CONFIG from '../config/api'

// Tạo axios instance
const createAxiosInstance = (baseURL) => {
  const instance = axios.create({
    baseURL,
    timeout: API_CONFIG.TIMEOUT,
    headers: {
      'Content-Type': 'application/json',
      'Accept-Language': 'vi',
    },
  })

  // Request interceptor để thêm token
  instance.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('accessToken')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      } else {
        console.warn('No access token found in localStorage for request:', config.url)
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )

  // Response interceptor để xử lý lỗi
  instance.interceptors.response.use(
    (response) => {
      return response.data
    },
    (error) => {
      // Chỉ logout nếu là 401 từ Security service (authentication error)
      // Không logout nếu là CORS error hoặc network error (không có error.response)
      // Không logout nếu là 401 từ QLSV/QLGV (có thể là authorization error, không phải authentication)
      if (error.response?.status === 401) {
        const requestUrl = error.config?.url || ''
        const baseURL = error.config?.baseURL || ''
        
        // Chỉ logout nếu lỗi 401 từ Security service (auth endpoints)
        // Không logout nếu lỗi từ QLSV/QLGV (có thể do thiếu quyền, không phải token invalid)
        if (baseURL.includes('security') || requestUrl.includes('/auth/')) {
          console.warn('401 Unauthorized from Security service - token may be invalid, logging out')
          localStorage.removeItem('accessToken')
          localStorage.removeItem('refreshToken')
          window.location.href = '/login'
        } else {
          // 401 từ QLSV/QLGV - có thể là authorization error (thiếu quyền), không logout
          console.warn('401 Unauthorized from QLSV/QLGV - may be authorization error, not logging out')
        }
      } else if (!error.response) {
        // Network error hoặc CORS error - không có response từ server
        // Không logout vì đây là lỗi kết nối, không phải authentication error
        console.warn('Network error or CORS error - no response from server:', error.message)
      }
      return Promise.reject(error)
    }
  )

  return instance
}

// Tạo các instance cho từng service
const securityApi = createAxiosInstance(API_CONFIG.SECURITY_BASE_URL)
const qlsvApi = createAxiosInstance(API_CONFIG.QLSV_BASE_URL)
const qlgvApi = createAxiosInstance(API_CONFIG.QLGV_BASE_URL)

const apiService = {
  // ========== AUTH APIs ==========
  login: async (username, password) => {
    const response = await securityApi.post('/auth/login', { username, password })
    if (response.data?.accessToken) {
      localStorage.setItem('accessToken', response.data.accessToken)
      localStorage.setItem('refreshToken', response.data.refreshToken)
    }
    return response
  },

  register: async (userData) => {
    const response = await securityApi.post('/auth/register', userData)
    if (response.data?.accessToken) {
      localStorage.setItem('accessToken', response.data.accessToken)
      localStorage.setItem('refreshToken', response.data.refreshToken)
    }
    return response
  },

  logout: async () => {
    try {
      await securityApi.post('/auth/logout')
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
    }
  },

  validateToken: async () => {
    const response = await securityApi.get('/auth/validate')
    // Response structure: { status, message, modelName, errors, data }
    // data chứa UserDto với firstName, lastName, etc.
    return response
  },

  // ========== STUDENTS APIs ==========
  getStudentByUserId: async (userId, forceRefresh = false) => {
    try {
      // Chỉ thêm timestamp để bypass cache, không xóa cache
      const url = forceRefresh 
        ? `/students/${userId}?_t=${Date.now()}` 
        : `/students/${userId}`
      const response = await qlsvApi.get(url)
      console.log('getStudentByUserId response:', response)
      return response
    } catch (error) {
      console.error('getStudentByUserId error:', error)
      throw error
    }
  },

  getStudents: async (forceRefresh = false) => {
    try {
      // Chỉ thêm timestamp để bypass cache, không xóa cache
      const url = forceRefresh 
        ? `/students?_t=${Date.now()}` 
        : '/students'
      const response = await qlsvApi.get(url)
      console.log('getStudents response:', response)
      return response
    } catch (error) {
      console.error('getStudents error:', error)
      console.error('Error response:', error.response?.data)
      throw error
    }
  },

  updateStudent: async (id, data) => {
    const response = await qlsvApi.put(`/students/${id}`, data)
    // Sau khi update thành công, có thể cần đợi một chút để cache được clear
    await new Promise(resolve => setTimeout(resolve, 100))
    return response
  },

  deleteStudents: async (userIds) => {
    return await qlsvApi.delete('/students', { data: userIds })
  },

  disableUsers: async (userIds) => {
    // Gọi API disable từ Security service
    return await securityApi.patch('/auth/internal/users/disable', userIds)
  },

  adminUpdateUser: async (userId, role, permissions, username, password, status) => {
    // Đảm bảo permissions là array và không null
    const permissionsArray = Array.isArray(permissions) ? permissions : (permissions ? [permissions] : [])
    
    const requestBody = {
      userId: userId,
      role: role,
      permissions: permissionsArray.length > 0 ? permissionsArray : null
    }
    
    // Chỉ thêm các field nếu có giá trị
    if (username && username.trim() !== '') {
      requestBody.username = username.trim()
    }
    if (password && password.trim() !== '') {
      requestBody.password = password.trim()
    }
    // Status luôn được gửi (required) - đảm bảo có giá trị
    requestBody.status = status || 'ENABLED'
    
    const response = await securityApi.put(`/auth/users/${userId}/admin-update`, requestBody)
    return response
  },

  // ========== TEACHERS APIs ==========
  getTeachers: async (forceRefresh = false) => {
    try {
      // Chỉ thêm timestamp để bypass cache, không xóa cache
      const url = forceRefresh 
        ? `/teachers?_t=${Date.now()}` 
        : '/teachers'
      const response = await qlgvApi.get(url)
      console.log('getTeachers response:', response)
      return response
    } catch (error) {
      console.error('getTeachers error:', error)
      console.error('Error response:', error.response?.data)
      throw error
    }
  },

  getTeacherByUserId: async (userId, forceRefresh = false) => {
    try {
      // Chỉ thêm timestamp để bypass cache, không xóa cache
      const url = forceRefresh
        ? `/teachers/${userId}?_t=${Date.now()}` 
        : `/teachers/${userId}`
      const response = await qlgvApi.get(url)
      return response
    } catch (error) {
      console.error('getTeacherByUserId error:', error)
      throw error
    }
  },

  updateTeacher: async (id, data) => {
    return await qlgvApi.put(`/teachers/${id}`, data)
  },

  deleteTeachers: async (userIds) => {
    return await qlgvApi.delete('/teachers', { data: userIds })
  },

  getStudentsByClassManaging: async (classManaging, forceRefresh = false) => {
    try {
      // Chỉ thêm timestamp để bypass cache, không xóa cache
      const url = forceRefresh
        ? `/teachers/${encodeURIComponent(classManaging)}/students?_t=${Date.now()}`
        : `/teachers/${encodeURIComponent(classManaging)}/students`
      const response = await qlgvApi.get(url)
      return response
    } catch (error) {
      console.error('getStudentsByClassManaging error:', error)
      throw error
    }
  },

  // ========== PAGINATION APIs ==========
  getStudentsPaged: async (page = 0, size = 10, sortBy = 'id', sortDirection = 'asc', schoolClass = null, forceRefresh = false) => {
    try {
      let url = `/students/paged?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`
      if (schoolClass) {
        url += `&schoolClass=${encodeURIComponent(schoolClass)}`
      }
      if (forceRefresh) {
        url += `&_t=${Date.now()}`
      }
      const response = await qlsvApi.get(url)
      return response
    } catch (error) {
      console.error('getStudentsPaged error:', error)
      throw error
    }
  },

  getTeachersPaged: async (page = 0, size = 10, sortBy = 'id', sortDirection = 'asc', forceRefresh = false) => {
    try {
      let url = `/teachers/paged?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`
      if (forceRefresh) {
        url += `&_t=${Date.now()}`
      }
      const response = await qlgvApi.get(url)
      return response
    } catch (error) {
      console.error('getTeachersPaged error:', error)
      throw error
    }
  },

  // ========== FILTER APIs ==========
  filterTeachers: async (filters = {}) => {
    try {
      const params = new URLSearchParams()
      
      // Chỉ thêm params nếu có giá trị
      if (filters.id !== undefined && filters.id !== null && filters.id !== '') {
        params.append('id', filters.id)
      }
      if (filters.firstName) {
        params.append('firstName', filters.firstName)
      }
      if (filters.lastName) {
        params.append('lastName', filters.lastName)
      }
      if (filters.age !== undefined && filters.age !== null && filters.age !== '') {
        params.append('age', filters.age)
      }
      if (filters.gender) {
        params.append('gender', filters.gender)
      }
      if (filters.email) {
        params.append('email', filters.email)
      }
      if (filters.phoneNumber) {
        params.append('phoneNumber', filters.phoneNumber)
      }
      if (filters.classManaging) {
        params.append('classManaging', filters.classManaging)
      }
      if (filters.department) {
        params.append('department', filters.department)
      }
      
      const url = `/teachers/filter?${params.toString()}`
      const response = await qlgvApi.get(url)
      return response
    } catch (error) {
      console.error('filterTeachers error:', error)
      throw error
    }
  },

  filterStudents: async (filters = {}) => {
    try {
      const params = new URLSearchParams()
      
      // Chỉ thêm params nếu có giá trị
      if (filters.id !== undefined && filters.id !== null && filters.id !== '') {
        params.append('id', filters.id)
      }
      if (filters.firstName) {
        params.append('firstName', filters.firstName)
      }
      if (filters.lastName) {
        params.append('lastName', filters.lastName)
      }
      if (filters.age !== undefined && filters.age !== null && filters.age !== '') {
        params.append('age', filters.age)
      }
      if (filters.gender) {
        params.append('gender', filters.gender)
      }
      if (filters.email) {
        params.append('email', filters.email)
      }
      if (filters.phoneNumber) {
        params.append('phoneNumber', filters.phoneNumber)
      }
      if (filters.score !== undefined && filters.score !== null && filters.score !== '') {
        params.append('score', filters.score)
      }
      if (filters.schoolClass) {
        params.append('schoolClass', filters.schoolClass)
      }
      if (filters.major) {
        params.append('major', filters.major)
      }
      if (filters.graduate !== undefined && filters.graduate !== null && filters.graduate !== '') {
        // Convert boolean to string for URL params
        params.append('graduate', String(filters.graduate))
      }
      
      const url = `/students/filter?${params.toString()}`
      const response = await qlsvApi.get(url)
      return response
    } catch (error) {
      console.error('filterStudents error:', error)
      throw error
    }
  },
}

export default apiService

