import React, { useState, useEffect } from 'react'
import { useAuth } from '../contexts/AuthContext'
import apiService from '../services/apiService'
import './ProfileEdit.css'

const ProfileEdit = () => {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [entityId, setEntityId] = useState(null)
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    age: '',
    gender: 'MALE',
    birth: '',
    phoneNumber: '',
    email: ''
  })

  useEffect(() => {
    if (user?.userId) {
      loadProfileInfo()
    }
  }, [user])

  const loadProfileInfo = async () => {
    try {
      setLoading(true)
      setError('')
      
      if (!user?.userId) {
        setError('Không tìm thấy thông tin người dùng')
        return
      }
      
      let response
      if (user.role === 'STUDENT') {
        response = await apiService.getStudentByUserId(user.userId, false)
      } else if (user.role === 'TEACHER') {
        response = await apiService.getTeacherByUserId(user.userId, false)
      } else {
        setError('Bạn không có quyền truy cập trang này')
        return
      }
      
      const entityData = response?.data?.data || response?.data
      
      if (entityData) {
        // Lấy id của entity (student hoặc teacher)
        setEntityId(entityData.id)
        
        // Lấy thông tin user từ entity
        const userInfo = entityData.user || {}
        
        // Format birth date từ LocalDate sang input date format (yyyy-MM-dd)
        // Backend trả về format "dd-MM-yyyy" (theo @JsonFormat pattern trong UpdateUserDto)
        let birthDate = ''
        if (userInfo.birth) {
          try {
            if (typeof userInfo.birth === 'string') {
              // Backend trả về format "dd-MM-yyyy" (ví dụ: "01-01-2000")
              if (userInfo.birth.includes('-')) {
                const parts = userInfo.birth.split('-').map(p => p.trim())
                if (parts.length === 3) {
                  const firstPart = parts[0]
                  const secondPart = parts[1]
                  const thirdPart = parts[2]
                  
                  // Kiểm tra: nếu phần thứ 3 (index 2) có 4 chữ số và hợp lệ (1900-2100)
                  // thì chắc chắn là format dd-MM-yyyy, cần convert sang yyyy-MM-dd
                  if (thirdPart.length === 4 && parseInt(thirdPart) >= 1900 && parseInt(thirdPart) <= 2100) {
                    // Kiểm tra xem có phải format yyyy-MM-dd không (year ở đầu)
                    if (firstPart.length === 4 && parseInt(firstPart) >= 1900 && parseInt(firstPart) <= 2100) {
                      // Format yyyy-MM-dd (đã đúng format)
                      birthDate = userInfo.birth
                    } else {
                      // Format dd-MM-yyyy -> yyyy-MM-dd
                      // parts[0] = day, parts[1] = month, parts[2] = year
                      birthDate = `${thirdPart}-${secondPart.padStart(2, '0')}-${firstPart.padStart(2, '0')}`
                    }
                  } else {
                    // Thử parse như ISO string
                    const date = new Date(userInfo.birth)
                    if (!isNaN(date.getTime())) {
                      birthDate = date.toISOString().split('T')[0]
                    }
                  }
                } else {
                  // Thử parse như ISO string
                  const date = new Date(userInfo.birth)
                  if (!isNaN(date.getTime())) {
                    birthDate = date.toISOString().split('T')[0]
                  }
                }
              } else {
                // Thử parse như ISO string hoặc timestamp
                const date = new Date(userInfo.birth)
                if (!isNaN(date.getTime())) {
                  birthDate = date.toISOString().split('T')[0]
                }
              }
            } else if (userInfo.birth instanceof Date) {
              // Nếu là Date object
              birthDate = userInfo.birth.toISOString().split('T')[0]
            } else if (Array.isArray(userInfo.birth) && userInfo.birth.length === 3) {
              // Nếu là array [year, month, day] (LocalDate serialized)
              birthDate = `${userInfo.birth[0]}-${String(userInfo.birth[1]).padStart(2, '0')}-${String(userInfo.birth[2]).padStart(2, '0')}`
            } else {
              // Thử parse như ISO string hoặc timestamp
              const date = new Date(userInfo.birth)
              if (!isNaN(date.getTime())) {
                birthDate = date.toISOString().split('T')[0]
              }
            }
          } catch (error) {
            console.error('Error parsing birth date:', error, 'birth value:', userInfo.birth)
            // Fallback: thử parse như Date
            try {
              const date = new Date(userInfo.birth)
              if (!isNaN(date.getTime())) {
                birthDate = date.toISOString().split('T')[0]
              }
            } catch (e) {
              console.error('Fallback date parsing also failed:', e)
            }
          }
        }
        
        // Debug log để kiểm tra - luôn hiển thị để user biết
        console.log('ProfileEdit - Raw birth from API:', userInfo.birth, '-> Parsed birthDate for input:', birthDate)
        
        setFormData({
          firstName: userInfo.firstName || '',
          lastName: userInfo.lastName || '',
          age: userInfo.age || '',
          gender: userInfo.gender || 'MALE',
          birth: birthDate,
          phoneNumber: userInfo.phoneNumber || '',
          email: userInfo.email || ''
        })
      } else {
        setError('Không tìm thấy thông tin')
      }
    } catch (err) {
      console.error('Load profile info error:', err)
      setError(err.response?.data?.message || 'Không thể tải thông tin')
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
    // Clear error khi user bắt đầu nhập
    if (error) setError('')
    if (success) setSuccess('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    if (!entityId) {
      setError('Không tìm thấy ID của entity')
      return
    }
    
    try {
      setSaving(true)
      setError('')
      setSuccess('')
      
      // Format birth date từ yyyy-MM-dd sang dd-MM-yyyy (format mà backend mong đợi)
      let formattedBirth = formData.birth
      if (formData.birth) {
        const parts = formData.birth.split('-')
        if (parts.length === 3) {
          formattedBirth = `${parts[2]}-${parts[1]}-${parts[0]}`
        }
      }
      
      // Chuẩn bị data theo UpdateEntityModel structure
      const updateData = {
        user: {
          userId: user.userId,
          firstName: formData.firstName.trim(),
          lastName: formData.lastName.trim(),
          age: parseInt(formData.age),
          gender: formData.gender,
          birth: formattedBirth,
          phoneNumber: formData.phoneNumber.trim(),
          email: formData.email.trim()
        }
      }
      
      let response
      if (user.role === 'STUDENT') {
        response = await apiService.updateStudent(entityId, updateData)
      } else if (user.role === 'TEACHER') {
        response = await apiService.updateTeacher(entityId, updateData)
      }
      
      setSuccess('Cập nhật thông tin thành công!')
      
      // Reload lại thông tin sau khi update
      setTimeout(() => {
        loadProfileInfo()
      }, 500)
      
    } catch (err) {
      console.error('Update profile error:', err)
      setError(err.response?.data?.message || 'Không thể cập nhật thông tin')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="profile-edit-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Đang tải thông tin...</p>
        </div>
      </div>
    )
  }

  if (user?.role !== 'STUDENT' && user?.role !== 'TEACHER') {
    return (
      <div className="profile-edit-page">
        <div className="error-container">
          <div className="error-icon">⚠️</div>
          <h2>Không có quyền truy cập</h2>
          <p>Trang này chỉ dành cho sinh viên và giáo viên.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="profile-edit-page">
      <div className="page-header">
        <h1>✏️ Chỉnh Sửa Thông Tin Cá Nhân</h1>
      </div>

      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}

      <div className="profile-edit-container">
        <form onSubmit={handleSubmit} className="profile-edit-form">
          <div className="form-section">
            <h3>Thông Tin Cá Nhân</h3>
            
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="firstName">Họ *</label>
                <input
                  type="text"
                  id="firstName"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  required
                  placeholder="Nhập họ"
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="lastName">Tên *</label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  required
                  placeholder="Nhập tên"
                />
              </div>
            </div>

            <div className="form-row form-row-three">
              <div className="form-group form-group-small">
                <label htmlFor="age">Tuổi *</label>
                <input
                  type="number"
                  id="age"
                  name="age"
                  value={formData.age}
                  onChange={handleChange}
                  required
                  min="1"
                  max="99"
                  placeholder="Nhập tuổi"
                />
              </div>
              
              <div className="form-group form-group-small">
                <label htmlFor="gender">Giới tính *</label>
                <select
                  id="gender"
                  name="gender"
                  value={formData.gender}
                  onChange={handleChange}
                  required
                >
                  <option value="MALE">Nam</option>
                  <option value="FEMALE">Nữ</option>
                  <option value="OTHER">Khác</option>
                </select>
              </div>

              <div className="form-group form-group-medium">
                <label htmlFor="birth">Ngày sinh *</label>
                <input
                  type="date"
                  id="birth"
                  name="birth"
                  value={formData.birth}
                  onChange={handleChange}
                  required
                  max={new Date().toISOString().split('T')[0]}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="phoneNumber">Số điện thoại *</label>
                <input
                  type="tel"
                  id="phoneNumber"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  onChange={handleChange}
                  required
                  minLength="10"
                  maxLength="11"
                  placeholder="Nhập số điện thoại (10-11 số)"
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="email">Email *</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  placeholder="Nhập email"
                />
              </div>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={loadProfileInfo}
              className="btn btn-secondary"
              disabled={saving}
            >
              🔄 Làm mới
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={saving}
            >
              {saving ? '⏳ Đang lưu...' : '💾 Lưu thay đổi'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default ProfileEdit

