import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import '../cssToDeparts_Facus.css'

const FacultyIT = () => {

  const relatedItems = [
    {
      id: 1,
      title: 'Giới thiệu Khoa Công Nghệ Thông Tin',
      date: '28/04/2020',
      link: 'https://stu.edu.vn/vi/388/11124/gioi-thieu-khoa-cong-nghe-thong-tin.html'
    },
    {
      id: 2,
      title: 'Sứ mạng, Tầm nhìn, Triết lý giáo dục của Khoa CNTT',
      date: '16/02/2018',
      link: 'https://stu.edu.vn/vi/388/19026/su-mang-tam-nhin-triet-ly-giao-duc-cua-khoa-cntt.html'
    },
    {
      id: 3,
      title: 'Giới thiệu Khoa Công Nghệ Thông Tin',
      date: '16/02/2011',
      link: 'https://stu.edu.vn/vi/388/3835/gioi-thieu-khoa-cong-nghe-thong-tin.html'
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
              <h1 className="page-title">Giới thiệu khoa</h1>

              {/* Giới thiệu Khoa CNTT */}
              <section className="functions-section">
                <div className="section-header">
                  <h2 className="section-title">Giới thiệu Khoa Công Nghệ Thông Tin</h2>
                  <span className="update-date">Cập nhật 13/08/2025 - 01:11:51 PM (GMT+7)</span>
                </div>
                <div className="functions-content">
                  <p className="intro-text">
                    Khoa Công nghệ Thông tin (CNTT) của Trường Đại học Công nghệ Sài Gòn (STU) được thành lập vào ngày 02/5/2001 theo quyết định số 17A-2001/CĐKN. Qua hơn hai thập kỷ phát triển, Khoa đã khẳng định vị thế là một trong những đơn vị đào tạo CNTT uy tín, cung cấp nguồn nhân lực chất lượng cao cho ngành công nghiệp phần mềm và công nghệ thông tin tại Việt Nam.
                  </p>

                  <div className="function-block">
                    <h3 className="function-heading">Chương trình đào tạo</h3>
                    <p className="function-text">
                      Hiện tại, Khoa đào tạo ngành Công nghệ Thông tin ở bậc đại học với các hướng chuyên môn sâu gồm:
                    </p>
                    <ul className="functions-list" style={{ paddingLeft: '20px', marginTop: '15px' }}>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Lập trình ứng dụng: Phát triển phần mềm trên nền tảng Web, Windows và Mobile.</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Phân tích và thiết kế hệ thống thông tin: Xây dựng, tối ưu hóa hệ thống quản lý doanh nghiệp.</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Quản trị hệ thống thông tin: Bảo mật, quản trị dữ liệu và tối ưu hạ tầng CNTT.</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Ngoài ra, sinh viên cũng được trang bị kiến thức cơ bản về phần cứng, mạng máy tính, an toàn thông tin.</span>
                      </li>
                    </ul>
                    <p className="function-text" style={{ marginTop: '15px' }}>
                      Bên cạnh hệ đại học chính quy, Khoa còn đào tạo hệ liên thông đại học, tạo điều kiện cho sinh viên đã tốt nghiệp cao đẳng tiếp tục nâng cao trình độ.
                    </p>
                  </div>

                  <div className="function-block">
                    <h3 className="function-heading">Cơ sở vật chất hiện đại</h3>
                    <p className="function-text">
                      Khoa Công nghệ Thông tin hiện quản lý 11 phòng máy tính với khoảng 30 máy mỗi phòng, tất cả đều có cấu hình cao, kết nối mạng tốc độ cao phục vụ nhu cầu học tập, thực hành và nghiên cứu.
                    </p>
                    <p className="function-text">
                      Mỗi phòng máy được trang bị máy lạnh, tạo môi trường học tập thoải mái cho giảng viên và sinh viên.
                    </p>
                    <p className="function-text">
                      Hàng năm, Trường và Khoa đều có kế hoạch mua sắm, nâng cấp thiết bị để sinh viên tiếp cận với công nghệ hiện đại, đáp ứng yêu cầu thực tế.
                    </p>
                  </div>

                  <div className="function-block">
                    <h3 className="function-heading">Cam kết chất lượng đào tạo</h3>
                    <p className="function-text">
                      Công tác đảm bảo chất lượng luôn được Khoa chú trọng nhằm đáp ứng nhu cầu thực tiễn của doanh nghiệp và xã hội. Một số giải pháp nâng cao chất lượng đào tạo gồm:
                    </p>
                    <ul className="functions-list" style={{ paddingLeft: '20px', marginTop: '15px' }}>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Rà soát, cập nhật chương trình đào tạo phù hợp với xu hướng công nghệ và nhu cầu nhân lực.</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Phát triển đội ngũ giảng viên có trình độ sau đại học, giàu kinh nghiệm thực tế.</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Tăng cường đào tạo ngoại ngữ, kỹ năng thực hành, kỹ năng mềm cho sinh viên.</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Thúc đẩy nghiên cứu khoa học trong giảng viên và sinh viên.</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Liên kết với doanh nghiệp CNTT, tạo điều kiện thực tập và việc làm sau khi tốt nghiệp.</span>
                      </li>
                    </ul>
                  </div>

                  <div className="function-block">
                    <h3 className="function-heading">Kiểm định chất lượng chương trình đào tạo</h3>
                    <p className="function-text">
                      Ngày 11/01/2024, Trung tâm Kiểm định Chất lượng Giáo dục - ĐHQG TP.HCM đã ký quyết định số 03/QĐ-TTKĐ công nhận đạt chuẩn kiểm định cho chương trình Đào tạo ngành Công nghệ Thông tin trình độ đại học.
                    </p>
                    <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="https://images.stu.edu.vn/ckfinder/uploads/links/Ban%20DamBaoChatLuong/GCN-CNTT.jpg" 
                          alt="Kiểm định chất lượng chương trình đào tạo" 
                          className="activity-image"
                        />
                        <p className="activity-caption">Kiểm định chất lượng chương trình đào tạo</p>
                      </div>
                    </div>
                    <p className="function-text">
                      Buổi lễ công bố và trao chứng nhận kiểm định diễn ra vào ngày 19/01/2024. Sự kiện này đánh dấu bước tiến quan trọng, khẳng định chất lượng đào tạo của ngành CNTT tại STU đạt chuẩn kiểm định quốc gia.
                    </p>
                    <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="http://images.stu.edu.vn/ckfinder/uploads/links/Khoa%20CNTT/STU%20IT%20dat%20kiem%20dinh.jpg" 
                          alt="Lễ công bố và trao chứng nhận kiểm định" 
                          className="activity-image"
                        />
                        <p className="activity-caption">Lễ công bố và trao chứng nhận kiểm định</p>
                      </div>
                    </div>
                    <p className="function-text" style={{ marginTop: '5px', fontWeight: '500', textAlign: 'center' }}>
                      ---
                    </p>
                    <p className="function-text" style={{ marginTop: '5px', fontWeight: '500' }}>
                      Khoa Công nghệ Thông tin – STU cam kết mang đến môi trường học tập hiện đại, chất lượng, giúp sinh viên phát triển toàn diện và sẵn sàng chinh phục thị trường lao động trong thời đại công nghệ số.
                    </p>
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
                  <li><Link to="/">Trắc Nghiệm Chọn Nghề</Link></li>
                  <li><Link to="/">Trắc Nghiệm IQ</Link></li>
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

export default FacultyIT

