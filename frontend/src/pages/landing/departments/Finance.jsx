import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import '../cssToDeparts_Facus.css'

const Finance = () => {

  const relatedItems = [
    {
      id: 1,
      title: 'Thông báo " V/v miễn thuế thu nhập cá nhân năm 2011"',
      date: '10/10/2011',
      link: 'https://stu.edu.vn/vi/376/6695/thong-bao-v-v-mien-thue-thu-nhap-ca-nhan-nam-2011.html'
    }
  ]

  const videos = [
    {
      id: 1,
      title: 'STU - Ngày tốt nghiệp',
      thumbnail: 'https://images.stu.edu.vn/image.php?src=/uploads/banner/images/ba115e88aa527667d1a15de78ca372f9.png&h=150&aoe=1'
    },
    {
      id: 2,
      title: 'Đánh thức đam mê, Chinh phục ước mơ - Bí kíp "Bẻ khóa tương lai" cùng STU',
      thumbnail: 'http://images.stu.edu.vn/image.php?src=/uploads/banner/images/dsc0254-a403b0b891.jpg&h=150&aoe=1'
    },
    {
      id: 3,
      title: 'STU - Nơi chắp cánh những ước mơ',
      thumbnail: 'http://images.stu.edu.vn/image.php?src=/uploads/banner/images/ms56-4-a827e8c3bd.jpg&h=150&aoe=1'
    }
  ]

  const departments = [
    'Khoa Công Nghệ Thông Tin',
    'Khoa Kinh tế - Quản trị',
    'Khoa Design'
  ]

  return (
    <div className="department-page">
      {/* Header */}
      <LandingHeader />
      <HeroSlider slides={DEFAULT_SLIDES} />

      {/* Main Content */}
      <main className="department-main">
        <div className="container">
          <div className="department-layout">
            {/* Left Column - Content */}
            <div className="department-content">
              <h1 className="page-title">Giới Thiệu Phòng Kế Hoạch Tài Chính</h1>

              {/* Chức năng - Nhiệm vụ */}
              <section className="functions-section">
                <div className="section-header">
                  <h2 className="section-title">Chức năng - Nhiệm vụ</h2>
                  <span className="update-date">Cập nhật 03/09/2009 - 10:40:26 AM (GMT+7)</span>
                </div>
                <div className="functions-content">
                  <div className="function-block">
                    <h3 className="function-heading">1. Chức năng:</h3>
                    <p className="function-text">
                      Tham mưu, giúp HĐQT và Hiệu trưởng xây dựng kế hoạch Tài chính, quản lý, điều hành toàn bộ hoạt động tài chính của Nhà trường.
                    </p>
                  </div>
                  
                  <div className="function-block">
                    <h3 className="function-heading">2. Nhiệm vụ:</h3>
                    <ol className="functions-list">
                      <li className="function-item">
                        <span className="function-title">Thực hiện công tác kế toán thống kê đúng quy định của Nhà nước.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Tham mưu cho BGH xây dựng kế hoạch tài chính toàn trường.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Quản lý vốn góp, cổ phần, cổ đông: chuyển nhượng, ưu đãi…, tham mưu cho lãnh đạo Nhà trường trong việc ra quyết định mức cổ tức hàng năm cho cổ đông trong các năm tài chính.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Quản lý giá trị tài sản của trường (Tính giá trị tài sản hình thành, chuyển nhượng, thanh lý, giá trị khấu hao hàng năm.)</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Quản lý các hợp đồng kinh tế về thu, chi, dịch vụ, xây dựng.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Có trách nhiệm về chuyên môn, nghiệp vụ trong phạm vi tài chính của Trường.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Thay mặt Nhà trường giao dịch, báo cáo định kỳ cho cơ quan Thuế.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Báo cáo tài chính, thực hiện kế hoạch tài chính định kỳ cho HĐQT và BGH.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Tổ chức thu học phí sinh viên toàn trường.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Phối hợp với các phòng chức năng đề xuất, tham mưu cho BGH trong việc quyết định chế độ lương, thưởng của GV, CBCNV toàn trường.</span>
                      </li>
                      <li className="function-item">
                        <span className="function-title">Phối hợp với các phòng chức năng đề xuất, tham mưu cho BGH trong việc quyết định mức học phí, chế độ miễn giảm học phí cho sinh viên toàn trường.</span>
                      </li>
                    </ol>
                  </div>
                </div>
              </section>

              {/* Các Nội Dung Liên Quan */}
              <section className="related-section">
                <h2 className="section-title">Các Nội Dung Liên Quan</h2>
                <div className="related-list">
                  {relatedItems.map((item) => (
                    <article key={item.id} className="related-item">
                      <a 
                        href={item.link} 
                        target="_blank" 
                        rel="noopener noreferrer" 
                        className="related-link"
                      >
                        {item.title}
                      </a>
                      <span className="related-date">({item.date})</span>
                    </article>
                  ))}
                </div>
              </section>
            </div>

            {/* Right Column - Sidebar */}
            <aside className="department-sidebar">
              {/* Videos Section */}
              <section className="videos-section">
                <h2 className="sidebar-title">Videos</h2>
                <div className="videos-list">
                  {videos.map((video) => (
                    <div key={video.id} className="video-item">
                      <div className="video-thumbnail">
                        <img src={video.thumbnail} alt={video.title} />
                        <div className="play-overlay">
                          <span className="play-icon">▶</span>
                        </div>
                      </div>
                      <h3 className="video-title">{video.title}</h3>
                    </div>
                  ))}
                </div>
                <Link to="/" className="view-all">Xem tất cả</Link>
              </section>

              {/* Quick Links */}
              <section className="quick-links-section">
                <h2 className="sidebar-title">Liên Kết Thường Dùng</h2>
                <ul className="quick-links">
                  <li><Link to="/">Trắc Nghiệm IQ</Link></li>
                  <li><Link to="/">Tuyển sinh thạc sĩ</Link></li>
                  <li><Link to="/admission">Tuyển sinh đại học</Link></li>
                </ul>
              </section>
            </aside>
          </div>
        </div>
      </main>

      <LandingFooter />
    </div>
  )
}

export default Finance

