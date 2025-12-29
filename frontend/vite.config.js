import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // Cho phép truy cập từ bên ngoài container
    port: 3000,
    // Cho phép tất cả hosts (cho Railway và custom domain)
    allowedHosts: [
      'localhost',
      '.railway.app',
      'huyk3school.up.railway.app',
      'huyk3school.net.vn',
      'project-school-management-system-production.up.railway.app'
    ],
    proxy: {
      '/api': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  // Preview config cho production build
  preview: {
    host: '0.0.0.0',
    port: 3000,
    allowedHosts: [
      'localhost',
      '.railway.app',
      'huyk3school.up.railway.app',
      'huyk3school.net.vn',
      'project-school-management-system-production.up.railway.app'
    ]
  }
})

