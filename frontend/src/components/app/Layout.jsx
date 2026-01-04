import React, { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import Footer from './Footer'
import './Layout.css'

const Layout = ({ children }) => {
  const { user, logout, validateToken } = useAuth()
  const [displayUser, setDisplayUser] = useState(user)
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()

  // Load user info nếu có token nhưng chưa có user info đầy đủ
  useEffect(() => {
    const loadUserInfo = async () => {
      const token = localStorage.getItem('accessToken')
      if (token && (!user || !user.firstName)) {
        // Gọi validateToken từ context để cập nhật user state
        const userData = await validateToken()
        if (userData) {
          setDisplayUser(userData)
        }
      }
    }

    loadUserInfo()
  }, [user, validateToken])

  // Update displayUser khi user từ context thay đổi
  useEffect(() => {
    if (user) {
      setDisplayUser(user)
    }
  }, [user])

  const handleLogout = async () => {
    await logout()
    navigate('/app/login')
  }

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen)
  }

  const closeMobileMenu = () => {
    setMobileMenuOpen(false)
  }

  return (
    <div className="layout">
      <nav className="navbar">
        <div className="nav-brand">
          <h2>🏫 Quản Lý Nhà Trường</h2>
        </div>
        <button 
          className="mobile-menu-toggle" 
          onClick={toggleMobileMenu}
          aria-label="Toggle menu"
        >
          <span></span>
          <span></span>
          <span></span>
        </button>
        <div className={`nav-links ${mobileMenuOpen ? 'mobile-open' : ''}`}>
          <Link
            to="/app"
            className={location.pathname === '/app' ? 'active' : ''}
            onClick={closeMobileMenu}
          >
            {displayUser?.role === 'ADMIN' ? 'Trang Chủ' : 'Thông Tin Tài Khoản'}
          </Link>
          {/* Menu cho ADMIN */}
          {displayUser?.role === 'ADMIN' && (
            <>
              <Link
                to="/app/students"
                className={location.pathname === '/app/students' ? 'active' : ''}
                onClick={closeMobileMenu}
              >
                Quản Lý Sinh Viên
              </Link>
              <Link
                to="/app/teachers"
                className={location.pathname === '/app/teachers' ? 'active' : ''}
                onClick={closeMobileMenu}
              >
                Quản Lý Giáo Viên
              </Link>
            </>
          )}
          {/* Menu cho STUDENT */}
          {displayUser?.role === 'STUDENT' && (
            <>
              <Link
                to="/app/info-student"
                className={location.pathname === '/app/info-student' ? 'active' : ''}
                onClick={closeMobileMenu}
              >
                Thông Tin Sinh Viên
              </Link>
              <Link
                to="/app/profile-edit"
                className={location.pathname === '/app/profile-edit' ? 'active' : ''}
                onClick={closeMobileMenu}
              >
                Chỉnh Sửa Thông Tin
              </Link>
            </>
          )}
          {/* Menu cho TEACHER */}
          {displayUser?.role === 'TEACHER' && (
            <>
              <Link
                to="/app/teacher-classes"
                className={location.pathname === '/app/teacher-classes' ? 'active' : ''}
                onClick={closeMobileMenu}
              >
                Các Lớp Giảng Dạy
              </Link>
              <Link
                to="/app/profile-edit"
                className={location.pathname === '/app/profile-edit' ? 'active' : ''}
                onClick={closeMobileMenu}
              >
                Chỉnh Sửa Thông Tin
              </Link>
            </>
          )}
        </div>
        <div className={`nav-user ${mobileMenuOpen ? 'mobile-open' : ''}`}>
          <span className="username">
            {displayUser?.firstName && displayUser?.lastName 
              ? `${displayUser.firstName} ${displayUser.lastName}` 
              : displayUser?.username || 'User'} ({displayUser?.role || 'N/A'})
          </span>
          <Link to="/" className="home-btn" onClick={closeMobileMenu}>
            Trở lại trang chủ
          </Link>
          <button onClick={handleLogout} className="logout-btn">
            Đăng xuất
          </button>
        </div>
      </nav>
      <main className="main-content">{children}</main>
      <Footer />
    </div>
  )
}

export default Layout

