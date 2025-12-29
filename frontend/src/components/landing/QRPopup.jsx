import React from 'react'
import './QRPopup.css'

const QRPopup = ({ isOpen, onClose, qrImage }) => {
  if (!isOpen) return null

  return (
    <div className="qr-popup-overlay" onClick={onClose}>
      <div className="qr-popup-content" onClick={(e) => e.stopPropagation()}>
        <button className="qr-popup-close" onClick={onClose}>
          ×
        </button>
        <h2 className="qr-popup-title">Mã QR Đóng Học Phí Nhanh</h2>
        <div className="qr-popup-image-wrapper">
          <img src={qrImage} alt="Mã QR đóng học phí" className="qr-popup-image" />
        </div>
        <p className="qr-popup-note">Quét mã QR để thanh toán học phí nhanh chóng</p>
      </div>
    </div>
  )
}

export default QRPopup

