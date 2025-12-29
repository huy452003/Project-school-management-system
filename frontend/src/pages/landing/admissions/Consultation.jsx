import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import './Consultation.css'

const Consultation = () => {
  const [selectedWeek, setSelectedWeek] = useState('')
  const [currentPage, setCurrentPage] = useState(1)
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    question: '',
    subscribe: false
  })

  const weeks = [
    'Tuần 1 (01/01/2025 - 07/01/2025)',
    'Tuần 2 (08/01/2025 - 14/01/2025)',
    'Tuần 3 (15/01/2025 - 21/01/2025)',
    'Tuần 4 (22/01/2025 - 28/01/2025)',
    'Tuần 5 (29/01/2025 - 04/02/2025)',
    'Tuần 6 (05/02/2025 - 11/02/2025)',
    'Tuần 7 (12/02/2025 - 18/02/2025)',
    'Tuần 8 (19/02/2025 - 25/02/2025)',
    'Tuần 9 (26/02/2025 - 04/03/2025)',
    'Tuần 10 (05/03/2025 - 11/03/2025)',
    'Tuần 11 (12/03/2025 - 18/03/2025)',
    'Tuần 12 (19/03/2025 - 25/03/2025)',
    'Tuần 13 (26/03/2025 - 01/04/2025)',
    'Tuần 14 (02/04/2025 - 08/04/2025)',
    'Tuần 15 (09/04/2025 - 15/04/2025)',
    'Tuần 16 (16/04/2025 - 22/04/2025)',
    'Tuần 17 (23/04/2025 - 29/04/2025)',
    'Tuần 18 (30/04/2025 - 06/05/2025)',
    'Tuần 19 (07/05/2025 - 13/05/2025)',
    'Tuần 20 (14/05/2025 - 20/05/2025)',
    'Tuần 21 (21/05/2025 - 27/05/2025)',
    'Tuần 22 (28/05/2025 - 03/06/2025)',
    'Tuần 23 (04/06/2025 - 10/06/2025)',
    'Tuần 24 (11/06/2025 - 17/06/2025)',
    'Tuần 25 (18/06/2025 - 24/06/2025)',
    'Tuần 26 (25/06/2025 - 01/07/2025)',
    'Tuần 27 (02/07/2025 - 08/07/2025)',
    'Tuần 28 (09/07/2025 - 15/07/2025)',
    'Tuần 29 (16/07/2025 - 22/07/2025)',
    'Tuần 30 (23/07/2025 - 29/07/2025)',
    'Tuần 31 (30/07/2025 - 05/08/2025)',
    'Tuần 32 (06/08/2025 - 12/08/2025)',
    'Tuần 33 (13/08/2025 - 19/08/2025)',
    'Tuần 34 (20/08/2025 - 26/08/2025)',
    'Tuần 35 (27/08/2025 - 02/09/2025)',
    'Tuần 36 (03/09/2025 - 09/09/2025)',
    'Tuần 37 (10/09/2025 - 16/09/2025)',
    'Tuần 38 (17/09/2025 - 23/09/2025)',
    'Tuần 39 (24/09/2025 - 30/09/2025)',
    'Tuần 40 (01/10/2025 - 07/10/2025)',
    'Tuần 41 (08/10/2025 - 14/10/2025)',
    'Tuần 42 (15/10/2025 - 21/10/2025)',
    'Tuần 43 (22/10/2025 - 28/10/2025)',
    'Tuần 44 (29/10/2025 - 04/11/2025)',
    'Tuần 45 (05/11/2025 - 11/11/2025)',
    'Tuần 46 (12/11/2025 - 18/11/2025)',
    'Tuần 47 (19/11/2025 - 25/11/2025)',
    'Tuần 48 (26/11/2025 - 02/12/2025)',
    'Tuần 49 (03/12/2025 - 09/12/2025)',
    'Tuần 50 (10/12/2025 - 16/12/2025)',
    'Tuần 52 (24/12/2025 - 30/12/2025)'
  ]

  const questions = [
    {
      id: 1,
      question: 'đăng kí tuyển sinh như nào',
      author: 'Lê Văn Pháp',
      answer: 'chào bạn, hiện tại trường nhận hồ sơ theo thời gian quy định của Bộ, bạn theo dõi thông tin trên webstie www.stu.edu.vn hoặc trang fanpage của trường, trường sẽ cập nhật thông tin sớm nhất.\n\nThông tin đến bạn.\n\nBan Tư Vấn',
      date: '22/12/2025'
    },
    {
      id: 2,
      question: 'điểm chuẩn công nghệ thông tin và tuyển sinh nhue nào ạ',
      author: 'Lê Văn Pháp',
      answer: 'chào bạn, hiện tại trường chưa có thông báo điểm chuẩn bạn nhé, điểm nhận hồ sơ đối với phương thức học bạ là 18đ\n\nThông tin đến bạn.\n\nBan Tư Vấn',
      date: '22/12/2025'
    },
    {
      id: 3,
      question: 'học phí 1 năm của trường về các nghành kỹ thuật dao động bao nhiêu ạ',
      author: 'Nguyễn Ngọc Anh Tú',
      answer: 'Chào bạn, Học phí dự kiến năm 2025 khoảng 19.880.300 đồng - 25.954.500 đồng/năm học tùy theo ngành học. Bạn có thể tham khảo thông tin chi tiết tại website của trường.\n\nThông tin đến bạn.\n\nBan Tư Vấn',
      date: '22/12/2025'
    },
    {
      id: 4,
      question: 'Cho e hỏi khi nào trường mới nhận học bạ 3 năm ạ',
      author: 'Nguyễn Văn A',
      answer: 'Chào bạn, Trường sẽ nhận hồ sơ theo thời gian quy định của Bộ từ 16/7 - 28/7, bạn đăng ký xét tuyển, sắp xếp nguyện vọng trên hệ thống Bộ GD&ĐT.\n\nThông tin đến bạn.\n\nBan Tư Vấn',
      date: '22/12/2025'
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
      title: 'STU - Ngày tốt nghiệp',
      thumbnail: 'https://images.stu.edu.vn/image.php?src=/uploads/banner/images/ba115e88aa527667d1a15de78ca372f9.png&h=150&aoe=1'
    },
    {
      id: 3,
      title: 'Đánh thức đam mê, Chinh phục ước mơ - Bí kíp "Bẻ khóa tương lai" cùng STU',
      thumbnail: 'http://images.stu.edu.vn/image.php?src=/uploads/banner/images/dsc0254-a403b0b891.jpg&h=150&aoe=1'
    }
  ]

  const departments = [
    'Khoa Công Nghệ Thông Tin',
    'Khoa Kinh tế - Quản trị',
    'Khoa Design'
  ]

  const itemsPerPage = 3
  const totalPages = Math.ceil(questions.length / itemsPerPage)
  const startIndex = (currentPage - 1) * itemsPerPage
  const displayedQuestions = questions.slice(startIndex, startIndex + itemsPerPage)

  const handleSubmit = (e) => {
    e.preventDefault()
    // Xử lý submit form
    alert('Câu hỏi của bạn đã được gửi. Chúng tôi sẽ trả lời sớm nhất có thể!')
    setFormData({
      name: '',
      email: '',
      phone: '',
      question: '',
      subscribe: false
    })
  }

  return (
    <div className="consultation-page">
      {/* Header */}
      <LandingHeader />
      <HeroSlider slides={DEFAULT_SLIDES} />

      {/* Main Content */}
      <main className="consultation-main">
        <div className="container">
          <div className="consultation-layout">
            {/* Left Column - Q&A */}
            <div className="consultation-content">
              <div className="content-header">
                <h1 className="page-title">Nội Dung Tư Vấn</h1>
                <div className="filter-section">
                  <span className="filter-label">Lọc Tin</span>
                  <div className="filter-controls">
                    <span className="update-info">Cập nhật 22/12/2025, 05:20:47 PM (GMT+7)</span>
                    <div className="week-filter">
                      <label>Tìm theo tháng:</label>
                      <select 
                        value={selectedWeek} 
                        onChange={(e) => setSelectedWeek(e.target.value)}
                        className="week-select"
                      >
                        <option value="">---Chọn---</option>
                        {weeks.map((week, idx) => (
                          <option key={idx} value={week}>{week}</option>
                        ))}
                      </select>
                    </div>
                  </div>
                </div>
              </div>

              {/* Questions List */}
              <div className="questions-list">
                {displayedQuestions.map((item, index) => (
                  <article key={item.id} className="question-item">
                    <div className="question-number">Câu {startIndex + index + 1} ({item.author})</div>
                    <h3 className="question-text">{item.question}</h3>
                    <div className="answer-section">
                      <strong>Trả lời:</strong>
                      <p className="answer-text">{item.answer}</p>
                    </div>
                  </article>
                ))}
              </div>

              {/* Pagination */}
              <div className="pagination">
                <button 
                  className="pagination-btn"
                  onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                  disabled={currentPage === 1}
                >
                  ‹
                </button>
                {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                  <button
                    key={page}
                    className={`pagination-btn ${currentPage === page ? 'active' : ''}`}
                    onClick={() => setCurrentPage(page)}
                  >
                    {page}
                  </button>
                ))}
                <button 
                  className="pagination-btn"
                  onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                  disabled={currentPage === totalPages}
                >
                  ›
                </button>
              </div>
            </div>

            {/* Right Column - Form & Videos */}
            <aside className="consultation-sidebar">
              {/* Ask Question Form */}
              <section className="ask-question-section">
                <h2 className="sidebar-title">Đặt Câu Hỏi</h2>
                <p className="form-note">Xin vui lòng gõ tiếng việt không dấu. Câu trả lời sẽ được đưa lên hệ thống và gởi về địa chỉ mail của bạn.</p>
                <form onSubmit={handleSubmit} className="question-form">
                  <div className="form-group">
                    <label>Thông Tin Cá Nhân</label>
                    <input
                      type="text"
                      placeholder="Họ và tên"
                      value={formData.name}
                      onChange={(e) => setFormData({...formData, name: e.target.value})}
                      required
                    />
                    <input
                      type="email"
                      placeholder="Email"
                      value={formData.email}
                      onChange={(e) => setFormData({...formData, email: e.target.value})}
                      required
                    />
                    <input
                      type="tel"
                      placeholder="Số điện thoại"
                      value={formData.phone}
                      onChange={(e) => setFormData({...formData, phone: e.target.value})}
                    />
                    <small className="form-hint">Thông tin của bạn sẽ được bảo mật an toàn và chỉ sử dụng trong trường hợp cần thiết để liên lạc và hỗ trợ bạn tốt nhất.</small>
                  </div>
                  <div className="form-group">
                    <label>
                      <input
                        type="checkbox"
                        checked={formData.subscribe}
                        onChange={(e) => setFormData({...formData, subscribe: e.target.checked})}
                      />
                      Đăng ký nhận thông tin mới về ngành học qua email
                    </label>
                  </div>
                  <div className="form-group">
                    <label>Nội Dung Câu Hỏi</label>
                    <textarea
                      placeholder="Nhập câu hỏi của bạn..."
                      value={formData.question}
                      onChange={(e) => setFormData({...formData, question: e.target.value})}
                      required
                      rows="5"
                    />
                  </div>
                  <div className="form-group">
                    <label>Captcha:</label>
                    <div className="captcha-placeholder">[Captcha sẽ được tích hợp sau]</div>
                  </div>
                  <div className="form-actions">
                    <button type="submit" className="btn-submit">Gửi</button>
                    <button type="button" className="btn-reset" onClick={() => setFormData({
                      name: '',
                      email: '',
                      phone: '',
                      question: '',
                      subscribe: false
                    })}>
                      Nhập Lại
                    </button>
                  </div>
                </form>
              </section>

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
            </aside>
          </div>
        </div>
      </main>

      <LandingFooter />
    </div>
  )
}

export default Consultation

