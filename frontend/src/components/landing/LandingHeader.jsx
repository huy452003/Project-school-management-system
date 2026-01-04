import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import logoImage from '../../img/logo-school.png'
import './LandingHeader.css'

const LandingHeader = () => {
  const { user } = useAuth()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const [openDropdown, setOpenDropdown] = useState(null)

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen)
  }

  const closeMobileMenu = () => {
    setMobileMenuOpen(false)
    setOpenDropdown(null)
  }

  const toggleDropdown = (dropdownName) => {
    setOpenDropdown(openDropdown === dropdownName ? null : dropdownName)
  }

  return (
    <header className="landing-header">
      <div className="header-main">
        <div className="container">
          <div className="logo-section">
            <div className="logo">
              <img src={logoImage} alt="STU" />
            </div>
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
          <nav className={`main-nav ${mobileMenuOpen ? 'mobile-open' : ''}`}>
            <Link to="/" className="nav-item" onClick={closeMobileMenu}>Trang Chủ</Link>
            <Link to="/about" className="nav-item" onClick={closeMobileMenu}>Giới thiệu</Link>
            <div className={`nav-item dropdown ${openDropdown === 'tuyen-sinh' ? 'open' : ''}`}>
              <span onClick={() => toggleDropdown('tuyen-sinh')}>Tuyển sinh</span>
              <div className="dropdown-menu">
                <Link to="/admission" onClick={closeMobileMenu}>Thông tin tuyển sinh</Link>
                <Link to="/career" onClick={closeMobileMenu}>Hướng Nghiệp</Link>
                <Link to="/consultation" onClick={closeMobileMenu}>Câu Hỏi Tư Vấn</Link>
              </div>
            </div>
            <div className={`nav-item dropdown ${openDropdown === 'phong-ban' ? 'open' : ''}`}>
              <span onClick={() => toggleDropdown('phong-ban')}>Phòng ban</span>
              <div className="dropdown-menu">
                <Link to="/department" onClick={closeMobileMenu}>Phòng Đào Tạo</Link>
                <Link to="/finance" onClick={closeMobileMenu}>Phòng Kế Hoạch Tài Chính</Link>
                <Link to="/student-affairs" onClick={closeMobileMenu}>Phòng Công Tác Sinh Viên</Link>
              </div>
            </div>
            <div className={`nav-item dropdown ${openDropdown === 'khoa' ? 'open' : ''}`}>
              <span onClick={() => toggleDropdown('khoa')}>Khoa</span>
              <div className="dropdown-menu">
                <Link to="/faculty-it" onClick={closeMobileMenu}>Khoa Công Nghệ Thông Tin</Link>
                <Link to="/faculty-economics" onClick={closeMobileMenu}>Khoa Kinh tế - Quản trị</Link>
                <Link to="/faculty-design" onClick={closeMobileMenu}>Khoa Design</Link>
              </div>
            </div>
            <Link to="/library" className="nav-item" onClick={closeMobileMenu}>Thư viện</Link>
            <Link to="/contact" className="nav-item" onClick={closeMobileMenu}>Liên hệ</Link>
          </nav>
          <div className={`header-actions ${mobileMenuOpen ? 'mobile-open' : ''}`}>
            <Link to={user ? "/app" : "/app/login"} className="btn-login" onClick={closeMobileMenu}>
              Quản Lý Tài Khoản
            </Link>
          </div>
        </div>
      </div>
    </header>
  )
}

export default LandingHeader

