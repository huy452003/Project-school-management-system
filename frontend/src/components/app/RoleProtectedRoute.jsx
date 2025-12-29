import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'

/**
 * Component bảo vệ route theo role
 * @param {React.ReactNode} children - Component con cần bảo vệ
 * @param {string[]} allowedRoles - Mảng các role được phép truy cập (ví dụ: ['ADMIN'])
 */
const RoleProtectedRoute = ({ children, allowedRoles = [] }) => {
  const { user, loading } = useAuth()

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <div>Đang tải...</div>
      </div>
    )
  }

  // Nếu chưa đăng nhập, chuyển về login
  if (!user) {
    return <Navigate to="/app/login" replace />
  }

  // Nếu user không có role trong danh sách allowed, chuyển về trang chủ
  if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
    return <Navigate to="/app" replace />
  }

  return children
}

export default RoleProtectedRoute

