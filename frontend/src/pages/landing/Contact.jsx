import React from 'react'
import LandingHeader from '../../components/landing/LandingHeader'
import HeroSlider from '../../components/landing/HeroSlider'
import LandingFooter from '../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../config/landingConstants'
import './Contact.css'

const Contact = () => {

  return (
    <div className="contact-page">
      <LandingHeader />
      <HeroSlider slides={DEFAULT_SLIDES} />

      {/* Main Content */}
      <main className="contact-main">
        <div className="container">
          <div className="contact-content">
            <h1 className="page-title">Thông tin liên hệ</h1>

            <section className="contact-section">
              <div className="contact-info">
                <h2 className="contact-title">Trường Đại học Công nghệ Sài Gòn HuyK3School</h2>
                
                <div className="contact-details">
                  <div className="contact-item">
                    <div className="contact-label">Địa chỉ:</div>
                    <div className="contact-value">khu tự trị campuchia của huyk3, Phnom Penh, Cambodia</div>
                  </div>
                  
                  <div className="contact-item">
                    <div className="contact-label">Điện thoại:</div>
                    <div className="contact-value">0101010101</div>
                  </div>
                  
                  <div className="contact-item">
                    <div className="contact-label">Fax:</div>
                    <div className="contact-value">0101010101</div>
                  </div>
                  
                  <div className="contact-item">
                    <div className="contact-label">Email:</div>
                    <div className="contact-value">
                      <a href="mailto:huyk3schoolcontact@stu.edu.vn">huyk3schoolcontact@stu.edu.vn</a>
                    </div>
                  </div>
                </div>

                <div className="contact-map">
                  <iframe
                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3918.5!2d106.6!3d10.7!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMTDCsDQyJzAwLjAiTiAxMDbCsDM2JzAwLjAiRQ!5e0!3m2!1svi!2s!4v1234567890!5m2!1svi!2s"
                    width="100%"
                    height="400"
                    style={{ border: 0 }}
                    allowFullScreen=""
                    loading="lazy"
                    referrerPolicy="no-referrer-when-downgrade"
                    title="Thư viện STU - 180 Cao Lỗ, Phường 4, Quận 8"
                  ></iframe>
                </div>
              </div>

              <div className="contact-info">
                <h2 className="contact-title">Thư viện Trường Đại học Công nghệ Sài Gòn HuyK3School (Tầng 10 của trường )</h2>
                
                <div className="contact-details">
                  <div className="contact-item">
                    <div className="contact-label">Địa chỉ:</div>
                    <div className="contact-value">khu tự trị campuchia của huyk3, Phnom Penh, Cambodia</div>
                  </div>
                  
                  <div className="contact-item">
                    <div className="contact-label">Điện thoại:</div>
                    <div className="contact-value">0101010101</div>
                  </div>
                  
                  <div className="contact-item">
                    <div className="contact-label">Fax:</div>
                    <div className="contact-value">0101010101</div>
                  </div>
                  
                  <div className="contact-item">
                    <div className="contact-label">Email:</div>
                    <div className="contact-value">
                      <a href="mailto:huyk3schoolcontact@stu.edu.vn">huyk3schoolcontact@stu.edu.vn</a>
                    </div>
                  </div>
                </div>

                <div className="contact-map">
                  <iframe
                    src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3918.5!2d106.6!3d10.7!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zMTDCsDQyJzAwLjAiTiAxMDbCsDM2JzAwLjAiRQ!5e0!3m2!1svi!2s!4v1234567890!5m2!1svi!2s"
                    width="100%"
                    height="400"
                    style={{ border: 0 }}
                    allowFullScreen=""
                    loading="lazy"
                    referrerPolicy="no-referrer-when-downgrade"
                    title="Thư viện STU - 180 Cao Lỗ, Phường Chánh Hưng"
                  ></iframe>
                </div>
              </div>
            </section>
          </div>
        </div>
      </main>

      <LandingFooter />
    </div>
  )
}

export default Contact

