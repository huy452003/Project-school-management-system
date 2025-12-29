import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import './Career.css'

const Career = () => {

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
    },
    {
      id: 3,
      title: 'Trường Đại học Công nghệ Sài Gòn (STU) – Trường mạnh về thực hành',
      date: '09/12/2025',
      image: 'http://images.stu.edu.vn/ckfinder/uploads/links/files/STU%20FB%20post%202025-2026.png',
      link: 'https://stu.edu.vn/vi/267/21209/truong-dai-hoc-cong-nghe-sai-gon-stu-truong-manh-ve-thuc-hanh.html'
    }
  ]

  const examItems = [
    {
      id: 1,
      title: 'Những điểm cần lưu ý trong Quy chế tuyển sinh đại học 2025',
      date: '26/03/2025',
      link: 'https://stu.edu.vn/vi/267/20616/nhung-diem-can-luu-y-trong-quy-che-tuyen-sinh-dai-hoc-2025.html',
      isNew: true
    },
    {
      id: 2,
      title: 'Những điểm mới trong Quy chế thi tốt nghiệp THPT từ năm 2025',
      date: '25/12/2024',
      link: 'https://stu.edu.vn/vi/267/20426/nhung-diem-moi-trong-quy-che-thi-tot-nghiep-thpt-tu-nam-2025.html'
    },
    {
      id: 3,
      title: 'Đề thi tham khảo Kỳ thi tốt nghiệp THPT năm 2025',
      date: '29/10/2024',
      link: 'https://stu.edu.vn/vi/267/20283/de-thi-tham-khao-ky-thi-tot-nghiep-thpt-nam-2025.html'
    }
  ]

  const laborItems = [
    {
      id: 1,
      title: 'Tìm hiểu 12 ngành bậc đại học đang có nhu cầu cao về nhân lực thí sinh cần biết.',
      date: '09/05/2022',
      link: 'https://stu.edu.vn/vi/267/18435/tim-hieu-12-nganh-bac-dai-hoc-dang-co-nhu-cau-cao-ve-nhan-luc-thi-sinh-can-biet.html'
    },
    {
      id: 2,
      title: 'Ngành Công nghệ Thông tin khan hiếm nhân lực',
      date: '18/04/2022',
      link: 'https://stu.edu.vn/vi/267/18414/nganh-cong-nghe-thong-tin-khan-hiem-nhan-luc.html'
    },
    {
      id: 3,
      title: 'Xu hướng tuyển dụng trên thế giới sau đại dịch COVID-19.',
      date: '07/07/2021',
      link: 'https://stu.edu.vn/vi/267/17813/xu-huong-tuyen-dung-tren-the-gioi-sau-dai-dich-covid19.html'
    }
  ]

  const experienceItems = [
    {
      id: 1,
      title: 'Cách \'gọt tỉa\' CV cũ thành bản giới thiệu sáng giá.',
      date: '06/05/2022',
      link: 'https://stu.edu.vn/vi/267/18413/cach-got-tia-cv-cu-thanh-ban-gioi-thieu-sang-gia.html',
      isNew: true
    },
    {
      id: 2,
      title: 'Bốn lỗi khi viết CV.',
      date: '12/03/2022',
      link: 'https://stu.edu.vn/vi/267/18307/bon-loi-khi-viet-cv.html'
    },
    {
      id: 3,
      title: 'Nhà tuyển dụng tìm kiếm \'thái độ\' hay \'trình độ\' ở ứng viên?',
      date: '06/02/2022',
      link: 'https://stu.edu.vn/vi/267/18306/nha-tuyen-dung-tim-kiem-thai-do-hay-trinh-do-o-ung-vien.html'
    }
  ]

  const successItems = [
    {
      id: 1,
      title: 'Chàng trai vàng Olympic Vật lý APhO lập "kỳ tích" sau 19 năm của Việt Nam.',
      date: '25/05/2021',
      link: 'https://stu.edu.vn/vi/267/17615/chang-trai-vang-olympic-vat-ly-apho-lap-ky-tich-sau-19-nam-cua-viet-nam.html'
    },
    {
      id: 2,
      title: 'Cô gái khiếm thị nhận học bổng 1,5 tỷ đồng để viết tiếp ước mơ truyền thông.',
      date: '13/03/2021',
      link: 'https://stu.edu.vn/vi/267/17539/co-gai-khiem-thi-nhan-hoc-bong-15-ty-dong-de-viet-tiep-uoc-mo-truyen-thong.html'
    },
    {
      id: 3,
      title: '17 TUỔI SÁNG CHẾ ROBOT HỖ TRỢ ĐIỀU TRỊ TỰ KỶ, WEBSITE HỌC TRỰC TUYẾN',
      date: '20/03/2020',
      link: 'https://stu.edu.vn/vi/267/15739/17-tuoi-sang-che-robot-ho-tro-dieu-tri-tu-ky-website-hoc-truc-tuyen.html'
    }
  ]

  const richItems = [
    {
      id: 1,
      title: 'Định luật người giàu: Muốn thành công, trước thay đổi THÓI QUEN, sau thay đổi CÁCH NGHĨ.',
      date: '08/09/2021',
      link: 'https://stu.edu.vn/vi/267/17955/dinh-luat-nguoi-giau-muon-thanh-cong-truoc-thay-doi-thoi-quen-sau-thay-doi-cach-nghi.html'
    },
    {
      id: 2,
      title: 'Đừng vì thành công đến muộn hơn so với người khác mà nghi ngờ khả năng của chính mình.',
      date: '06/09/2021',
      link: 'https://stu.edu.vn/vi/267/17954/dung-vi-thanh-cong-den-muon-hon-so-voi-nguoi-khac-ma-nghi-ngo-kha-nang-cua-chinh-minh.html'
    },
    {
      id: 3,
      title: 'Tại sao người thành công thức dậy trước 6h?',
      date: '27/08/2021',
      link: 'https://stu.edu.vn/vi/267/17953/tai-sao-nguoi-thanh-cong-thuc-day-truoc-6h.html'
    }
  ]

  const departments = [
    'Khoa Công Nghệ Thông Tin',
    'Khoa Kinh tế - Quản trị',
    'Khoa Design'
  ]

  return (
    <div className="career-page">
      {/* Header */}
      <LandingHeader />
      <HeroSlider slides={DEFAULT_SLIDES} />

      {/* Main Content */}
      <main className="career-main">
        <div className="container">
          {/* Hướng Nghiệp Section */}
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
                      href={item.link} 
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

          {/* Học - Thi - Tuyển sinh */}
          <section className="exam-section">
            <div className="section-header">
              <h2 className="section-title">Học - Thi - Tuyển sinh</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="exam-list">
              {examItems.map((item) => (
                <article key={item.id} className="exam-item">
                  {item.isNew && <span className="badge-new">mới</span>}
                  <h3 className="exam-title">{item.title}</h3>
                  <span className="exam-date">{item.date}</span>
                  <a 
                    href={item.link} 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="exam-link"
                  >
                    Chi tiết
                  </a>
                </article>
              ))}
            </div>
          </section>

          {/* Nhu Cầu Nguồn Nhân Lực */}
          <section className="labor-section">
            <div className="section-header">
              <h2 className="section-title">Nhu Cầu Nguồn Nhân Lực</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="labor-list">
              {laborItems.map((item) => (
                <article key={item.id} className="labor-item">
                  <h3 className="labor-title">{item.title}</h3>
                  <span className="labor-date">{item.date}</span>
                  <a 
                    href={item.link} 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="labor-link"
                  >
                    Chi tiết
                  </a>
                </article>
              ))}
            </div>
          </section>

          {/* Kinh Nghiệm Việc Làm */}
          <section className="experience-section">
            <div className="section-header">
              <h2 className="section-title">Kinh Nghiệm Việc Làm</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="experience-list">
              {experienceItems.map((item) => (
                <article key={item.id} className="experience-item">
                  {item.isNew && <span className="badge-new">mới</span>}
                  <h3 className="experience-title">{item.title}</h3>
                  <span className="experience-date">{item.date}</span>
                  <a 
                    href={item.link} 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="experience-link"
                  >
                    Chi tiết
                  </a>
                </article>
              ))}
            </div>
          </section>

          {/* Gương Sáng */}
          <section className="success-section">
            <div className="section-header">
              <h2 className="section-title">Gương Sáng</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="success-list">
              {successItems.map((item) => (
                <article key={item.id} className="success-item">
                  <h3 className="success-title">{item.title}</h3>
                  <span className="success-date">{item.date}</span>
                  <a 
                    href={item.link} 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="success-link"
                  >
                    Chi tiết
                  </a>
                </article>
              ))}
            </div>
          </section>

          {/* Người Thành Công */}
          <section className="rich-section">
            <div className="section-header">
              <h2 className="section-title">Người Thành Công</h2>
              <Link to="/" className="view-all">Xem tất cả</Link>
            </div>
            <div className="rich-list">
              {richItems.map((item) => (
                <article key={item.id} className="rich-item">
                  {item.isNew && <span className="badge-new">mới</span>}
                  <h3 className="rich-title">{item.title}</h3>
                  <span className="rich-date">{item.date}</span>
                  <a 
                    href={item.link} 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="rich-link"
                  >
                    Chi tiết
                  </a>
                </article>
              ))}
            </div>
          </section>
        </div>
      </main>

      <LandingFooter />
    </div>
  )
}

export default Career

