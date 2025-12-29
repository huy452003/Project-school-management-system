# Modules Parent - Hệ thống Quản lý Nhà trường

Dự án multi-module Spring Boot với frontend React.

## Cấu trúc dự án

- **Backend**: Spring Boot 3.5.0 (Maven multi-module)
  - `security`: Module xác thực và phân quyền
  - `qlsv`: Quản lý sinh viên
  - `qlgv`: Quản lý giáo viên
  - `model_shared`: Models dùng chung
  - `security_shared`: Security utilities
  - `kafka_shared`: Kafka integration
  - `logging`: Logging utilities
  - `handle_exceptions`: Exception handling

- **Frontend**: React 18 + Vite
  - `frontend/`: React application

## Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- Node.js 18+
- Docker (tùy chọn, cho deployment)

## Cài đặt và chạy local

### Backend

```bash
# Build tất cả modules
mvn clean install

# Chạy từng service
cd security
mvn spring-boot:run

cd ../qlsv
mvn spring-boot:run

cd ../qlgv
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Deploy lên web

Xem file [DEPLOY.md](./DEPLOY.md) để biết hướng dẫn chi tiết về deployment.

