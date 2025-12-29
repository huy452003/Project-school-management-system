import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../components/landing/LandingHeader'
import HeroSlider from '../../components/landing/HeroSlider'
import LandingFooter from '../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../config/landingConstants'
import logoImage from '../../img/logo-school.png'
import './Library.css'

const Library = () => {

  const announcements = [
    {
      id: 1,
      title: 'Thông báo thời gian hoạt động của thư viện dịp hè năm 2025',
      date: '25/12/2025',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=37',
      image: 'https://thuvien.stu.edu.vn/Upload/2025/08/11/thong-bao.png_2025Thg811_081025793(1).png'
    },
    {
      id: 2,
      title: 'Hưởng ứng ngày sách Việt Nam 21-4',
      date: '21/04/2025',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=36',
      image: 'https://thuvien.stu.edu.vn/Upload/2025/04/21/ngay%20sach%202025%20-%201.png_2025Thg421_084340208(1).png'
    },
    {
      id: 3,
      title: 'THÔNG BÁO VỀ VIỆC SỬ DỤNG CƠ SỞ DỮ LIỆU BENITO',
      date: '21/04/2023',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=34',
      image: 'https://thuvien.stu.edu.vn/Upload/2024/10/10/benito.png_2024Thg1010_024200078(1).png'
    },
    {
      id: 4,
      title: 'V/V Mở Rộng Thư Viện Phục Vụ Bạn Đọc STU',
      date: '23/12/2022',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=32',
      image: 'https://thuvien.stu.edu.vn/Upload/2024/05/28/11.jpg_2024Thg528_081755404(1).jpg'
    },
    {
      id: 5,
      title: 'ĐĂNG KÝ KÍCH HOẠT TÀI KHOẢN',
      date: '04/10/2022',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=30',
      image: logoImage
    }
  ]

  const newsItems = [
    {
      id: 1,
      title: 'THÔNG BÁO VỀ VIỆC SỬ DỤNG CƠ SỞ DỮ LIỆU BENITO',
      date: '14/08/2024',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=34'
    },
    {
      id: 2,
      title: 'ĐĂNG KÝ KÍCH HOẠT TÀI KHOẢN',
      date: '03/10/2023',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=30'
    },
    {
      id: 3,
      title: 'Hoạt động hưởng ứng ngày sách Việt Nam 21/04/2023',
      date: '14/04/2023',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=28'
    },
    {
      id: 4,
      title: 'Tài khoản xem toàn văn các tài liệu số tailieu.vn',
      date: '23/12/2022',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=26'
    },
    {
      id: 5,
      title: 'Thông báo về buổi giới thiệu chuyên đề',
      date: '04/10/2022',
      link: 'https://thuvien.stu.edu.vn/TrangTinTinTucChiTiet.aspx?Id=23'
    }
  ]

  const featuredBooks = [
    {
      id: 1,
      title: 'Software Engineering',
      link: 'https://thuvien.stu.edu.vn/TrangTinSanPhamChiTiet.aspx?Id=58',
      cover: 'https://thuvien.stu.edu.vn/Upload/2021/11/05/165e38ce6df5a5abfce410.jpg_2021Thg1105_044042252(1).jpg'
    },
    {
      id: 2,
      title: 'SOFTWARE EVOLUTION AND MAINTENANCE',
      link: 'https://thuvien.stu.edu.vn/TrangTinSanPhamChiTiet.aspx?Id=54',
      cover: 'https://thuvien.stu.edu.vn/Upload/2021/11/05/1b148d63d7581f0646494.jpg_2021Thg1105_040338737(1).jpg'
    },
    {
      id: 3,
      title: 'CẨM NANG PHÒNG, CHỐNG COVID-19 TRONG ĐẢM BẢO AN TOÀN, VỆ SINH LAO ĐỘNG',
      link: 'https://thuvien.stu.edu.vn/TrangTinSanPhamChiTiet.aspx?Id=47',
      cover: 'https://thuvien.stu.edu.vn/Upload/2021/08/04/1.jpg_2021Thg804_074252239(1).jpg'
    },
    {
      id: 4,
      title: 'Introduction to Data Science',
      link: 'https://thuvien.stu.edu.vn/TrangTinSanPhamChiTiet.aspx?Id=57',
      cover: 'https://thuvien.stu.edu.vn/Upload/2021/11/05/77b251d10beac3b49afb7.jpg_2021Thg1105_045441503(1).jpg'
    }
  ]

  const openDatabases = [
    {
      id: 1,
      title: 'Thư viện pháp luật',
      link: 'https://thuvienphapluat.vn/page/tim-van-ban.aspx?keyword=&type=39&match=True&area=0',
      icon: 'https://thuvien.stu.edu.vn/Upload/2025/11/27/Screenshot%202025-11-27%20101651.jpg_2025Thg1127_112703826(1).jpg',
      isNew: true
    },
    {
      id: 2,
      title: 'Mạng thông tin và khoa học công nghệ Tp.HCM',
      link: 'http://www.stinet.gov.vn/',
      icon: 'https://thuvien.stu.edu.vn/Upload/2021/11/30/stinet.png_2021Thg1130_044754866(1).png',
      isNew: false
    }
  ]

  const popularDocuments = [
    {
      id: 1,
      title: 'Hướng dẫn học tập Tư tưởng Hồ Chí Minh',
      link: 'https://thuvien.stu.edu.vn/TraCuuTaiLieuSo2XemChiTiet.aspx?Id=274'
    },
    {
      id: 2,
      title: 'Bài giảng Nhập môn Quản trị chuỗi cung ứng',
      link: 'https://thuvien.stu.edu.vn/TraCuuTaiLieuSo2XemChiTiet.aspx?Id=238'
    },
    {
      id: 3,
      title: 'Xây dựng website gọi món và quản lý món ăn',
      link: 'https://thuvien.stu.edu.vn/TraCuuTaiLieuSo2XemChiTiet.aspx?Id=263'
    },
    {
      id: 4,
      title: 'Xây dựng hệ thống quản lý sản xuất và tồn kho cho một công ty inox',
      link: 'https://thuvien.stu.edu.vn/TraCuuTaiLieuSo2XemChiTiet.aspx?Id=265'
    },
    {
      id: 5,
      title: 'Thiết kế mô hình bãi đậu xe tự động',
      link: 'https://thuvien.stu.edu.vn/TraCuuTaiLieuSo2XemChiTiet.aspx?Id=38'
    },
    {
      id: 6,
      title: 'Xây dựng hệ thống đặt phòng khách sạn trực tuyến dựa trên mô hình Marketplace',
      link: 'https://thuvien.stu.edu.vn/TraCuuTaiLieuSo2XemChiTiet.aspx?Id=244'
    }
  ]

  const departments = [
    'Khoa Công Nghệ Thông Tin',
    'Khoa Kinh tế - Quản trị',
    'Khoa Design'
  ]

  return (
    <div className="library-page">
      {/* Header */}
      <LandingHeader />
      <HeroSlider slides={DEFAULT_SLIDES} />

      {/* Main Content */}
      <main className="library-main">
        <div className="container">
          {/* Dòng sự kiện - Full width */}
          <section className="events-section">
            <h2 className="section-title">Dòng sự kiện</h2>
            <div className="events-list">
              {announcements.map((item) => (
                <div key={item.id} className="event-item">
                  <div className="event-image">
                    <img src={item.image} alt={item.title} />
                  </div>
                  <div className="event-content">
                    <a 
                      href={item.link} 
                      target="_blank" 
                      rel="noopener noreferrer" 
                      className="event-link"
                    >
                      {item.title}
                    </a>
                    <span className="event-date">{item.date}</span>
                  </div>
                </div>
              ))}
            </div>
          </section>

          <div className="library-layout">
            {/* Left Column - Main Content */}
            <div className="library-content">
              {/* Dịch vụ thư viện */}
              <section className="services-section">
                {/* GIỚI THIỆU SÁCH */}
                <div className="service-card">
                  <h3 className="service-title">GIỚI THIỆU SÁCH</h3>
                  <div className="books-grid">
                    {featuredBooks.map((book) => (
                      <div key={book.id} className="book-item">
                        <a 
                          href={book.link} 
                          target="_blank" 
                          rel="noopener noreferrer"
                          className="book-link"
                        >
                          <div className="book-cover">
                            <img src={book.cover} alt={book.title} />
                          </div>
                          <p className="book-title">{book.title}</p>
                        </a>
                      </div>
                    ))}
                  </div>
                  <div className="view-more-container">
                    <Link to="/" className="view-more">Xem thêm</Link>
                  </div>
                </div>

                {/* CƠ SỞ DỮ LIỆU MỞ */}
                <div className="service-card">
                  <h3 className="service-title">CƠ SỞ DỮ LIỆU MỞ</h3>
                  <div className="databases-open-grid">
                    {openDatabases.map((db) => (
                      <div key={db.id} className="database-open-item">
                        <a 
                          href={db.link} 
                          target="_blank" 
                          rel="noopener noreferrer"
                          className="database-open-link"
                        >
                          <div className="database-icon-wrapper">
                            <div className="database-icon">
                              <img src={db.icon} alt={db.title} />
                            </div>
                            {db.isNew && <span className="new-badge">NEW</span>}
                          </div>
                          <p className="database-open-title">{db.title}</p>
                        </a>
                      </div>
                    ))}
                  </div>
                  <div className="view-more-container">
                    <Link to="/" className="view-more">Xem thêm</Link>
                  </div>
                </div>
              </section>

              {/* Tài liệu số quan tâm nhiều nhất */}
              <section className="popular-docs-section">
                <h2 className="section-title">Tài liệu số quan tâm nhiều nhất</h2>
                <ul className="popular-docs-list">
                  {popularDocuments.map((doc) => (
                    <li key={doc.id}>
                      <a 
                        href={doc.link} 
                        target="_blank" 
                        rel="noopener noreferrer"
                      >
                        {doc.title}
                      </a>
                    </li>
                  ))}
                </ul>
                <Link to="/" className="view-more">Xem thêm</Link>
              </section>

            </div>

            {/* Right Column - Sidebar */}
            <aside className="library-sidebar">
              {/* Tìm kiếm nhanh */}
              <section className="search-section">
                <h2 className="sidebar-title">Tìm kiếm nhanh</h2>
                <div className="search-forms">
                  <div className="search-form-item">
                    <label>Thư viện truyền thống</label>
                    <div className="search-input-group">
                      <input type="text" placeholder="Nhập từ khóa tìm kiếm..." />
                      <button type="button">Tìm kiếm</button>
                    </div>
                  </div>
                  <div className="search-form-item">
                    <label>Tài liệu số</label>
                    <div className="search-input-group">
                      <input type="text" placeholder="Nhập từ khóa tìm kiếm..." />
                      <button type="button">Tìm kiếm</button>
                    </div>
                  </div>
                </div>
              </section>

              {/* Chỉ dẫn */}
              <section className="guide-section">
                <h2 className="sidebar-title">Chỉ dẫn</h2>
                <div className="guide-content">
                  <p className="guide-text">
                    <strong>Giờ mở cửa:</strong>
                  </p>
                  <p className="guide-text">
                    <strong>Thứ Hai - thứ Sáu: 7h30 - 17h00</strong>
                  </p>
                  <p className="guide-text">
                    <strong>Thứ Bảy: 7h30 - 15h30</strong>
                  </p>
                </div>
              </section>

              {/* Tin thông báo */}
              <section className="news-section">
                <h2 className="sidebar-title">Tin thông báo</h2>
                <div className="news-list">
                  {newsItems.map((item) => (
                    <div key={item.id} className="news-item">
                      <a 
                        href={item.link} 
                        target="_blank" 
                        rel="noopener noreferrer"
                        className="news-link"
                      >
                        {item.title}
                      </a>
                      <span className="news-date"> - {item.date}</span>
                    </div>
                  ))}
                </div>
                <Link to="/" className="view-more">Xem thêm</Link>
              </section>

              {/* Thống kê truy cập */}
              <section className="stats-section">
                <h2 className="sidebar-title">Thống kê truy cập</h2>
                <div className="stats-content">
                  <div className="stat-number">3.550.949</div>
                  <p className="stat-label">Tổng lượt truy cập</p>
                  <div className="stat-number">166.318</div>
                  <p className="stat-label">Tháng hiện tại</p>
                </div>
              </section>
            </aside>
          </div>
        </div>
      </main>

      <LandingFooter />
    </div>
  )
}

export default Library

