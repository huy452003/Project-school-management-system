import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../components/landing/LandingHeader'
import HeroSlider from '../../components/landing/HeroSlider'
import LandingFooter from '../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../config/landingConstants'
import './LandingPage.css'

const LandingPage = () => {

  const newsItems = [
    {
      id: 1,
      title: 'Giữa một buổi chiều xanh mát trong khuôn viên Trường Đại học Công nghệ Sài Gòn (STU)',
      date: '21/12/2025',
      category: 'Tin Tức STU',
      image: 'https://images.stu.edu.vn/image.php?src=/uploads/news/giua-mot-buoi-chieu-xanh-mat-tro-d451d001a1.jpeg&w=450&aoe=1',
      link: 'https://stu.edu.vn/vi/1/21243/giua-mot-buoi-chieu-xanh-mat-trong-khuon-vien-truong-dai-hoc-cong-nghe-sai-gon-stu.html',
      isNew: true
    },
    {
      id: 2,
      title: 'Hơn 40 câu lạc bộ tại Trường Đại học Công nghệ Sài Gòn (STU) dành cho sinh viên',
      date: '20/12/2025',
      category: 'Tin Tức STU',
      image: 'https://images.stu.edu.vn/image.php?src=/uploads/news/40-cau-lac-bo-tai-truong-dai-hoc-bffb383c5b.png&w=450&aoe=1',
      link: 'https://stu.edu.vn/vi/1/21239/hon-40-cau-lac-bo-tai-truong-dai-hoc-cong-nghe-sai-gon-stu-danh-cho-sinh-vien.html',
      isNew: true
    },
    {
      id: 3,
      title: 'Học bổng & Chính sách hỗ trợ tài chính tại trường Đại học Công nghệ Sài Gòn (STU) năm 2025',
      date: '08/09/2025',
      category: 'Tuyển Sinh STU',
      image: 'https://images.stu.edu.vn/image.php?src=/uploads/news/hoc-bong-chinh-sach-ho-tro-tai-c-9f060dd7fa.jpg&w=450&aoe=1',
      link: 'https://stu.edu.vn/vi/1/21029/hoc-bong-chinh-sach-ho-tro-tai-chinh-tai-truong-dai-hoc-cong-nghe-sai-gon-stu-nam-2025.html'
    }
  ]

  const recruitmentItems = [
    {
      id: 1,
      title: 'Điểm chuẩn STU 2025 – Cập nhật mới nhất & So sánh xu hướng các năm trước',
      date: '05/09/2025',
      image: 'https://images.stu.edu.vn/uploads/news/diem-chuan-stu-2025-dcb660cfe4.jpg',
      link: 'https://stu.edu.vn/vi/1/21021/diem-chuan-stu-2025-cap-nhat-moi-nhat-so-sanh-xu-huong-cac-nam-truoc.html'
    },
    {
      id: 2,
      title: 'Điểm chuẩn Trường Đại học Công nghệ Sài Gòn (STU) 2025',
      date: '28/08/2025',
      image: 'https://images.stu.edu.vn/ckfinder/uploads/links/files/b%C3%A0%20%C4%91i%E1%BB%83m%20chu%E1%BA%A9n%202025.JPG',
      link: 'https://stu.edu.vn/vi/1/21004/diem-chuan-truong-dai-hoc-cong-nghe-sai-gon-stu-2025.html'
    }
  ]

  const careerItems = [
    {
      id: 1,
      title: 'Chọn ngành sao cho phù hợp? Gợi ý từ Trường Đại học Công nghệ Sài Gòn (STU)',
      date: '19/12/2025',
      image: 'https://images.stu.edu.vn/image.php?src=/uploads/news/chon-nganh-sao-cho-phu-hop-goi-y-56e28eaf86.png&w=450&aoe=1',
      link: 'https://stu.edu.vn/vi/1/21237/chon-nganh-sao-cho-phu-hop-goi-y-tu-truong-dai-hoc-cong-nghe-sai-gon-stu.html',
      isNew: true
    },
    {
      id: 2,
      title: 'Cơ sở vật chất phục vụ học thực hành tại Trường Đại học Công nghệ Sài Gòn (STU)',
      date: '12/12/2025',
      image: 'https://images.stu.edu.vn/ckfinder/uploads/links/files/525251401_1229209152573345_8104590991107958288_n.jpg',
      link: 'https://stu.edu.vn/vi/1/21224/co-so-vat-chat-phuc-vu-hoc-thuc-hanh-tai-truong-dai-hoc-cong-nghe-sai-gon-stu.html'
    }
  ]

  const studentItems = [
    {
      id: 1,
      title: 'Thông báo về việc tổ chức chương trình "Tập huấn cán bộ Lớp trưởng, Bí thư" năm học 2025-2026',
      date: '12/12/2025',
      link: 'https://stu.edu.vn/vi/1/21222/thong-bao-ve-viec-to-chuc-chuong-trinh-tap-huan-can-bo-lop-truong-bi-thu-nam-hoc-20252026.html',
      isNew: true
    },
    {
      id: 2,
      title: 'Sinh viên STU bùng nổ ấn tượng trong cuộc thi "Trang phục Tái chế năm 2025"',
      date: '23/09/2025',
      link: 'https://stu.edu.vn/vi/1/21076/sinh-vien-stu-bung-no-an-tuong-trong-cuoc-thi-trang-phuc-tai-che-nam-2025.html'
    },
    {
      id: 3,
      title: '6 Tips học tập hiệu quả cho sinh viên năm nhất STU',
      date: '22/09/2025',
      link: 'https://stu.edu.vn/vi/1/21070/6-tips-hoc-tap-hieu-qua-cho-sinh-vien-nam-nhat-stu.html'
    }
  ]

  const departments = [
    'Khoa Công Nghệ Thông Tin',
    'Khoa Kinh tế - Quản trị',
    'Khoa Design'
  ]

  return (
    <div className="landing-page">
      <LandingHeader />
      <HeroSlider slides={DEFAULT_SLIDES} />

      {/* Main Content */}
      <main className="landing-main">
        <div className="container">
          {/* Tin Tức STU */}
          <section className="news-section">
            <div className="section-header">
              <h2 className="section-title">Tin Tức STU</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="news-grid">
              {newsItems.map((item) => (
                <article key={item.id} className="news-card">
                  {item.isNew && <span className="badge-new">mới</span>}
                  <div className="news-image">
                    <img src={item.image} alt={item.title} />
                  </div>
                  <div className="news-content">
                    <span className="news-date">{item.date}</span>
                    <h3 className="news-title">{item.title}</h3>
                    <a 
                      href={item.link || '#'} 
                      target="_blank" 
                      rel="noopener noreferrer" 
                      className="news-link"
                    >
                      Chi tiết
                    </a>
                  </div>
                </article>
              ))}
            </div>
          </section>

          {/* Tuyển Sinh STU */}
          <section className="recruitment-section">
            <div className="section-header">
              <h2 className="section-title">Tuyển Sinh STU</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="recruitment-grid">
              {recruitmentItems.map((item) => (
                <article key={item.id} className="recruitment-card">
                  <div className="recruitment-image">
                    <img src={item.image} alt={item.title} />
                  </div>
                  <div className="recruitment-content">
                    <span className="recruitment-date">{item.date}</span>
                    <h3 className="recruitment-title">{item.title}</h3>
                    <a 
                      href={item.link || '#'} 
                      target="_blank" 
                      rel="noopener noreferrer" 
                      className="recruitment-link"
                    >
                      Chi tiết
                    </a>
                  </div>
                </article>
              ))}
            </div>
          </section>

          {/* Hướng Nghiệp */}
          <section className="career-section">
            <div className="section-header">
              <h2 className="section-title">Hướng Nghiệp</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="career-grid">
              {careerItems.map((item) => (
                <article key={item.id} className="career-card">
                  {item.isNew && <span className="badge-new">mới</span>}
                  <div className="career-image">
                    <img src={item.image} alt={item.title} />
                  </div>
                  <div className="career-content">
                    <span className="career-date">{item.date}</span>
                    <h3 className="career-title">{item.title}</h3>
                    <a 
                      href={item.link || '#'} 
                      target="_blank" 
                      rel="noopener noreferrer" 
                      className="career-link"
                    >
                      Chi tiết
                    </a>
                  </div>
                </article>
              ))}
            </div>
          </section>

          {/* Sinh Viên STU */}
          <section className="student-section">
            <div className="section-header">
              <h2 className="section-title">Sinh Viên STU</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="student-list">
              {studentItems.map((item) => (
                <article key={item.id} className="student-item">
                  {item.isNew && <span className="badge-new">mới</span>}
                  <div className="student-content">
                    <h3 className="student-title">{item.title}</h3>
                    <span className="student-date">{item.date}</span>
                  </div>
                  <a 
                    href={item.link || '#'} 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="student-link"
                  >
                    Chi tiết
                  </a>
                </article>
              ))}
            </div>
          </section>

          {/* Học - Thi - Tuyển sinh */}
          <section className="exam-section">
            <div className="section-header">
              <h2 className="section-title">Học - Thi - Tuyển sinh</h2>
            </div>
            <div className="exam-list">
              <article className="exam-item">
                <span className="badge-new">mới</span>
                <h3 className="exam-title">Những điểm cần lưu ý trong Quy chế tuyển sinh đại học 2025</h3>
                <span className="exam-date">26/03/2025</span>
                <a 
                  href="https://stu.edu.vn/vi/1/20616/nhung-diem-can-luu-y-trong-quy-che-tuyen-sinh-dai-hoc-2025.html" 
                  target="_blank" 
                  rel="noopener noreferrer" 
                  className="exam-link"
                >
                  Chi tiết
                </a>
              </article>
            </div>
          </section>
        </div>
      </main>

      <LandingFooter />
    </div>
  )
}

export default LandingPage

