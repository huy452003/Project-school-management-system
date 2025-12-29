import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import '../cssToDeparts_Facus.css'

const FacultyEconomics = () => {

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

              {/* Giới thiệu Khoa Kinh tế - Quản trị */}
              <section className="functions-section">
                <div className="section-header">
                  <h2 className="section-title">GIỚI THIỆU KHOA KINH TẾ - QUẢN TRỊ</h2>
                  <span className="update-date">Cập nhật 23/04/2020 - 04:13:01 PM (GMT+7)</span>
                </div>
                <div className="functions-content">
                  <div className="function-block">
                    <h3 className="function-heading">a. Giới thiệu Khoa Kinh tế - Quản trị</h3>
                    <p className="function-text">
                      Tiền thân là khoa <strong>Quản trị Kinh doanh</strong>, được thành lập từ năm 1997 là khoa duy nhất đào tạo ngành kinh doanh – quản lý tại trường đại học Công nghệ Sài Gòn. Chương trình đào tạo được xây dựng theo hướng ứng dụng, giúp sinh viên phát triển tư duy sáng tạo, nắm vững kiến thức, kỹ năng nghề nghiệp và có khả năng thích ứng với sự thay đổi của môi trường kinh doanh thực tế. Đội ngũ giảng viên có năng lực chuyên môn cao, có kinh nghiệm thực tiễn và yêu nghề, cùng hợp tác với các bên liên quan tham gia vào quá trình đào tạo sinh viên để cung cấp nguồn nhân lực đáp ứng yêu cầu của xã hội. Năm 2022 Khoa chính thức đạt Giấy chứng nhận kiểm định chất lượng ngành Quản trị kinh doanh. Điều này khẳng định chất lượng đào tạo của khoa đáp ứng các tiêu chuẩn kiểm định khắt khe, cam kết mang đến môi trường học tập và nghiên cứu tốt nhất cho sinh viên.
                    </p>
                    <p className="function-text">
                      Tháng 10/2025, nhà trường có quyết định đổi tên thành <strong>Khoa Kinh tế - Quản trị</strong> để phù hợp với sự phát triển các ngành học của khoa.
                    </p>
                    <div className="activity-images" style={{ marginTop: '20px', marginBottom: '20px' }}>
                      <div className="activity-image-wrapper">
                        <img 
                          src="http://images.stu.edu.vn/ckfinder/uploads/links/Khoa%20QTKD/BCTT%202025/DSC_6993.JPG" 
                          className="activity-image"
                        />
                      </div>
                    </div>
                    <p className="function-text">
                      Trên cơ sở thực hiện sứ mạng, hướng tới tầm nhìn chung của trường, khoa Kinh tế - Quản trị đã xây dựng tầm nhìn, sứ mạng và triết lý giáo dục như sau:
                    </p>
                    <p className="function-text" style={{ marginTop: '15px' }}>
                      <strong>Tầm nhìn:</strong> Trở thành khoa đào tạo các ngành thuộc lĩnh vực kinh tế - quản trị với chất lượng đào tạo được xã hội thừa nhận.
                    </p>
                    <p className="function-text">
                      <strong>Sứ mạng:</strong> Góp phần cung cấp nguồn nhân lực có chất lượng thuộc khối ngành kinh tế, đáp ứng yêu cầu của xã hội và hội nhập quốc tế.
                    </p>
                    <p className="function-text">
                      <strong>Triết lý giáo dục:</strong> Giỏi chuyên môn, đạo đức nghề nghiệp và trách nhiệm xã hội.
                    </p>
                  </div>

                  <div className="function-block">
                    <h3 className="function-heading">b. Các ngành học thuộc Khoa Kinh tế - Quản trị</h3>
                    <p className="function-text">
                      Từ năm 2025, Khoa KT - QT có các ngành đào tạo sau:
                    </p>
                    <ul className="functions-list" style={{ paddingLeft: '20px', marginTop: '15px' }}>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Ngành Du lịch (gồm 2 chuyên ngành: Du lịch sức khỏe và Quản trị Nhà hàng – Khách sạn)</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Ngành Kinh doanh quốc tế</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Ngành Logistics và Quản lý chuỗi cung ứng</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Ngành Luật kinh tế</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Ngành Marketing (2 chuyên ngành: Quản trị Marketing và Marketing kỹ thuật số)</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Ngành Quản trị kinh doanh</span>
                      </li>
                      <li className="function-item" style={{ marginBottom: '10px' }}>
                        <span className="function-title">Ngành Tài chính - Ngân hàng (2 chuyên ngành: Tài chính doanh nghiệp và Ngân hàng)</span>
                      </li>
                    </ul>
                  </div>

                  <div className="function-block">
                    <h3 className="function-heading">c. Chương trình đào tạo</h3>
                    <p className="function-text">
                      Các chương trình đào tạo chú trọng kỹ năng chuyên môn nhằm giúp sinh viên thích nghi với những thay đổi của môi trường kinh doanh. Từ đó, sinh viên có được năng lực và phẩm chất cần thiết, đạo đức nghề nghiệp và hiểu biết xã hội để làm việc hiệu quả và có khả năng tham gia quản lý doanh nghiệp.
                    </p>
                    <p className="function-text">
                      Chương trình đào tạo cung cấp những kiến thức và kỹ năng mềm qua các phương pháp giảng dạy tích cực: kỹ năng làm việc cơ bản: giao tiếp, thuyết trình, làm việc nhóm và tự học…. Đồng thời tạo điều kiện để sinh viên tham gia thực tập và kiến tập tại các doanh nghiệp, tổ chức.
                    </p>
                    <p className="function-text">
                      Các chương trình đào tạo chú trọng đến đạo đức nghề nghiệp và văn hóa doanh nghiệp với mong muốn người học nhận thức được khi tham gia bất kỳ ngành/ nghề nào trước hết phải chấp hành pháp luật, tôn trọng chữ tín, giữ gìn thương hiệu và hình ảnh quốc gia, sản phẩm và dịch vụ tốt, chất lượng để thỏa mãn nhu cầu xã hội, giúp sinh viên nhận thức được hoạt động kinh doanh không chỉ là lợi ích cho riêng mình, mà cần phải có trách nhiệm với xã hội; để sau khi ra trường trở thành công dân tích cực, hiểu biết xã hội và thể hiện trách nhiệm cụ thể bằng cách tham gia các hoạt động có ích cho cộng đồng trên các lĩnh vực của đời sống xã hội.
                    </p>
                  </div>

                  <div className="function-block">
                    <h3 className="function-heading">d. Câu lạc bộ học thuật</h3>
                    <p className="function-text">
                      Hiện nay khoa Quản trị kinh doanh đang vận hành các câu lạc bộ học thuật: CLB Khởi nghiệp, CLB Quản trị, CLB Marketing, CLB Tài chính – Ngân hàng. Các CLB thường xuyên tổ chức các cuộc thi, các workshop để tạo môi trường học thuật năng động, hữu ích cho sinh viên.
                    </p>
                  </div>

                  <div className="function-block">
                    <h3 className="function-heading">e. Doanh nghiệp hợp tác</h3>
                    <p className="function-text">
                      Các công ty, tổ chức là đối tác tiêu biểu mà sinh viên đã đi thực tập, tham quan kiến tập hoặc đi làm như Tập đoàn Marriott International tại Việt Nam, Ngân hàng Sài Gòn Thương Tín, Công ty chứng khoán Rồng Việt, Công ty Đào tạo và Tư vấn thuế Việt Nam, Công ty Esuhai, Công ty cổ phần MISA…
                    </p>
                  </div>
                </div>
              </section>

              {/* Các Nội Dung Liên Quan */}
              <section className="related-section">
                <h2 className="section-title">Các Nội Dung Liên Quan</h2>
                <div className="related-list">
                  <p className="no-results">Không tìm thấy kết quả nào.</p>
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

export default FacultyEconomics

