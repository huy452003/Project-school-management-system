import React, { useState } from 'react'
import { useAuth } from '../../contexts/AuthContext'
import { useNavigate } from 'react-router-dom'
import './Home.css'

const Home = () => {
  const { user, validateToken } = useAuth()
  const navigate = useNavigate()
  const [showBanner, setShowBanner] = useState(true)
  const [refreshing, setRefreshing] = useState(false)

  // Kiểm tra xem thông tin có đầy đủ không
  const isInfoComplete = user?.firstName && user?.lastName && user?.age && user?.gender && user?.birth && user?.phoneNumber && user?.email

  // Chỉ hiển thị banner cho student và khi thông tin chưa đầy đủ
  const shouldShowBanner = user?.role === 'STUDENT' && !isInfoComplete && showBanner

  const handleUpdateInfo = () => {
    // Có thể điều hướng đến trang cập nhật hoặc mở modal
    // Tạm thời scroll đến phần thông tin
    const infoCard = document.querySelector('.user-info-card')
    if (infoCard) {
      infoCard.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }

  const handleRefresh = async () => {
    try {
      setRefreshing(true)
      await validateToken()
      // Có thể thêm thông báo thành công nếu cần
    } catch (error) {
      console.error('Refresh error:', error)
    } finally {
      setRefreshing(false)
    }
  }

  return (
    <div className="dashboard">
      {shouldShowBanner && (
        <div className="update-info-banner">
          <div className="banner-content">
            <div className="banner-icon">📝</div>
            <div className="banner-text">
              <h3>Cập nhật thông tin cá nhân</h3>
              <p>Vui lòng cập nhật đầy đủ thông tin cá nhân để sử dụng đầy đủ các tính năng của hệ thống.</p>
            </div>
            <div className="banner-actions">
              <button onClick={handleUpdateInfo} className="banner-btn-update">
                Cập nhật ngay
              </button>
              <button onClick={() => setShowBanner(false)} className="banner-btn-close" title="Đóng">
                ✕
              </button>
            </div>
          </div>
        </div>
      )}

      <p className="welcome-text">
        Chào mừng, {user?.role === 'ADMIN' ? 'Quản Trị Viên' : user?.role === 'TEACHER' ? 'Giáo Viên' : 'Sinh Viên'} <strong>
          {user?.firstName && user?.lastName 
            ? `${user.firstName} ${user.lastName}` 
            : user?.username || 'User'}
        </strong>!
      </p>

      <div className="user-info-card">
        <div className="card-header">
          <h2>📋 Thông Tin Tài Khoản</h2>
          <button 
            onClick={handleRefresh} 
            className="refresh-btn"
            disabled={refreshing}
            title="Làm mới thông tin"
          >
            {refreshing ? '⏳' : '🔄'} {refreshing ? 'Đang tải...' : 'Làm mới'}
          </button>
        </div>
        
        <div className="info-sections">
          {/* Thông tin cá nhân */}
          <div className="info-section">
            <h3 className="section-title-home">
              <span className="section-icon">👤</span>
              Thông Tin Cá Nhân
            </h3>
            <div className="info-grid">
              <div className="info-item">
                <div className="info-icon">🆔</div>
                <div className="info-content">
                  <span className="info-label">Mã người dùng</span>
                  <span className="info-value">#{user?.userId || 'N/A'}</span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">👤</div>
                <div className="info-content">
                  <span className="info-label">Tên đăng nhập</span>
                  <span className="info-value">{user?.username || 'N/A'}</span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">📝</div>
                <div className="info-content">
                  <span className="info-label">Họ và tên</span>
                  <span className="info-value">
                    {user?.firstName && user?.lastName 
                      ? `${user.firstName} ${user.lastName}` 
                      : 'Chưa có thông tin'}
                  </span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">🎂</div>
                <div className="info-content">
                  <span className="info-label">Tuổi</span>
                  <span className="info-value">{user?.age || 'N/A'} tuổi</span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">⚧️</div>
                <div className="info-content">
                  <span className="info-label">Giới tính</span>
                  <span className="info-value">
                    {user?.gender === 'NAM' ? 'Nam' : user?.gender === 'NU' ? 'Nữ' : 'N/A'}
                  </span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">📅</div>
                <div className="info-content">
                  <span className="info-label">Ngày sinh</span>
                  <span className="info-value">
                    {user?.birth 
                      ? (() => {
                          // Backend trả về format "dd-MM-yyyy", parse và format lại
                          try {
                            const [day, month, year] = user.birth.split('-')
                            if (day && month && year) {
                              return new Date(`${year}-${month}-${day}`).toLocaleDateString('vi-VN', {
                                day: '2-digit',
                                month: '2-digit',
                                year: 'numeric'
                              })
                            }
                            return user.birth
                          } catch {
                            return user.birth
                          }
                        })()
                      : 'Chưa có'}
                  </span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">📞</div>
                <div className="info-content">
                  <span className="info-label">Số điện thoại</span>
                  <span className="info-value">{user?.phoneNumber || 'Chưa có'}</span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">📧</div>
                <div className="info-content">
                  <span className="info-label">Email</span>
                  <span className="info-value">{user?.email || 'Chưa có'}</span>
                </div>
              </div>
            </div>
          </div>

          {/* Thông tin hệ thống */}
          <div className="info-section">
            <h3 className="section-title-home">
              <span className="section-icon">⚙️</span>
              Thông Tin Hệ Thống
            </h3>
            <div className="info-grid">
              <div className="info-item">
                <div className="info-icon">🎭</div>
                <div className="info-content">
                  <span className="info-label">Loại tài khoản</span>
                  <span className="info-value badge badge-type">
                    {user?.type || 'N/A'}
                  </span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">👑</div>
                <div className="info-content">
                  <span className="info-label">Vai trò</span>
                  <span className="info-value badge badge-role">
                    {user?.role === 'ADMIN' ? 'Quản Trị Viên' : 
                     user?.role === 'TEACHER' ? 'Giáo Viên' : 
                     user?.role === 'STUDENT' ? 'Sinh Viên' : user?.role || 'N/A'}
                  </span>
                </div>
              </div>
              <div className="info-item">
                <div className="info-icon">📊</div>
                <div className="info-content">
                  <span className="info-label">Trạng thái</span>
                  <span className={`info-value badge badge-status badge-${user?.status?.toLowerCase() || 'unknown'}`}>
                    {user?.status === 'ENABLED' ? 'Đang hoạt động' :
                     user?.status === 'DISABLED' ? 'Đã vô hiệu hóa' :
                     user?.status === 'PENDING' ? 'Đang chờ' :
                     user?.status === 'FAILED' ? 'Thất bại' : user?.status || 'N/A'}
                  </span>
                </div>
              </div>
              {user?.permissions && user.permissions.size > 0 && (
                <div className="info-item info-item-full">
                  <div className="info-icon">🔐</div>
                  <div className="info-content">
                    <span className="info-label">Quyền hạn</span>
                    <div className="permissions-list">
                      {Array.from(user.permissions).map((permission, index) => (
                        <span key={index} className="permission-badge">
                          {permission}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Home

