import React, { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import './Auth.css'

const Login = () => {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [loginSuccess, setLoginSuccess] = useState(false)
  const { login, user } = useAuth()
  const navigate = useNavigate()

  // Navigate khi user đã có đầy đủ thông tin (có firstName)
  useEffect(() => {
    if (loginSuccess && user && user.firstName) {
      navigate('/')
      setLoginSuccess(false)
    }
  }, [loginSuccess, user, navigate])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    setLoginSuccess(false)

    try {
      const result = await login(username, password)
      if (result.success) {
        // Đánh dấu login thành công, useEffect sẽ xử lý navigate khi user có firstName
        setLoginSuccess(true)
        // Nếu user đã có firstName ngay (từ result.user), navigate luôn
        if (result.user && result.user.firstName) {
          // User đã có đầy đủ, có thể navigate ngay
          // Nhưng để useEffect xử lý để đảm bảo
        }
      } else {
        setError(result.error)
        setLoading(false)
      }
    } catch (error) {
      console.error('Login error:', error)
      setError('Đăng nhập thất bại')
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>Đăng Nhập</h1>
        <p className="auth-subtitle">Hệ thống Quản Lý Nhà Trường</p>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">Tên đăng nhập</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              placeholder="Nhập tên đăng nhập"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Mật khẩu</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="Nhập mật khẩu"
            />
          </div>

          <button type="submit" className="auth-button" disabled={loading}>
            {loading ? 'Đang đăng nhập...' : 'Đăng Nhập'}
          </button>
        </form>

        <p className="auth-footer">
          Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link>
        </p>
      </div>
    </div>
  )
}

export default Login

