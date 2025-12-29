import React from 'react'
import './Footer.css'

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-section">
          <h3 className="footer-title">Liên Hệ Với Nhà Trường Qua Các Kênh:</h3>
          <div className="footer-content">
            <div className="footer-item">
              <div className="footer-icon">📞</div>
              <div className="footer-text">
                <span className="footer-label">Hotline hỗ trợ</span>
                <span className="footer-value">1900-3333-4444 (HuyK3)</span>
              </div>
            </div>
            <div className="footer-item">
              <div className="footer-icon">📧</div>
              <div className="footer-text">
                <span className="footer-label">Email hỗ trợ</span>
                <span className="footer-value">huyk3@school.edu.vn</span>
              </div>
            </div>
            <div className="footer-item">
              <div className="footer-icon">💬</div>
              <div className="footer-text">
                <span className="footer-label">Chat trực tuyến</span>
                <div className="footer-social-icons">
                  <a href="https://facebook.com" target="_blank" rel="noopener noreferrer" className="social-icon facebook" title="Facebook">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                    </svg>
                  </a>
                  <a href="https://zalo.me" target="_blank" rel="noopener noreferrer" className="social-icon zalo" title="Zalo">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm3.5 6L12 10.5 8.5 8l3.5 2.5L15.5 8zm-7 8l3.5-2.5L15.5 16l-3.5-2.5L8.5 16z"/>
                    </svg>
                  </a>
                  <a href="https://messenger.com" target="_blank" rel="noopener noreferrer" className="social-icon messenger" title="Messenger">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 0C5.373 0 0 5.373 0 12c0 3.584 1.948 6.728 4.851 8.421L3.5 24l3.649-1.351C8.5 23.5 10.2 24 12 24c6.627 0 12-5.373 12-12S18.627 0 12 0zm0 22c-1.5 0-2.9-.3-4.1-.8l-.5-.2-2.7.7.7-2.6-.2-.5C4.3 16.9 4 15.5 4 14c0-4.4 3.6-8 8-8s8 3.6 8 8-3.6 8-8 8zm-1-7h2v2h-2v-2zm0-4h2v2h-2v-2zm4 0h2v2h-2v-2z"/>
                    </svg>
                  </a>
                </div>
              </div>
            </div>
            <div className="footer-item">
              <div className="footer-icon">📍</div>
              <div className="footer-text">
                <span className="footer-label">Phòng hỗ trợ sinh viên</span>
                <span className="footer-value">Phòng 101, Tầng 1</span>
              </div>
            </div>
          </div>
        </div>
        <div className="footer-bottom">
          <p>&copy; 2025 Hệ Thống Quản Lý Nhà Trường - By Huyk3. Tất cả quyền được bảo lưu by HuyK3.</p>
        </div>
      </div>
    </footer>
  )
}

export default Footer

