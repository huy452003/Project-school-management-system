import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import LandingPage from './pages/landing/LandingPage'
import About from './pages/landing/About'
import Admission from './pages/landing/admissions/Admission'
import Career from './pages/landing/admissions/Career'
import Consultation from './pages/landing/admissions/Consultation'
import Department from './pages/landing/departments/Department'
import Finance from './pages/landing/departments/Finance'
import StudentAffairs from './pages/landing/departments/StudentAffairs'
import FacultyIT from './pages/landing/facultys/FacultyIT'
import FacultyEconomics from './pages/landing/facultys/FacultyEconomics'
import FacultyDesign from './pages/landing/facultys/FacultyDesign'
import Library from './pages/landing/Library'
import Contact from './pages/landing/Contact'
import Login from './pages/app/Login'
import Register from './pages/app/Register'
import Home from './pages/app/Home'
import Students from './pages/app/Students'
import Teachers from './pages/app/Teachers'
import InfoStudent from './pages/app/InfoStudent'
import TeacherClasses from './pages/app/TeacherClasses'
import ProfileEdit from './pages/app/ProfileEdit'
import ProtectedRoute from './components/app/ProtectedRoute'
import RoleProtectedRoute from './components/app/RoleProtectedRoute'
import Layout from './components/app/Layout'
import ScrollToTop from './components/landing/ScrollToTop'
import ScrollToTopButton from './components/landing/ScrollToTopButton'

function App() {
  return (
    <AuthProvider>
      <Router>
        <ScrollToTop />
        <ScrollToTopButton />
        <Routes>
          {/* Landing Page - Public */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/about" element={<About />} />
          <Route path="/admission" element={<Admission />} />
          <Route path="/career" element={<Career />} />
          <Route path="/consultation" element={<Consultation />} />
          <Route path="/department" element={<Department />} />
          <Route path="/finance" element={<Finance />} />
          <Route path="/student-affairs" element={<StudentAffairs />} />
          <Route path="/faculty-it" element={<FacultyIT />} />
          <Route path="/faculty-economics" element={<FacultyEconomics />} />
          <Route path="/faculty-design" element={<FacultyDesign />} />
          <Route path="/library" element={<Library />} />
          <Route path="/contact" element={<Contact />} />
          
          {/* Backend Routes - Protected */}
          <Route path="/app/login" element={<Login />} />
          <Route path="/app/register" element={<Register />} />
          <Route
            path="/app"
            element={
              <ProtectedRoute>
                <Layout>
                  <Home />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/app/students"
            element={
              <RoleProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>
                  <Students />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route
            path="/app/teachers"
            element={
              <RoleProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>
                  <Teachers />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route
            path="/app/info-student"
            element={
              <RoleProtectedRoute allowedRoles={['STUDENT']}>
                <Layout>
                  <InfoStudent />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route
            path="/app/teacher-classes"
            element={
              <RoleProtectedRoute allowedRoles={['TEACHER']}>
                <Layout>
                  <TeacherClasses />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route
            path="/app/profile-edit"
            element={
              <RoleProtectedRoute allowedRoles={['STUDENT', 'TEACHER']}>
                <Layout>
                  <ProfileEdit />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          
          {/* Redirect old routes to new app routes */}
          <Route path="/login" element={<Navigate to="/app/login" replace />} />
          <Route path="/register" element={<Navigate to="/app/register" replace />} />
          
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  )
}

export default App

