import React from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import logoImage from '../../img/logo-school.png'
import './LandingHeader.css'

const LandingHeader = () => {
  const { user } = useAuth()

  return (
    <header className="landing-header">
      <div className="header-main">
        <div className="container">
          <div className="logo-section">
            <div className="logo">
              <img src={logoImage} alt="STU" />
            </div>
          </div>
          <nav className="main-nav">
            <Link to="/" className="nav-item">Trang Chủ</Link>
            <Link to="/about" className="nav-item">Giới thiệu</Link>
            <div className="nav-item dropdown">
              <span>Tuyển sinh</span>
              <div className="dropdown-menu">
                <Link to="/admission">Thông tin tuyển sinh</Link>
                <Link to="/career">Hướng Nghiệp</Link>
                <Link to="/consultation">Câu Hỏi Tư Vấn</Link>
              </div>
            </div>
            <div className="nav-item dropdown">
              <span>Phòng ban</span>
              <div className="dropdown-menu">
                <Link to="/department">Phòng Đào Tạo</Link>
                <Link to="/finance">Phòng Kế Hoạch Tài Chính</Link>
                <Link to="/student-affairs">Phòng Công Tác Sinh Viên</Link>
              </div>
            </div>
            <div className="nav-item dropdown">
              <span>Khoa</span>
              <div className="dropdown-menu">
                <Link to="/faculty-it">Khoa Công Nghệ Thông Tin</Link>
                <Link to="/faculty-economics">Khoa Kinh tế - Quản trị</Link>
                <Link to="/faculty-design">Khoa Design</Link>
              </div>
            </div>
            <Link to="/library" className="nav-item">Thư viện</Link>
            <Link to="/contact" className="nav-item">Liên hệ</Link>
          </nav>
          <div className="header-actions">
            <Link to={user ? "/app" : "/app/login"} className="btn-login">
              Quản Lý Tài Khoản
            </Link>
          </div>
        </div>
      </div>
    </header>
  )
}

export default LandingHeader

