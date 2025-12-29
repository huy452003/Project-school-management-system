import React, { useState, useEffect } from 'react'
import { useAuth } from '../../contexts/AuthContext'
import apiService from '../../services/apiService'
import './InfoStudent.css'

const InfoStudent = () => {
  const { user } = useAuth()
  const [studentInfo, setStudentInfo] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (user?.userId) {
      loadStudentInfo(false) // Lần đầu không cần force refresh
    }
  }, [user])

  const loadStudentInfo = async (forceRefresh = false) => {
    try {
      setLoading(true)
      setError('')
      
      if (!user?.userId) {
        setError('Không tìm thấy thông tin người dùng')
        return
      }
      
      // Sử dụng endpoint mới getByUserId để lấy thông tin sinh viên theo userId
      const response = await apiService.getStudentByUserId(user.userId, forceRefresh)
      console.log('getStudentByUserId response:', response)
      console.log('response.data:', response.data)
      
      // Response structure: { status, message, modelName, errors, data }
      // data chứa EntityModel với user và profileData
      const studentData = response.data?.data || response.data
      
      if (studentData) {
        console.log('Student data found:', studentData)
        console.log('studentData.user?.profileData:', studentData.user?.profileData)
        setStudentInfo(studentData)
      } else {
        setError('Không tìm thấy thông tin sinh viên')
      }
    } catch (err) {
      console.error('Load student info error:', err)
      console.error('Error response:', err.response)
      if (err.response?.status === 404) {
        setError('Không tìm thấy thông tin sinh viên. Vui lòng liên hệ quản trị viên.')
      } else {
        setError(err.response?.data?.message || 'Không thể tải thông tin sinh viên')
      }
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="grades-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Đang tải thông tin...</p>
        </div>
      </div>
    )
  }

  if (error && !studentInfo) {
    return (
      <div className="grades-page">
        <div className="error-container">
          <div className="error-icon">⚠️</div>
          <h2>Không thể tải thông tin</h2>
          <p>{error}</p>
          <button onClick={() => loadStudentInfo(true)} className="retry-btn">
            🔄 Thử lại
          </button>
        </div>
      </div>
    )
  }

  if (!studentInfo) {
    return (
      <div className="grades-page">
        <div className="error-container">
          <div className="error-icon">📋</div>
          <h2>Chưa có thông tin</h2>
          <p>Thông tin sinh viên của bạn chưa được tạo trong hệ thống.</p>
        </div>
      </div>
    )
  }

  // Lấy profileData từ studentInfo.user.profileData (backend đã put tất cả vào đây)
  const profileData = studentInfo.user?.profileData || {}
  const userInfo = studentInfo.user || {}
  
  // Debug log để kiểm tra data
  console.log('=== DEBUG INFO STUDENT ===')
  console.log('studentInfo:', JSON.stringify(studentInfo, null, 2))
  console.log('profileData:', profileData)
  console.log('profileData.score:', profileData.score)
  console.log('profileData.schoolClass:', profileData.schoolClass)
  console.log('profileData.major:', profileData.major)
  console.log('profileData.graduate:', profileData.graduate)
  console.log('userInfo:', userInfo)

  return (
    <div className="grades-page">
      <div className="page-header">
        <h1>👤 Thông Tin Sinh Viên</h1>
        <button onClick={() => loadStudentInfo(true)} className="refresh-btn">
          🔄 Làm mới
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="student-info-container">
        {/* Card thông tin học tập */}
        <div className="info-card academic-info">
          <div className="card-header">
            <h3>🎓 Thông Tin Học Tập</h3>
          </div>
          <div className="info-grid">
            <div className="info-item highlight">
              <div className="info-icon">⭐</div>
              <div className="info-content">
                <span className="info-label">Điểm trung bình</span>
                <span className="info-value score-value">
                  {(() => {
                    const score = profileData?.score
                    console.log('Rendering score:', score, 'type:', typeof score)
                    if (score !== null && score !== undefined) {
                      // Format số để loại bỏ số 0 thừa ở cuối
                      const numScore = typeof score === 'number' ? score : parseFloat(score)
                      if (!isNaN(numScore)) {
                        // Chuyển sang string và loại bỏ số 0 thừa ở cuối
                        return numScore.toString().replace(/\.?0+$/, '')
                      }
                      return score.toString()
                    }
                    return 'Chưa có'
                  })()}
                </span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">🏫</div>
              <div className="info-content">
                <span className="info-label">Lớp</span>
                <span className="info-value">
                  {profileData?.schoolClass || 'Chưa có thông tin'}
                </span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">📚</div>
              <div className="info-content">
                <span className="info-label">Chuyên ngành</span>
                <span className="info-value">
                  {profileData?.major || 'Chưa có thông tin'}
                </span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">🎯</div>
              <div className="info-content">
                <span className="info-label">Tình trạng tốt nghiệp</span>
                <span className={`info-value status-badge ${
                  profileData?.graduate === true || profileData?.graduate === 'true' ? 'graduated' : 'studying'
                }`}>
                  {profileData?.graduate === true || profileData?.graduate === 'true' ? '✅ Đã tốt nghiệp' : '📖 Đang học'}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Card Khảo sát */}
        <div className="info-card survey-info">
          <div className="card-header">
            <h3>📊 Khảo Sát</h3>
          </div>
          <div className="info-grid">
            <div className="info-item">
              <div className="info-icon">📝</div>
              <div className="info-content">
                <span className="info-label">Khảo sát chất lượng giảng dạy</span>
                <span className="info-value">Chưa thực hiện</span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">📋</div>
              <div className="info-content">
                <span className="info-label">Khảo sát cơ sở vật chất</span>
                <span className="info-value">Chưa thực hiện</span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">💬</div>
              <div className="info-content">
                <span className="info-label">Khảo sát ý kiến sinh viên</span>
                <span className="info-value">Chưa thực hiện</span>
              </div>
            </div>
          </div>
        </div>

        {/* Card Quy định - Quy chế */}
        <div className="info-card regulation-info">
          <div className="card-header">
            <h3>📜 Quy Định - Quy Chế</h3>
          </div>
          <div className="info-grid">
            <div className="info-item">
              <div className="info-icon">📖</div>
              <div className="info-content">
                <span className="info-label">Quy chế đào tạo</span>
                <span className="info-value">
                  <a href="#" className="link-value">Xem chi tiết</a>
                </span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">📚</div>
              <div className="info-content">
                <span className="info-label">Quy định học tập</span>
                <span className="info-value">
                  <a href="#" className="link-value">Xem chi tiết</a>
                </span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">⚖️</div>
              <div className="info-content">
                <span className="info-label">Quy chế thi cử</span>
                <span className="info-value">
                  <a href="#" className="link-value">Xem chi tiết</a>
                </span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">🎓</div>
              <div className="info-content">
                <span className="info-label">Quy định tốt nghiệp</span>
                <span className="info-value">
                  <a href="#" className="link-value">Xem chi tiết</a>
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Card Học phí - Phí dịch vụ */}
        <div className="info-card fee-info">
          <div className="card-header">
            <h3>💰 Học Phí - Phí Dịch Vụ</h3>
          </div>
          <div className="info-grid">
            <div className="info-item highlight">
              <div className="info-icon">💵</div>
              <div className="info-content">
                <span className="info-label">Học phí học kỳ hiện tại</span>
                <span className="info-value fee-value">5.000.000 VNĐ</span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">📅</div>
              <div className="info-content">
                <span className="info-label">Hạn nộp học phí</span>
                <span className="info-value">30/12/2024</span>
              </div>
            </div>
            <div className="info-item">
              <div className="info-icon">✅</div>
              <div className="info-content">
                <span className="info-label">Tình trạng nộp học phí</span>
                <span className="info-value status-badge studying">Đã nộp</span>
              </div>
            </div>
            {/* <div className="info-item">
              <div className="info-icon">💳</div>
              <div className="info-content">
                <span className="info-label">Phí dịch vụ</span>
                <span className="info-value">500.000 VNĐ</span>
              </div>
            </div> */}
            <div className="info-item">
              <div className="info-icon">📊</div>
              <div className="info-content">
                <span className="info-label">Tổng còn nợ</span>
                <span className="info-value">0 VNĐ</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default InfoStudent

