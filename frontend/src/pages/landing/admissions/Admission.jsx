import React from 'react'
import { Link } from 'react-router-dom'
import LandingHeader from '../../../components/landing/LandingHeader'
import HeroSlider from '../../../components/landing/HeroSlider'
import LandingFooter from '../../../components/landing/LandingFooter'
import { DEFAULT_SLIDES } from '../../../config/landingConstants'
import './Admission.css'

const Admission = () => {

  const undergraduateMajors = [
    { code: '7510203', name: 'Công nghệ kỹ thuật cơ điện tử', year: '2004' },
    { code: '7510301', name: 'Công nghệ kỹ thuật điện, điện tử', year: '2004' },
    { code: '7510302', name: 'Công nghệ kỹ thuật điện tử - viễn thông', year: '2004' },
    { code: '7480201', name: 'Công nghệ thông tin', year: '2004' },
    { code: '7540101', name: 'Công nghệ thực phẩm', year: '2004' },
    { code: '7580201', name: 'Kỹ thuật xây dựng', year: '2004' },
    { code: '7340101', name: 'Quản trị kinh doanh', year: '2004' },
    { code: '7210402', name: 'Thiết kế công nghiệp', year: '2007' },
    { code: '7510201', name: 'Công nghệ kỹ thuật cơ khí', year: '2025' },
    { code: '7480106', name: 'Kỹ thuật máy tính', year: '2025' },
    { code: '7540106', name: 'Đảm bảo chất lượng và an toàn thực phẩm', year: '2025' },
    { code: '7340115', name: 'Marketing', year: '2025' },
    { code: '7340120', name: 'Kinh doanh quốc tế', year: '2025' },
    { code: '7340201', name: 'Tài chính - Ngân hàng', year: '2025' },
    { code: '7510605', name: 'Logistics và Quản lý chuỗi cung ứng', year: '2025' },
    { code: '7810101', name: 'Du lịch', year: '2025' },
    { code: '7580302', name: 'Quản lý xây dựng', year: '2025' },
    { code: '7380107', name: 'Luật kinh tế', year: '2025' }
  ]

  const transferMajors = [
    { name: 'Công nghệ Kỹ thuật Cơ điện tử', code: '7510203', duration: '2 năm liên thông đại học cấp bằng kỹ sư', startYear: '2005', recentYear: '2024' },
    { name: 'Công nghệ Kỹ thuật Điện, Điện tử', code: '7510301', duration: '2 năm liên thông đại học cấp bằng kỹ sư', startYear: '2005', recentYear: '2024' },
    { name: 'Công nghệ Kỹ thuật Điện tử Viễn thông', code: '7510302', duration: '2 năm liên thông đại học cấp bằng kỹ sư', startYear: '2005', recentYear: '2024' },
    { name: 'Công nghệ Thông tin', code: '7480201', duration: '2 năm liên thông đại học cấp bằng kỹ sư', startYear: '2005', recentYear: '2024' },
    { name: 'Công nghệ Thực phẩm', code: '7540101', duration: '2 năm liên thông đại học cấp bằng kỹ sư', startYear: '2005', recentYear: '2024' },
    { name: 'Kỹ thuật Xây dựng', code: '7580201', duration: '2 năm liên thông đại học cấp bằng kỹ sư', startYear: '2005', recentYear: '2024' },
    { name: 'Quản trị Kinh doanh', code: '7340101', duration: '2 năm liên thông đại học cấp bằng cử nhân', startYear: '2005', recentYear: '2024' },
    { name: 'Thiết kế Công nghiệp', code: '7210402', duration: '2 năm liên thông đại học cấp bằng cử nhân', startYear: '2007', recentYear: '2024' }
  ]

  const departments = [
    'Khoa Công Nghệ Thông Tin',
    'Khoa Kinh tế - Quản trị',
    'Khoa Design'
  ]

  return (
    <div className="admission-page">
      <LandingHeader />
      <HeroSlider slides={DEFAULT_SLIDES} />

      {/* Main Content */}
      <main className="admission-main">
        <div className="container">
          {/* Undergraduate Majors Table */}
          <section className="majors-section">
            <h2 className="section-title">Thông tin danh mục ngành đào tạo đại học được phép đào tạo chính quy:</h2>
            <div className="table-wrapper">
              <table className="majors-table">
                <thead>
                  <tr>
                    <th>STT</th>
                    <th>Mã ngành</th>
                    <th>Tên ngành</th>
                    <th>Năm bắt đầu đào tạo</th>
                  </tr>
                </thead>
                <tbody>
                  {undergraduateMajors.map((major, index) => (
                    <tr key={index}>
                      <td>{index + 1}</td>
                      <td>{major.code}</td>
                      <td>{major.name}</td>
                      <td>{major.year}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>

          {/* Transfer Majors Table */}
          <section className="majors-section">
            <h2 className="section-title">Thông tin danh mục được phép đào tạo liên thông đại học chính quy từ cao đẳng:</h2>
            <div className="table-wrapper">
              <table className="majors-table">
                <thead>
                  <tr>
                    <th>STT</th>
                    <th>Tên ngành</th>
                    <th>Mã ngành</th>
                    <th>Thời gian đào tạo</th>
                    <th>Năm bắt đầu đào tạo</th>
                    <th>Năm đã tuyển sinh và đào tạo gần nhất</th>
                  </tr>
                </thead>
                <tbody>
                  {transferMajors.map((major, index) => (
                    <tr key={index}>
                      <td>{index + 1}</td>
                      <td>{major.name}</td>
                      <td>{major.code}</td>
                      <td>{major.duration}</td>
                      <td>{major.startYear}</td>
                      <td>{major.recentYear}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </main>

      <LandingFooter />
    </div>
  )
}

export default Admission

