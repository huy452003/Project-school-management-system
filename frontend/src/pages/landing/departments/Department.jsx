import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import '../cssToDeparts_Facus.css'

const Department = () => {

  const functions = [
    {
      id: 1,
      title: 'Tổ chức, quản lý và điều hành việc triển khai về tổ chức và kế hoạch để thực hiện chương trình đào tạo đã được phê duyệt.',
      items: [
        'Quản lý chương trình đào tạo và đề cương các môn học đã được phê duyệt.',
        'Lịch trình giảng dạy và học tập.',
        'Tổ chức lớp.',
        'Sắp xếp thời khóa biểu, thi cử.',
        'Khối lượng và kế hoạch chi tiết giảng dạy /học tập.',
        'Mời giáo viên giảng dạy theo đề nghị của Khoa có thông qua ý kiến của Phó Hiệu trưởng phụ trách về đào tạo.',
        'Nắm chắc khối lượng công tác giảng dạy, nhu cầu về phòng học, thí nghiệm, thực hành.'
      ]
    },
    {
      id: 2,
      title: 'Tổ chức, quản lý và điều hành thực hiện các Quy chế của Bộ Giáo dục và Đào tạo và của Trường về học vụ, về hồ sơ, về tuyển sinh và tốt nghiệp của học sinh – sinh viên.'
    },
    {
      id: 3,
      title: 'Giúp Ban Giám hiệu và các Khoa trong việc quan hệ với các cơ quan ngoài trường phục vụ cho công tác đào tạo.'
    },
    {
      id: 4,
      title: 'Tổ chức, quản lý và điều hành việc thu thập thông tin để nhận xét, đánh giá về :',
      items: [
        'Thực thi chương trình kế hoạch đào tạo.',
        'Thực hiện các Quy chế của Bộ và của Trường.',
        'Tình hình giảng dạy của giáo viên và học tập của học sinh – sinh viên và những ý kiến, yêu cầu của giáo viên và học sinh – sinh viên về giảng dạy, học tập.'
      ]
    },
    {
      id: 5,
      title: 'Tổ chức, quản lý và điều hành việc xây dựng và thực thi hệ thống các quy trình, quy định, mẫu mã, phân nhiệm để thực hiện, để kiểm tra và đánh giá công tác đã được giao nhiệm vụ cho phòng. Luôn xem xét kịp thời, bổ sung, sửa đổi và xây dựng mới.'
    },
    {
      id: 6,
      title: 'Dự toán kinh phí hoạt động, các phương tiện cần thiết đáp ứng yêu cầu công tác của Phòng.'
    },
    {
      id: 7,
      title: 'Đề xuất, tham mưu cho Ban Giám hiệu những biện pháp nhằm nâng cao chất lượng, hiệu quả công tác giáo dục – đào tạo toàn diện trong mọi hoạt động của nhà trường.'
    },
    {
      id: 8,
      title: 'Giúp Ban Giám hiệu thực hiện các báo cáo cấp trên về giáo dục – đào tạo theo nội dung và thời điểm quy định.'
    },
    {
      id: 9,
      title: 'Báo cáo định kỳ các nội dung công tác được giao cho Ban Giám hiệu.'
    },
    {
      id: 10,
      title: 'Trưởng phòng là người chịu trách nhiệm tổ chức, quản lý và điều hành các nhiệm vụ trên.'
    }
  ]

  const relatedItems = [
    {
      id: 1,
      title: 'Danh sách cán bộ nhân viên Phòng Đào Tạo',
      date: '06/08/2012',
      link: 'https://stu.edu.vn/vi/370/8977/danh-sach-can-bo-nhan-vien-phong-dao-tao.html'
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
              <h1 className="page-title">Giới Thiệu Phòng Đào Tạo</h1>

              {/* Chức năng - Nhiệm vụ */}
              <section className="functions-section">
                <div className="section-header">
                  <h2 className="section-title">Chức năng - Nhiệm vụ</h2>
                  <span className="update-date">Cập nhật 08/04/2010 - 01:00:00 AM (GMT+7)</span>
                </div>
                <div className="functions-content">
                  <p className="intro-text">
                    Nhận ủy nhiệm của Thầy Hiệu trưởng; tổ chức, quản lý và điều hành việc triển khai thực hiện chương trình, kế hoạch đào tạo và quản lý học vụ.
                  </p>
                  <ol className="functions-list">
                    {functions.map((func) => (
                      <li key={func.id} className="function-item">
                        <span className="function-title">{func.title}</span>
                        {func.items && (
                          <ol className="sub-items">
                            {func.items.map((item, idx) => (
                              <li key={idx}>{item}</li>
                            ))}
                          </ol>
                        )}
                      </li>
                    ))}
                  </ol>
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
                  <li><Link to="/app">Cổng thông tin đào tạo</Link></li>
                  <li><Link to="/">Tra Cứu Dữ Liệu Tốt Nghiệp</Link></li>
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

export default Department

