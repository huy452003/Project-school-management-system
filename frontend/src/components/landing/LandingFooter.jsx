import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import QRPopup from './QRPopup'
import { QR_PAYMENT_IMAGE } from '../../config/constants'
import './LandingFooter.css'

const LandingFooter = () => {
  const { user } = useAuth()
  const [showQRPopup, setShowQRPopup] = useState(false)

  return (
    <>
      <footer className="landing-footer">
        <div className="container">
          <div className="footer-content">
            <div className="footer-section">
              <h3>TUYỂN SINH</h3>
              <ul>
                <li><Link to="/admission">Thông tin tuyển sinh HuyK3School</Link></li>
                <li><Link to="/career">Hướng Nghiệp</Link></li>
                <li><Link to="/consultation">Câu Hỏi Tư Vấn</Link></li>
              </ul>
            </div>           
            <div className="footer-section">
              <h3>PHÒNG BAN</h3>
              <ul>
                <li><Link to="/department">Phòng Đào Tạo</Link></li>
                <li><Link to="/finance">Phòng Kế Hoạch Tài Chính</Link></li>
                <li><Link to="/student-affairs">Phòng Công Tác Sinh Viên</Link></li>
              </ul>
            </div>
            <div className="footer-section">
              <h3>KHOA ĐÀO TẠO</h3>
              <ul>
                <li><Link to="/faculty-it">Khoa Công Nghệ Thông Tin</Link></li>
                <li><Link to="/faculty-economics">Khoa Kinh tế - Quản trị</Link></li>
                <li><Link to="/faculty-design">Khoa Design</Link></li>
              </ul>
            </div>
            <div className="footer-section">
              <h3>TÍNH NĂNG THƯỜNG DÙNG</h3>
              <ul>
                <li><Link to={user ? "/app" : "/app/login"}>Quản Lý Tài Khoản</Link></li>
                <li><Link to="/contact">Liên hệ</Link></li>
                <li><Link to="/library">Thư viện</Link></li>
              </ul>
            </div>
          </div>
          <div className="footer-qr-section">
            <button 
              className="footer-qr-btn"
              onClick={() => setShowQRPopup(true)}
            >
              💳 Hiện mã QR đóng học phí nhanh
            </button>
          </div>
          <div className="footer-bottom">
            <div className="footer-info">
              <h4>Trường Đại học Công nghệ Sài Gòn - HuyK3School</h4>
              <p>khu tự trị campuchia của huyk3, Phnom Penh, Cambodia</p>
              <p>ĐT: 0101010101 | Email: huyk3schoolcontact@stu.edu.vn</p>
            </div>
            <div className="footer-copyright">
              <p>© 2025 Trường Đại học Công nghệ Sài Gòn - HuyK3School. All rights reserved.</p>
              <p>Bản quyền thuộc về Trường Đại học Công nghệ Sài Gòn - HuyK3School</p>
            </div>
          </div>
        </div>
      </footer>

      <QRPopup
        isOpen={showQRPopup}
        onClose={() => setShowQRPopup(false)}
        qrImage={QR_PAYMENT_IMAGE}
      />
    </>
  )
}

export default LandingFooter

