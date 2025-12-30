// Cấu hình API endpoints
// Sử dụng environment variables cho production, fallback về localhost cho development
const getBaseURL = (service) => {
  const envKey = `VITE_${service}_BASE_URL`;
  const envValue = import.meta.env[envKey];
  
  if (envValue) {
    return envValue;
  }
  
  // Fallback URLs cho development
  const devUrls = {
    SECURITY: 'http://localhost:8083',
    QLSV: 'http://localhost:8080',
    QLGV: 'http://localhost:8081',
  };
  
  return devUrls[service] || 'http://localhost:8080';
};

const API_CONFIG = {
  // Security service (port 8083)
  SECURITY_BASE_URL: getBaseURL('SECURITY'),
  // QLSV service (port mặc định 8080)
  QLSV_BASE_URL: getBaseURL('QLSV'),
  // QLGV service (port 8081)
  QLGV_BASE_URL: getBaseURL('QLGV'),
  TIMEOUT: 10000,
}

// Debug: Log API URLs in development
if (import.meta.env.DEV) {
  console.log('API Configuration:', {
    SECURITY_BASE_URL: API_CONFIG.SECURITY_BASE_URL,
    QLSV_BASE_URL: API_CONFIG.QLSV_BASE_URL,
    QLGV_BASE_URL: API_CONFIG.QLGV_BASE_URL,
    VITE_SECURITY_BASE_URL: import.meta.env.VITE_SECURITY_BASE_URL,
  })
}

export default API_CONFIG

