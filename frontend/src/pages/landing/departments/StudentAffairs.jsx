import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import img1 from '../../../img/img1.jpg'
import '../cssToDeparts_Facus.css'

const StudentAffairs = () => {

  const activities = [
    {
      id: 1,
      title: 'Tuyên truyền các chủ trương, đường lối của Đảng, chính sách, pháp luật của nhà nước, giáo dục truyền thống đối với sinh viên trường thông qua các lớp học Tuần sinh hoạt công dân sinh viên, các cuộc thi, chương trình đối thoại sinh viên.',
      images: [
        'http://images.stu.edu.vn/ckfinder/uploads/links/files/Ph%C3%B2ng%20CTSV/_MG_9771.JPG',
        'http://images.stu.edu.vn/ckfinder/uploads/links/files/Ph%C3%B2ng%20CTSV/_MG_9905.JPG'
      ],
      captions: [
        'Sinh viên tham gia Tuần SHCD',
        'Sinh viên tham quan công viên VH lịch sử Đền Hùng'
      ]
    },
    {
      id: 2,
      title: 'Hướng dẫn thực hiện các chính sách của Nhà nước đối với sinh viên về học bổng, miễn giảm học phí, trợ cấp xã hội và các chế độ khác có liên quan đến sinh viên.',
      images: [
        'http://images.stu.edu.vn/ckfinder/uploads/links/files/Ph%C3%B2ng%20CTSV/_DSC6658.JPG'
      ],
      captions: [
        'Sinh viên nhận học bổng tại Lễ khai giảng năm học.'
      ]
    },
    {
      id: 3,
      title: 'Tổ chức các hoạt động hỗ trợ, tư vấn hướng nghiệp, ngày hội việc làm cho sinh viên.',
      images: [
        'http://images.stu.edu.vn/ckfinder/uploads/links/files/Ph%C3%B2ng%20CTSV/007_6559.JPG'
      ],
      captions: [
        'Sinh viên tham gia ngày hội việc làm'
      ]
    },
    {
      id: 4,
      title: 'Phối hợp cùng với các Khoa, Phòng Ban, Đoàn Thanh niên và Hội sinh viên tổ chức cho sinh viên tham gia các hoạt động chính trị - xã hội, văn hóa, văn nghệ, thể dục - thể thao, các hoạt động ngoài giờ lên lớp, các lớp bồi dưỡng kiến thức cơ bản về kỹ năng sống, cử đoàn sinh viên tham dự các hội thi học thuật, Olympic, nghiên cứu khoa học các cấp trong sinh viên.',
      images: [
        'http://images.stu.edu.vn/ckfinder/uploads/links/files/Ph%C3%B2ng%20CTSV/_MG_8939.JPG',
        img1
      ],
      captions: [
        'Sinh viên tại Liên hoan văn nghệ STU',
        'Sinh viên tham gia lễ bế mạc hội thao'
      ]
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
              <h1 className="page-title">Giới thiệu Phòng Công tác Sinh viên</h1>

              {/* Giới thiệu */}
              <section className="functions-section">
                <div className="section-header">
                  <h2 className="section-title">Chức năng - Nhiệm vụ</h2>
                  <span className="update-date">Cập nhật 08/05/2020 - 01:00:00 AM (GMT+7)</span>
                </div>
                <div className="functions-content">
                  <p className="intro-text">
                    Phòng Công tác Sinh viên có nhiệm vụ tham mưu cho Hiệu trưởng và thực hiện những chủ trương biện pháp giúp sinh viên rèn luyện, không ngừng tiến bộ và phát triển con người toàn diện, phát triển tài năng trong thời gian theo học tại trường. Phòng tiến hành các công tác tuyên truyền, phổ biến, học tập các đường lối chính sách, pháp luật trong sinh viên; Phối hợp với Đoàn Thanh niên và Hội Sinh viên hỗ trợ sinh viên trong rèn luyện đạo đức, nhân cách, tư vấn về các mặt học tập, nghề nghiệp, đời sống tâm lý cho sinh viên, tổ chức đời sống tinh thần, văn nghệ, thể thao vui chơi lành mạnh, phong phú.
                  </p>

                  {/* Activities */}
                  <div className="activities-list">
                    {activities.map((activity) => (
                      <div key={activity.id} className="activity-item">
                        <p className="activity-text">{activity.title}</p>
                        <div className="activity-images">
                          {activity.images.map((img, idx) => (
                            <div key={idx} className="activity-image-wrapper">
                              <img src={img} alt={activity.captions[idx]} className="activity-image" />
                              <p className="activity-caption">{activity.captions[idx]}</p>
                            </div>
                          ))}
                        </div>
                      </div>
                    ))}
                  </div>

                  <p className="additional-text">
                    Ngoài các hoạt động trên, Phòng CTSV còn theo dõi, thực hiện công tác đánh giá kết quả rèn luyện sinh viên theo từng học kỳ, năm học và toàn khóa học, tham gia vào hội đồng xét khen thưởng và kỷ luật sinh viên, quản lý và thực hiện các quy định của Bộ Giáo dục và Đào tạo cũng như của Nhà trường đối với sinh viên như: Quy định quản lý sinh viên ngoại trú, Quy định thực hiện ngày Công tác xã hội, Quy định công tác Y tế học đường, ... Các thủ tục hành chính, cấp phát, xác nhận các loại giấy tờ theo nhu cầu của sinh viên như xác nhận vay vốn ngân hàng, xác nhận hoãn nghĩa vụ quân sự, ...
                  </p>
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
                  <li><Link to="/">Bản đồ đến STU</Link></li>
                  <li><Link to="/">Công tác sinh viên</Link></li>
                  <li><Link to="/app">Cổng thông tin đào tạo</Link></li>
                  <li><Link to="/">Trắc nghiệm chọn nghề</Link></li>
                  <li><Link to="/">Trắc nghiệm IQ</Link></li>
                  <li><Link to="/">Web STU - phiên bản cũ</Link></li>
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

export default StudentAffairs

