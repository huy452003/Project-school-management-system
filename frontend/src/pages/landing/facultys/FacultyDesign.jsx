import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import '../cssToDeparts_Facus.css'

const FacultyDesign = () => {

  const relatedItems = [
    {
      id: 1,
      title: 'SINH VIÊN KHAI GIẢNG NĂM HỌC MỚI VỚI CHECK-IN BẢO TÀNG, \'QUẨY\' VMARK VÀ BÙNG NỔ KINH NGHIỆM THỰC CHIẾN, NGÀNH THIẾT KẾ SẢN PHẨM STU ĐANG CÓ GÌ MÀ CHẤT THẾ?',
      date: '12/12/2025',
      link: 'https://stu.edu.vn/vi/439/21220/sinh-vien-khai-giang-nam-hoc-moi-voi-checkin-bao-tang-quay-vmark-va-bung-no-kinh-nghiem-thuc-chien-nganh-thiet-ke-san-pham-stu-dang-co-gi-ma-chat-the.html'
    },
    {
      id: 2,
      title: 'Triển lãm Online GƯƠNG 2025 - Khoa Design STU',
      date: '26/11/2025',
      link: 'https://stu.edu.vn/vi/439/21189/trien-lam-online-guong-2025-khoa-design-stu.html'
    },
    {
      id: 3,
      title: 'TRIỂN LÃM TRANH ĐẠT GIẢI "STU ART VISION 2025" – HÀNH TRÌNH LAN TỎA SẮC MÀU SÁNG TẠO CỦA SINH VIÊN KHOA DESIGN',
      date: '26/11/2025',
      link: 'https://stu.edu.vn/vi/439/21186/trien-lam-tranh-dat-giai-stu-art-vision-2025-hanh-trinh-lan-toa-sac-mau-sang-tao-cua-sinh-vien-khoa-design.html'
    },
    {
      id: 4,
      title: 'KHÁM PHÁ SỨC HÚT TỪ MÔN HỌC "MINH HỌA NHÂN VẬT" CỦA SINH VIÊN KHOA DESIGN STU',
      date: '21/08/2025',
      link: 'https://stu.edu.vn/vi/439/20993/kham-pha-suc-hut-tu-mon-hoc-minh-hoa-nhan-vat-cua-sinh-vien-khoa-design-stu.html'
    },
    {
      id: 5,
      title: 'Design không chỉ vẽ – Sinh viên Design còn biết viết, trình bày và phản biện',
      date: '15/08/2025',
      link: 'https://stu.edu.vn/vi/439/20981/design-khong-chi-ve-sinh-vien-design-con-biet-viet-trinh-bay-va-phan-bien.html'
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
              <h1 className="page-title">Khoa Thiết kế</h1>

              {/* GIỚI THIỆU KHOA */}
              <section className="functions-section">
                <div className="section-header">
                  <h2 className="section-title">GIỚI THIỆU KHOA</h2>
                  <span className="update-date">Cập nhật 11/04/2024 - 05:31:02 PM (GMT+7)</span>
                </div>
                <div className="functions-content">
                  <p className="intro-text">
                    Được thành lập từ đầu năm 2007, Khoa Design được Nhà trường chú trọng đầu tư phát triển mạnh mẽ. Với tên gọi DESIGN – cách sử dụng phổ biến ở nhiều nước trên thế giới dùng cho các Trường có nhóm ngành Thiết kế, cũng chính là mong muốn <strong>tạo sự khác biệt</strong> của những người chủ trì những ngày đầu thành lập Khoa.
                  </p>
                  <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="http://images.stu.edu.vn/ckfinder/uploads/links/Khoa%20Design/DESIGN/Design-02-tap-the-scaled.jpg" 
                          className="activity-image"
                        />
                      </div>
                    </div>
                  <p className="function-text">
                    Khi thế giới ngày càng phẳng, quá trình toàn cầu hóa kinh tế kéo theo quá trình toàn cầu hóa về mọi mặt đời sống xã hội. Một thế giới mới đi vào "luật chơi chung", từ đó đặc thù công việc, giải pháp kết nối, hợp tác của con người được nâng lên một tầm cao mới. Lúc này, giá trị của sự khác biệt tạo cơ hội tồn tại và phát huy bản sắc riêng.
                  </p>
                  <p className="function-text">
                    Với nỗ lực trở thành một trong những địa chỉ đào tạo ngành Thiết kế Công nghiệp có chất lượng và uy tín hàng đầu tại khu vực phía Nam, Khoa Design liên tục cập nhật các nội dung và phương pháp đào tạo mới nhất từ các nước tiên tiến, đồng thời Khoa có những điều chỉnh để bám sát nhu cầu thực tiễn. Hòa nhập cùng xu thế chung của thế giới hiện nay, triết lý '<strong>Thiết kế cho phát triển bền vững'</strong> được STU lựa chọn là tiêu chí nền tảng để đào tạo các nhà thiết kế trẻ tương lai.
                  </p>
                  <p className="function-text">
                    Song song với các kỹ năng chính trong chuyên môn của lĩnh vực Design, hệ thống các môn học được thiết kế nhằm rèn luyện cho sinh viên các kỹ năng phong phú hỗ trợ cho quá trình làm việc thực tiễn như: kỹ năng nghiên cứu, kỹ năng giao tiếp và thuyết trình, kỹ năng hoạch định, kỹ năng làm việc nhóm, kỹ năng sáng tác và kỹ năng khởi nghiệp.
                  </p>
                  <p className="function-text">
                    Hiện nay, Khoa Design đã xây dựng hoàn chỉnh chương trình đào tạo với 04 chuyên ngành:
                  </p>
                  <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="http://images.stu.edu.vn/ckfinder/uploads/links/Khoa%20Design/DESIGN/thiet-ke-do-hoa.png" 
                          alt="Thiết kế Đồ họa" 
                          className="activity-image"
                        />
                        <p className="activity-caption">Thiết kế Đồ họa</p>
                      </div>
                    </div>
                    <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="http://images.stu.edu.vn/ckfinder/uploads/links/Khoa%20Design/DESIGN/thiet-ke-noi-that.jpg" 
                          alt="Thiết kế Nội thất" 
                          className="activity-image"
                        />
                        <p className="activity-caption">Thiết kế Nội thất</p>
                      </div>
                    </div>
                    <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="http://images.stu.edu.vn/ckfinder/uploads/links/Khoa%20Design/DESIGN/thiet-ke-tao-dang-san-pham.jpg" 
                          alt="Thiết kế Tạo dáng sản phẩm công nghiệp" 
                          className="activity-image"
                        />
                        <p className="activity-caption">Thiết kế Tạo dáng sản phẩm công nghiệp</p>
                      </div>
                    </div>
                    <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="http://images.stu.edu.vn/ckfinder/uploads/links/Khoa%20Design/DESIGN/thiet-ke-thoi-trang.jpg" 
                          alt="Thiết kế Thời trang" 
                          className="activity-image"
                        />
                        <p className="activity-caption">Thiết kế Thời trang</p>
                      </div>
                    </div>

                  <div className="function-block" style={{ marginTop: '30px' }}>
                    <h3 className="function-heading">CƠ HỘI NGHỀ NGHIỆP VÀ THỰC HÀNH</h3>
                    <p className="function-text">
                      Sinh viên Khoa Design sau khi tốt nghiệp có kiến thức về Mỹ thuật, thẩm mỹ, nghệ thuật hỗ trợ cho việc phát triển ý tưởng và tư duy sáng tạo trong thiết kế, có kiến thức và kỹ năng chuyên ngành, liên ngành, có kỹ năng diễn họa bằng tay, bằng máy tính hoặc mô hình để trình bày ý tưởng. Sinh viên ra trường nắm được phương pháp xây dựng quy trình thiết kế và đảm nhận tốt các khâu trong quy trình thiết kế chuyên ngành mà mình đã được học.
                    </p>
                    <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="http://images.stu.edu.vn/ckfinder/uploads/links/Khoa%20Design/DESIGN/AD8.jpg" 
                          alt="Sinh viên năm 4: Hà kiều Oanh, lớp D18-TK2 chuyên ngành thiết kế Thời trang – là 1 trong 7 Nhà thiết kế trẻ được chọn lọc để tham gia trình diễn Fashion Voyage Designer 2021 - Chủ đề Chasing The Sun" 
                          className="activity-image"
                        />
                        <p className="activity-caption">Sinh viên năm 4: Hà kiều Oanh, lớp D18-TK2 chuyên ngành thiết kế Thời trang – là 1 trong 7 Nhà thiết kế trẻ được chọn lọc để tham giatrình diễn Fashion Voyage Designer 2021 - Chủ đề Chasing The Sun</p>
                      </div>
                    </div>
                    <p className="function-text">
                      Với từng chuyên ngành đào tạo, Sinh viên sau khi ra trường trở thành các nhà thiết kế làm việc trong các lĩnh vực Kiến trúc Nội thất, Đồ họa, Tạo dáng sản phẩm và Thời trang, mặt khác, với khát vọng lập nghiệp và cống hiến – đặc trưng văn hóa Design - STU, sinh viên ra trường có thể khởi nghiệp trong chính chuyên ngành được đào tạo hoặc các ngành liên quan, phát triển hoạt động kinh doanh, dịch vụ, văn hóa – nghệ thuật.
                    </p>
                  </div>

                  <div className="function-block" style={{ marginTop: '30px' }}>
                    <h3 className="function-heading">LỜI KẾT</h3>
                    <p className="function-text">
                      Mười lăm năm hình thành và phát triển, với bao khó khăn và thay đổi, tập thể cán bộ giảng viên, nhân viên Khoa Design vẫn nỗ lực từng ngày, vượt qua thử thách để đạt được một số kết quả như ngày hôm nay. Nhìn những em sinh viên bước đầu gặt hái được thành công với nghề ngay khi còn ngồi trên ghế nhà trường, và những thế hệ sinh viên ra trường trở thành các nhà thiết kế trẻ triển vọng, hoặc theo hướng thành lập và điều hành các doanh nghiệp thiết kế thi công, rồi quay lại mái nhà Design STU để tài trợ, giúp đỡ đàn em... Đó là niềm tự hào, niềm vui lớn giúp các giảng viên Khoa Design thêm yêu nghề, thêm gắn bó và tận tâm với sự nghiệp "trồng người" thiêng liêng mà mình đã lựa chọn.
                    </p>
                    <p className="function-text">
                      Dự kiến trong mười năm tới, thị trường việc làm ngày càng năng động, đặc biệt trong lĩnh vực Design, công nghệ thay đổi từng ngày kéo theo sự kết hợp giữa Nghệ thuật và Kỹ thuật ngày càng đa dạng và phong phú. Với định hướng phát triển bền vững, trở thành cơ sở đào tạo Design có chất lượng và uy tín, có bản sắc riêng trong vô số các cơ sở cùng ngành, Khoa Design Trường Đại học Công nghệ Sài Gòn quyết tâm <strong>Đổi mới Sáng tạo</strong>, đổi mới từ chính từng nhân sự trong khoa, đổi mới môi trường học tập và làm việc thêm gắn kết, đổi mới phương pháp giảng dạy thêm sáng tạo, trao quyền cho Giảng viên để trở thành những người định hướng, dẫn dắt tận tâm; trao quyền cho sinh viên để rèn luyện trở thành những nhà thiết kế kiến tạo, góp phần làm đẹp và thay đổi tích cực cho xã hội.
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
                  <li><Link to="/">Trắc nghiệm chọn nghề</Link></li>
                  <li><Link to="/">Trắc nghiệm IQ</Link></li>
                  <li><Link to="/admission">Tuyển sinh thạc sĩ</Link></li>
                  <li><Link to="/admission">Tuyển sinh Đại học</Link></li>
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

export default FacultyDesign

