import React, { useState, useEffect } from 'react'
import { useAuth } from '../../contexts/AuthContext'
import apiService from '../../services/apiService'
import './TeacherClasses.css'

const TeacherClasses = () => {
  const { user } = useAuth()
  const [classes, setClasses] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedClass, setSelectedClass] = useState(null)
  const [students, setStudents] = useState([])
  const [allStudents, setAllStudents] = useState([]) // Lưu tất cả students cho client-side pagination
  const [isClientPagination, setIsClientPagination] = useState(false) // Flag để biết đang dùng client-side pagination
  const [loadingStudents, setLoadingStudents] = useState(false)
  // Pagination state
  const [currentPage, setCurrentPage] = useState(0)
  const [pageSize] = useState(10)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  // Modal update score state
  const [showUpdateScoreModal, setShowUpdateScoreModal] = useState(false)
  const [selectedStudent, setSelectedStudent] = useState(null)
  const [scoreInput, setScoreInput] = useState('')
  const [updatingScore, setUpdatingScore] = useState(false)
  const [scoreError, setScoreError] = useState('')

  useEffect(() => {
    if (user?.userId) {
      loadClasses(false) // Lần đầu không cần force refresh
    }
  }, [user])

  const loadClasses = async (forceRefresh = false) => {
    try {
      setLoading(true)
      setError('')
      
      console.log('loadClasses called with forceRefresh:', forceRefresh)
      
      // Load teacher info từ QLGV service để có profileData đầy đủ
      let teacherInfo = null
      try {
        const response = await apiService.getTeacherByUserId(user.userId, forceRefresh)
        console.log('getTeacherByUserId response:', response)
        teacherInfo = response.data || response
      } catch (err) {
        console.error('Error loading teacher info:', err)
        setError('Không thể tải thông tin giáo viên')
        setLoading(false)
        return
      }

      const profileData = teacherInfo?.user?.profileData || {}
      const classManaging = profileData.classManaging

      if (!classManaging) {
        setError('Bạn chưa được phân công quản lý lớp nào')
        setLoading(false)
        return
      }

      // classManaging có thể là string hoặc array
      let classList = []
      if (typeof classManaging === 'string') {
        // Nếu là string, có thể là danh sách phân cách bởi dấu phẩy
        classList = classManaging.split(',').map(c => c.trim()).filter(c => c)
      } else if (Array.isArray(classManaging)) {
        classList = classManaging
      } else {
        setError('Định dạng lớp quản lý không hợp lệ')
        setLoading(false)
        return
      }

      if (classList.length === 0) {
        setError('Bạn chưa được phân công quản lý lớp nào')
        setLoading(false)
        return
      }

      // Lấy số lượng students cho mỗi lớp
      const classesWithCount = await Promise.all(
        classList.map(async (className) => {
          try {
            // Truyền forceRefresh để xóa cache khi cần
            const response = await apiService.getStudentsByClassManaging(className, forceRefresh)
            
            // Response structure từ backend: Response<List<EntityModel>>
            // Axios interceptor trả về response.data (từ axios response)
            // Vậy response = { status, message, modelName, errors, data: [students] }
            let students = []
            
            // Xử lý nhiều trường hợp response structure
            if (Array.isArray(response)) {
              // Nếu response là array trực tiếp
              students = response
            } else if (response?.data) {
              if (Array.isArray(response.data)) {
                students = response.data
              } else if (response.data?.data && Array.isArray(response.data.data)) {
                // Nested structure
                students = response.data.data
              }
            }
            
            return {
              className: className,
              studentCount: students.length
            }
          } catch (err) {
            console.error(`Error loading students for class ${className}:`, err)
            return {
              className: className,
              studentCount: 0,
              error: err.response?.data?.message || 'Không thể tải dữ liệu'
            }
          }
        })
      )

      setClasses(classesWithCount)
    } catch (err) {
      console.error('Load classes error:', err)
      setError(err.response?.data?.message || 'Không thể tải danh sách lớp')
    } finally {
      setLoading(false)
    }
  }

  const handleViewDetails = async (className, page = 0, forceRefresh = false) => {
    try {
      setLoadingStudents(true)
      setSelectedClass(className)
      setCurrentPage(page)
      setIsClientPagination(false)
      
      // Fetch students with pagination - thêm timestamp để bypass cache nếu forceRefresh
      const response = await apiService.getStudentsPaged(page, pageSize, 'id', 'asc', className, forceRefresh)
      
      // Response structure: { status, message, modelName, errors, data: PagedResponseModel }
      const pagedData = response?.data || response
      
      if (pagedData && pagedData.data) {
        const studentsList = pagedData.data || []
        setStudents(studentsList)
        setAllStudents([])
        setTotalPages(pagedData.totalPages || 0)
        setTotalElements(pagedData.totalElements || 0)
      } else {
        throw new Error('Invalid response from paginated API')
      }
    } catch (err) {
      console.error('Error loading students:', err)
      // Fallback to non-paginated API if pagination fails - dùng client-side pagination
      try {
        const response = await apiService.getStudentsByClassManaging(className, forceRefresh) // Dùng forceRefresh nếu cần
        let studentsList = []
        if (Array.isArray(response)) {
          studentsList = response
        } else if (response?.data) {
          if (Array.isArray(response.data)) {
            studentsList = response.data
          } else if (response.data?.data && Array.isArray(response.data.data)) {
            studentsList = response.data.data
          }
        }
        
        // Lưu tất cả students và set flag client-side pagination
        setAllStudents(studentsList)
        setIsClientPagination(true)
        
        // Slice dữ liệu theo page hiện tại
        const startIndex = page * pageSize
        const endIndex = startIndex + pageSize
        setStudents(studentsList.slice(startIndex, endIndex))
        
        setTotalPages(Math.ceil(studentsList.length / pageSize))
        setTotalElements(studentsList.length)
      } catch (fallbackErr) {
        console.error('Fallback API also failed:', fallbackErr)
        setStudents([])
        setAllStudents([])
        setTotalPages(0)
        setTotalElements(0)
        alert('Không thể tải danh sách sinh viên: ' + (err.response?.data?.message || err.message))
      }
    } finally {
      setLoadingStudents(false)
    }
  }

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages && selectedClass) {
      if (isClientPagination && allStudents.length > 0) {
        // Client-side pagination - không cần gọi API
        setCurrentPage(newPage)
        const startIndex = newPage * pageSize
        const endIndex = startIndex + pageSize
        setStudents(allStudents.slice(startIndex, endIndex))
      } else {
        // Server-side pagination - gọi API
        handleViewDetails(selectedClass, newPage)
      }
    }
  }

  const handleCloseModal = () => {
    setSelectedClass(null)
    setStudents([])
    setAllStudents([])
    setIsClientPagination(false)
    setCurrentPage(0)
    setTotalPages(0)
    setTotalElements(0)
  }

  const handleOpenUpdateScoreModal = (student) => {
    const profileData = student?.user?.profileData || {}
    const currentScore = profileData?.score
    setSelectedStudent(student)
    setScoreInput(currentScore !== null && currentScore !== undefined ? currentScore.toString() : '')
    setScoreError('')
    setShowUpdateScoreModal(true)
  }

  const handleCloseUpdateScoreModal = () => {
    setShowUpdateScoreModal(false)
    setSelectedStudent(null)
    setScoreInput('')
    setScoreError('')
  }

  const handleUpdateScore = async () => {
    if (!selectedStudent) return

    // Validation
    setScoreError('')
    const scoreValue = scoreInput.trim()
    
    if (scoreValue === '') {
      setScoreError('Vui lòng nhập điểm số')
      return
    }

    const numScore = parseFloat(scoreValue)
    if (isNaN(numScore)) {
      setScoreError('Điểm số phải là số hợp lệ')
      return
    }

    if (numScore < 0 || numScore > 10) {
      setScoreError('Điểm số phải trong khoảng 0 đến 10')
      return
    }

    try {
      setUpdatingScore(true)
      
      // Lấy student id từ student object
      const studentId = selectedStudent.id || selectedStudent.user?.userId
      if (!studentId) {
        throw new Error('Không tìm thấy ID sinh viên')
      }

      // Chuẩn bị data để update - format giống như trong Students.jsx
      const updateData = {
        id: studentId,
        user: {
          userId: selectedStudent.user?.userId,
          firstName: selectedStudent.user?.firstName,
          lastName: selectedStudent.user?.lastName,
          age: selectedStudent.user?.age,
          gender: selectedStudent.user?.gender,
          email: selectedStudent.user?.email,
          phoneNumber: selectedStudent.user?.phoneNumber,
          birth: selectedStudent.user?.birth,
          profileData: {
            ...selectedStudent.user?.profileData,
            score: numScore
          }
        }
      }

      // Gọi API update
      await apiService.updateStudent(studentId, updateData)

      // Update local state trực tiếp để UI cập nhật ngay lập tức
      setStudents(prevStudents => 
        prevStudents.map(student => {
          if (student.id === studentId || student.user?.userId === selectedStudent.user?.userId) {
            return {
              ...student,
              user: {
                ...student.user,
                profileData: {
                  ...student.user?.profileData,
                  score: numScore
                }
              }
            }
          }
          return student
        })
      )

      // Cũng update allStudents nếu đang dùng client-side pagination
      if (allStudents.length > 0) {
        setAllStudents(prevAllStudents =>
          prevAllStudents.map(student => {
            if (student.id === studentId || student.user?.userId === selectedStudent.user?.userId) {
              return {
                ...student,
                user: {
                  ...student.user,
                  profileData: {
                    ...student.user?.profileData,
                    score: numScore
                  }
                }
              }
            }
            return student
          })
        )
      }

      // Refresh từ server trong background để đảm bảo data đồng bộ
      if (selectedClass) {
        handleViewDetails(selectedClass, currentPage, true).catch(err => {
          console.error('Error refreshing data:', err)
          // Không hiển thị lỗi cho user vì đã update local state
        })
      }

      // Đóng modal
      handleCloseUpdateScoreModal()
      
      alert('Cập nhật điểm số thành công!')
    } catch (err) {
      console.error('Error updating score:', err)
      setScoreError(err.response?.data?.message || 'Không thể cập nhật điểm số. Vui lòng thử lại.')
    } finally {
      setUpdatingScore(false)
    }
  }

  if (loading) {
    return (
      <div className="teacher-classes-page">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Đang tải thông tin lớp giảng dạy...</p>
        </div>
      </div>
    )
  }

  if (error && classes.length === 0) {
    return (
      <div className="teacher-classes-page">
        <div className="error-container">
          <div className="error-icon">⚠️</div>
          <h2>Không thể tải thông tin</h2>
          <p>{error}</p>
          <button onClick={() => loadClasses(true)} className="retry-btn">
            🔄 Thử lại
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="teacher-classes-page">
      <div className="page-header">
        <h1>📚 Các Lớp Giảng Dạy</h1>
        <button onClick={() => loadClasses(true)} className="refresh-btn">
          🔄 Làm mới
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="classes-container">
        <div className="info-card">
          <div className="card-header">
            <h3>📋 Danh Sách Lớp Quản Lý</h3>
          </div>
          <div className="table-container">
            <table className="classes-table">
              <thead>
                <tr>
                  <th>STT</th>
                  <th>Lớp</th>
                  <th>Tổng số sinh viên</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {classes.length === 0 ? (
                  <tr>
                    <td colSpan="4" className="empty-message">
                      Không có lớp nào được phân công
                    </td>
                  </tr>
                ) : (
                  classes.map((classItem, index) => (
                    <tr key={index}>
                      <td>{index + 1}</td>
                      <td>
                        <span className="class-name">{classItem.className}</span>
                      </td>
                      <td>
                        <span className={`student-count ${classItem.error ? 'error' : ''}`}>
                          {classItem.error ? (
                            <span className="error-text" title={classItem.error}>
                              Lỗi: {classItem.error}
                            </span>
                          ) : (
                            <span className="count-badge">{classItem.studentCount}</span>
                          )}
                        </span>
                      </td>
                      <td>
                        <button
                          onClick={() => handleViewDetails(classItem.className)}
                          className="view-details-btn"
                          disabled={classItem.error}
                        >
                          👁️ Xem chi tiết
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Modal hiển thị danh sách students */}
      {selectedClass && (
        <div className="modal-overlay" onClick={handleCloseModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>📋 Danh Sách Sinh Viên - Lớp {selectedClass}</h2>
              <button className="modal-close-btn" onClick={handleCloseModal}>
                ✕
              </button>
            </div>
            <div className="modal-body">
              {loadingStudents ? (
                <div className="loading-students">
                  <div className="loading-spinner"></div>
                  <p>Đang tải danh sách sinh viên...</p>
                </div>
              ) : students.length === 0 ? (
                <div className="empty-students">
                  <p>Không có sinh viên nào trong lớp này</p>
                </div>
              ) : (
                <div className="students-table-container">
                  <table className="students-table">
                    <thead>
                      <tr>
                        <th>STT</th>
                        <th>Họ và tên</th>
                        <th>Tuổi</th>
                        <th>Giới tính</th>
                        <th>Email</th>
                        <th>Số điện thoại</th>
                        <th>Điểm số</th>
                        <th>Tình trạng tốt nghiệp</th>
                        <th>Thao tác</th>
                      </tr>
                    </thead>
                    <tbody>
                      {students.map((student, index) => {
                        const user = student?.user || {}
                        const profileData = user?.profileData || {}
                        const score = profileData?.score
                        const graduate = profileData?.graduate
                        
                        const fullName = `${user.firstName || ''} ${user.lastName || ''}`.trim()
                        const gender = user?.gender
                        const genderDisplay = gender === 'NAM' ? 'Nam' : gender === 'NU' ? 'Nữ' : gender || '-'
                        
                        return (
                          <tr key={student?.id || index}>
                            <td>{currentPage * pageSize + index + 1}</td>
                            <td title={fullName || '-'}>
                              {fullName || '-'}
                            </td>
                            <td>{user.age || '-'}</td>
                            <td>{genderDisplay}</td>
                            <td title={user.email || '-'}>
                              {user.email || '-'}
                            </td>
                            <td>{user.phoneNumber || '-'}</td>
                            <td>
                              {score !== null && score !== undefined
                                ? parseFloat(score).toString().replace(/\.?0+$/, '')
                                : '-'}
                            </td>
                            <td>
                              {graduate ? 'Đã tốt nghiệp' : 'Chưa tốt nghiệp'}
                            </td>
                            <td>
                              <button
                                onClick={() => handleOpenUpdateScoreModal(student)}
                                className="update-score-btn"
                                title="Cập nhật điểm số"
                              >
                                ✏️ Cập nhật điểm
                              </button>
                            </td>
                          </tr>
                        )
                      })}
                    </tbody>
                  </table>
                  
                  {/* Pagination Controls */}
                  {totalPages > 1 && (
                    <div className="pagination-container">
                      <div className="pagination-info">
                        Trang {currentPage + 1} / {totalPages} ({totalElements} sinh viên)
                      </div>
                      <div className="pagination-buttons">
                        <button
                          className="pagination-btn"
                          onClick={() => handlePageChange(0)}
                          disabled={currentPage === 0}
                        >
                          ⏮️ Đầu
                        </button>
                        <button
                          className="pagination-btn"
                          onClick={() => handlePageChange(currentPage - 1)}
                          disabled={currentPage === 0}
                        >
                          ◀️ Trước
                        </button>
                        {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                          let pageNum
                          if (totalPages <= 5) {
                            pageNum = i
                          } else if (currentPage < 3) {
                            pageNum = i
                          } else if (currentPage > totalPages - 4) {
                            pageNum = totalPages - 5 + i
                          } else {
                            pageNum = currentPage - 2 + i
                          }
                          return (
                            <button
                              key={pageNum}
                              className={`pagination-btn ${currentPage === pageNum ? 'active' : ''}`}
                              onClick={() => handlePageChange(pageNum)}
                            >
                              {pageNum + 1}
                            </button>
                          )
                        })}
                        <button
                          className="pagination-btn"
                          onClick={() => handlePageChange(currentPage + 1)}
                          disabled={currentPage >= totalPages - 1}
                        >
                          Sau ▶️
                        </button>
                        <button
                          className="pagination-btn"
                          onClick={() => handlePageChange(totalPages - 1)}
                          disabled={currentPage >= totalPages - 1}
                        >
                          Cuối ⏭️
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Modal cập nhật điểm số */}
      {showUpdateScoreModal && selectedStudent && (
        <div className="modal-overlay" onClick={handleCloseUpdateScoreModal}>
          <div className="modal-content score-update-modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>✏️ Cập Nhật Điểm Số</h2>
              <button className="modal-close-btn" onClick={handleCloseUpdateScoreModal}>
                ✕
              </button>
            </div>
            <div className="modal-body">
              <div className="score-update-form">
                <div className="student-info-section">
                  <h3>Thông tin sinh viên:</h3>
                  <div className="student-info-grid">
                    <div className="info-item">
                      <span className="info-label">Họ và tên:</span>
                      <span className="info-value">
                        {`${selectedStudent.user?.firstName || ''} ${selectedStudent.user?.lastName || ''}`.trim() || '-'}
                      </span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">Lớp:</span>
                      <span className="info-value">{selectedClass || '-'}</span>
                    </div>
                    <div className="info-item">
                      <span className="info-label">Điểm hiện tại:</span>
                      <span className="info-value">
                        {selectedStudent.user?.profileData?.score !== null && selectedStudent.user?.profileData?.score !== undefined
                          ? parseFloat(selectedStudent.user.profileData.score).toString().replace(/\.?0+$/, '')
                          : 'Chưa có điểm'}
                      </span>
                    </div>
                  </div>
                </div>
                
                <div className="score-input-section">
                  <label htmlFor="score-input">
                    Điểm số mới <span className="required">*</span>
                  </label>
                  <input
                    id="score-input"
                    type="number"
                    min="0"
                    max="10"
                    step="0.1"
                    value={scoreInput}
                    onChange={(e) => {
                      setScoreInput(e.target.value)
                      setScoreError('')
                    }}
                    placeholder="Nhập điểm số (0 - 10)"
                    className={scoreError ? 'error' : ''}
                    disabled={updatingScore}
                  />
                  {scoreError && (
                    <div className="error-message">{scoreError}</div>
                  )}
                  <div className="input-hint">
                    Điểm số phải trong khoảng 0 đến 10
                  </div>
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button
                className="btn-cancel"
                onClick={handleCloseUpdateScoreModal}
                disabled={updatingScore}
              >
                Hủy
              </button>
              <button
                className="btn-save"
                onClick={handleUpdateScore}
                disabled={updatingScore}
              >
                {updatingScore ? 'Đang lưu...' : '💾 Lưu'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TeacherClasses

