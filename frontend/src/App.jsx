import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import Login from './pages/Login'
import Register from './pages/Register'
import Home from './pages/Home'
import Students from './pages/Students'
import Teachers from './pages/Teachers'
import InfoStudent from './pages/InfoStudent'
import TeacherClasses from './pages/TeacherClasses'
import ProfileEdit from './pages/ProfileEdit'
import ProtectedRoute from './components/ProtectedRoute'
import RoleProtectedRoute from './components/RoleProtectedRoute'
import Layout from './components/Layout'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Layout>
                  <Home />
                </Layout>
              </ProtectedRoute>
            }
          />
          <Route
            path="/students"
            element={
              <RoleProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>
                  <Students />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route
            path="/teachers"
            element={
              <RoleProtectedRoute allowedRoles={['ADMIN']}>
                <Layout>
                  <Teachers />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route
            path="/info-student"
            element={
              <RoleProtectedRoute allowedRoles={['STUDENT']}>
                <Layout>
                  <InfoStudent />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route
            path="/teacher-classes"
            element={
              <RoleProtectedRoute allowedRoles={['TEACHER']}>
                <Layout>
                  <TeacherClasses />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route
            path="/profile-edit"
            element={
              <RoleProtectedRoute allowedRoles={['STUDENT', 'TEACHER']}>
                <Layout>
                  <ProfileEdit />
                </Layout>
              </RoleProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  )
}

export default App

