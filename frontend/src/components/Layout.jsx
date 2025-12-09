import React, { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import Footer from './Footer'
import './Layout.css'

const Layout = ({ children }) => {
  const { user, logout, validateToken } = useAuth()
  const [displayUser, setDisplayUser] = useState(user)
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
    navigate('/login')
  }

  return (
    <div className="layout">
      <nav className="navbar">
        <div className="nav-brand">
          <h2>🏫 Quản Lý Nhà Trường</h2>
        </div>
        <div className="nav-links">
          <Link
            to="/"
            className={location.pathname === '/' ? 'active' : ''}
          >
            {displayUser?.role === 'ADMIN' ? 'Trang Chủ' : 'Thông Tin Tài Khoản'}
          </Link>
          {/* Menu cho ADMIN */}
          {displayUser?.role === 'ADMIN' && (
            <>
              <Link
                to="/students"
                className={location.pathname === '/students' ? 'active' : ''}
              >
                Quản Lý Sinh Viên
              </Link>
              <Link
                to="/teachers"
                className={location.pathname === '/teachers' ? 'active' : ''}
              >
                Quản Lý Giáo Viên
              </Link>
            </>
          )}
          {/* Menu cho STUDENT */}
          {displayUser?.role === 'STUDENT' && (
            <Link
              to="/info-student"
              className={location.pathname === '/info-student' ? 'active' : ''}
            >
              Thông Tin Sinh Viên
            </Link>
          )}
          {/* Menu cho TEACHER */}
          {displayUser?.role === 'TEACHER' && (
            <Link
              to="/teacher-classes"
              className={location.pathname === '/teacher-classes' ? 'active' : ''}
            >
              Các Lớp Giảng Dạy
            </Link>
          )}
        </div>
        <div className="nav-user">
          <span className="username">
            {displayUser?.firstName && displayUser?.lastName 
              ? `${displayUser.firstName} ${displayUser.lastName}` 
              : displayUser?.username || 'User'} ({displayUser?.role || 'N/A'})
          </span>
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

