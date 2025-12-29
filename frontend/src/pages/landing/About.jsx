import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../components/landing/LandingHeader'
import HeroSlider from '../../components/landing/HeroSlider'
import LandingFooter from '../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../config/landingConstants'
import './About.css'

const About = () => {

  const introItems = [
    {
      id: 1,
      title: 'Cơ Cấu Tổ Chức',
      date: '1.7.2025',
      description: 'Hiện nay nhà trường tổ chức theo hệ thống hai cấp : Trường và Khoa, Phòng, Ban Trung tâm. Bên cạnh đó, còn có một số bộ môn trực thuộc trường.',
      image: 'http://images.stu.edu.vn/image.php?src=/uploads/news/co-cau-to-chuc2-82118c3b81.jpg&w=400&aoe=1',
      link: 'https://stu.edu.vn/vi/2/20462/co-cau-to-chuc.html'
    },
    {
      id: 2,
      title: 'Sứ mạng, tầm nhìn',
      date: '5.31.2023',
      description: '1. Sứ mạng: Trường Đại học Công nghệ Sài Gòn là cơ sở đào tạo đa ngành, đa lĩnh vực, đa hệ, với các trình độ: Đại học, cao học và nghiên cứu sinh; Cung cấp nguồn nhân lực chất lượng...',
      image: 'http://images.stu.edu.vn/image.php?src=/uploads/news/stu2-e353870e41.jpg&w=400&aoe=1',
      link: 'https://stu.edu.vn/vi/2/19273/su-mang-tam-nhin.html'
    },
    {
      id: 3,
      title: 'Triết lý giáo dục',
      date: '5.31.2023',
      description: 'Triết lý giáo dục của Trường đại học Công nghệ Sài Gòn (STU) là: Chất lượng – Trách nhiệm – Chính trực – Hợp tác – Sáng tạo',
      image: 'http://images.stu.edu.vn/image.php?src=/uploads/news/stu3-8ae61f06a3.jpg&w=400&aoe=1',
      link: 'https://stu.edu.vn/vi/2/19272/triet-ly-giao-duc.html'
    },
    {
      id: 4,
      title: 'Hội đồng sáng lập',
      date: '5.31.2023',
      description: 'Tháng 9/1997, 10 nhà sáng lập là những nhà giáo, nhà khoa học, doanh nhân cùng lý tưởng đã gắn kết, phối hợp để xây dựng đề án và tiến tới thành lập Trường Cao đẳng Kỹ nghệ dân lập...',
      image: 'http://images.stu.edu.vn/image.php?src=/uploads/news/bia-5-a778c4effc-ddc1f966b6.png&w=400&aoe=1',
      link: 'https://stu.edu.vn/vi/2/19270/hoi-dong-sang-lap.html'
    },
    {
      id: 5,
      title: 'Hội đồng trường',
      date: '8.14.2024',
      description: 'Hội đồng trường Trường Đại học Công nghệ Sài Gòn là tổ chức quản trị, thực hiện quyền đại diện cho nhà đầu tư và các bên có lợi ích liên quan. Hội đồng trường có trách nhiệm xây dựng...',
      image: 'http://images.stu.edu.vn/image.php?src=/uploads/news/anh-nho-hoi-dong-truong-e463805c85.png&w=400&aoe=1',
      link: 'https://stu.edu.vn/vi/2/19268/hoi-dong-truong.html'
    },
    {
      id: 6,
      title: 'Ban Giám hiệu',
      date: '8.14.2024',
      description: 'Văn phòng Ban Giám hiệu có chức năng tham mưu và giúp việc cho Ban Giám hiệu Nhà trường (chịu sự chỉ đạo trực tiếp của Hiệu trưởng) trong quản lý, thực hiện công tác tổ chức, điều hành công việc, chỉ đạo phối hợp hoạt động của các đơn vị thuộc, trực thuộc toàn Trường trong công tác tham mưu giúp việc theo nhiệm vụ.',
      image: 'http://images.stu.edu.vn/image.php?src=/uploads/news/anh-nho-hieu-truong-e8b3665312.png&w=400&aoe=1',
      link: 'https://stu.edu.vn/vi/2/19267/ban-giam-hieu.html'
    }
  ]

  const videos = [
    {
      id: 1,
      title: 'STU - Nơi chắp cánh những ước mơ',
      thumbnail: 'http://images.stu.edu.vn/image.php?src=/uploads/banner/images/ms56-4-a827e8c3bd.jpg&h=150&aoe=1'
    },
    {
      id: 2,
      title: 'Đánh thức đam mê, Chinh phục ước mơ - Bí kíp "Bẻ khóa tương lai" cùng STU',
      thumbnail: 'http://images.stu.edu.vn/image.php?src=/uploads/banner/images/dsc0254-a403b0b891.jpg&h=150&aoe=1'
    },
    {
      id: 3,
      title: 'STU - Ngày tốt nghiệp',
      thumbnail: 'https://images.stu.edu.vn/image.php?src=/uploads/banner/images/ba115e88aa527667d1a15de78ca372f9.png&h=150&aoe=1'
    }
  ]

  const departments = [
    'Khoa Công Nghệ Thông Tin',
    'Khoa Kinh tế - Quản trị',
    'Khoa Design'
  ]

  return (
    <div className="about-page">
      <LandingHeader />
      <HeroSlider slides={DEFAULT_SLIDES} />

      {/* Main Content */}
      <main className="about-main">
        <div className="container">
          {/* Introduction Items */}
          <section className="intro-section">
            <div className="intro-grid">
              {introItems.map((item) => (
                <article key={item.id} className="intro-card">
                  {item.image && (
                    <div className="intro-image">
                      <img src={item.image} alt={item.title} />
                    </div>
                  )}
                  <div className="intro-header">
                    <span className="intro-date">{item.date}</span>
                    <h3 className="intro-title">{item.title}</h3>
                  </div>
                  {item.description && (
                    <p className="intro-description">{item.description}</p>
                  )}
                  <a 
                    href={item.link} 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="intro-link"
                  >
                    Chi tiết
                  </a>
                </article>
              ))}
            </div>
          </section>

          {/* Videos Section */}
          <section className="videos-section">
            <div className="section-header">
              <h2 className="section-title">Videos</h2>
            </div>
            <div className="videos-grid">
              {videos.map((video) => (
                <div key={video.id} className="video-card">
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
          </section>
        </div>
      </main>

      <LandingFooter />
    </div>
  )
}

export default About

