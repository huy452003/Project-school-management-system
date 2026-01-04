# Modules Parent - Hệ thống Quản lý Nhà trường

Dự án multi-module Spring Boot với frontend React, hỗ trợ quản lý sinh viên, giáo viên và xác thực người dùng.

## 🌐 Deployment

**Production URLs:**
- **Frontend**: https://huyk3school.up.railway.app
- **Security Service**: https://security-production-e176.up.railway.app (Port 8083)
- **QLSV Service**: https://qlsv-production.up.railway.app (Port 8080)
- **QLGV Service**: https://qlgv-production.up.railway.app (Port 8081)

**Platform**: Railway.app

## 📁 Cấu trúc dự án

### Backend (Spring Boot 3.5.0 - Maven Multi-module)

- **`security/`**: Module xác thực và phân quyền (Port 8083)
  - JWT authentication
  - User registration/login
  - Role-based access control
  
- **`qlsv/`**: Quản lý sinh viên (Port 8080)
  - CRUD operations cho sinh viên
  - Tích hợp với Security service để validate tokens
  
- **`qlgv/`**: Quản lý giáo viên (Port 8081)
  - CRUD operations cho giáo viên
  - Tích hợp với Security service để validate tokens

- **`model_shared/`**: Models dùng chung giữa các modules
- **`security_shared/`**: Security utilities và services dùng chung
- **`kafka_shared/`**: Kafka integration và message models
- **`logging/`**: Logging utilities
- **`handle_exceptions/`**: Exception handling và error responses

### Frontend (React 18 + Vite)

- **`frontend/`**: React application
  - React Router cho navigation
  - Axios cho API calls
  - Responsive UI

## 🔧 Yêu cầu hệ thống

### Bắt buộc

- **Java 17+** (JDK 17 hoặc cao hơn)
- **Maven 3.6+**
- **Node.js 18+** và npm/yarn               - có thẻ cài và chạy qua docker
- **MySQL 8.0+** (hoặc MariaDB 10.5+)       - có thẻ cài và chạy qua docker
- **Redis 6.0+** (cho token caching)        - có thẻ cài và chạy qua docker
- **Apache Kafka 2.8+** (cho message queue) - có thẻ cài và chạy qua docker

### Tùy chọn

- **Docker** và **Docker Compose** (cho deployment hoặc chạy services qua Docker)
- **Git** (để clone repository)

## 📦 Cài đặt và chạy localhost

### Bước 1: Clone repository

```bash
git clone <repository-url>
cd modules-parent
```

### Bước 2: Cài đặt và cấu hình Database

#### MySQL

1. Tạo 3 databases:
```sql
CREATE DATABASE security_module;
CREATE DATABASE qlsv;
CREATE DATABASE qlgv;
```

2. Cấu hình MySQL user (hoặc dùng root):
```sql
CREATE USER 'root'@'localhost' IDENTIFIED BY 'huy12345';
GRANT ALL PRIVILEGES ON security_module.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON qlsv.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON qlgv.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

#### Redis

1. Cài đặt Redis:
   - **Windows**: Download từ https://redis.io/download hoặc dùng WSL
   - **Linux/Mac**: `sudo apt-get install redis-server` hoặc `brew install redis`

2. Khởi động Redis:
```bash
redis-server
```

Mặc định Redis chạy trên `localhost:6379` (không cần password cho local).

#### Kafka

Kafka bản mới (2.8+) không cần Zookeeper nữa (sử dụng KRaft mode).

**Cách 1: Chạy trực tiếp**

1. Download Kafka từ https://kafka.apache.org/downloads (version 2.8+)
2. Khởi động Kafka (không cần Zookeeper):
```bash
# Chỉ cần start Kafka broker
bin/kafka-server-start.sh config/kraft/server.properties
```

**Cách 2: Chạy qua Docker (Khuyến nghị)**

```bash
docker run -d \
  --name kafka \
  -p 9092:9092 \
  apache/kafka:latest
```

Mặc định Kafka chạy trên `localhost:9092`.

**Lưu ý**: 
- Nếu không muốn dùng Kafka, có thể set `KAFKA_ENABLED=false` trong environment variables.
- Nếu dùng Kafka version cũ (< 2.8), vẫn cần Zookeeper.

### Bước 3: Cấu hình Environment Variables (Tùy chọn)

Nếu muốn override default values, tạo file `.env` ở root hoặc set environment variables:

```bash
# Database
DB_USERNAME=root
DB_PASSWORD=huy12345

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_ENABLED=true

# JWT - dành cho JWT token ( security )
JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```

### Bước 4: Build Backend

```bash
# Build tất cả modules
mvn clean install

# Nếu gặp lỗi, có thể skip tests
mvn clean install -DskipTests
```

### Bước 5: Chạy Backend Services

Mở 3 terminal windows và chạy từng service:

**Terminal 1 - Security Service:**
```bash
cd security
mvn spring-boot:run
```
Service sẽ chạy trên: http://localhost:8083

**Terminal 2 - QLSV Service:**
```bash
cd qlsv
mvn spring-boot:run
```
Service sẽ chạy trên: http://localhost:8080

**Terminal 3 - QLGV Service:**
```bash
cd qlgv
mvn spring-boot:run
```
Service sẽ chạy trên: http://localhost:8081

### Bước 6: Cài đặt và chạy Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend sẽ chạy trên: http://localhost:5173 (hoặc port khác nếu 5173 đã được sử dụng)

### Bước 7: Kiểm tra

1. Mở browser và truy cập: http://localhost:5173
2. Kiểm tra backend services:
   - Security: http://localhost:8083/actuator/health
   - QLSV: http://localhost:8080/actuator/health
   - QLGV: http://localhost:8081/actuator/health

## 🚀 Cách vận hành

### Development Workflow

1. **Start services theo thứ tự:**
   - MySQL → Redis → Kafka → Security → QLSV → QLGV → Frontend

2. **API Endpoints:**
   - Security: `http://localhost:8083/api/auth/*`
   - QLSV: `http://localhost:8080/api/students/*`
   - QLGV: `http://localhost:8081/api/teachers/*`

3. **Database Migration:**
   - Hibernate tự động tạo/update schema khi start (ddl-auto=update)
   - Để reset database, xóa và tạo lại databases

### Production (Railway)

- Tất cả services được deploy tự động khi push code lên Git
- Environment variables được set trên Railway Dashboard
- Services tự động restart khi có lỗi
- Logs có thể xem trên Railway Dashboard

## 📝 Lưu ý quan trọng

1. **Database**: Đảm bảo MySQL đang chạy trước khi start backend services
2. **Redis**: Nếu không có Redis, một số tính năng caching có thể không hoạt động
3. **Kafka**: Có thể disable bằng cách set `KAFKA_ENABLED=false`
4. **Ports**: Đảm bảo ports 8080, 8081, 8083, 5173, 3306, 6379, 9092 không bị conflict
5. **CORS**: Frontend đã được cấu hình để gọi APIs từ các backend services

## 🐛 Troubleshooting

### Backend không kết nối được database
- Kiểm tra MySQL đang chạy: `mysql -u root -p`
- Kiểm tra database đã được tạo chưa
- Kiểm tra username/password trong `application.properties`

### Redis connection error
- Kiểm tra Redis đang chạy: `redis-cli ping` (nên trả về `PONG`)
- Kiểm tra port 6379 không bị block

### Kafka connection error
- Kiểm tra Kafka đang chạy (Kafka 2.8+ không cần Zookeeper)
- Kiểm tra port 9092 không bị block
- Hoặc set `KAFKA_ENABLED=false` để disable Kafka

### Frontend không kết nối được backend
- Kiểm tra backend services đang chạy
- Kiểm tra CORS configuration
- Kiểm tra `VITE_SECURITY_BASE_URL`, `VITE_QLSV_BASE_URL`, `VITE_QLGV_BASE_URL` trong frontend

## 📚 Tài liệu thêm

- [Tiêu chí chấm điểm](./TIEUCHI_CHAMDIEM.md)
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- React Documentation: https://react.dev
- Railway Documentation: https://docs.railway.app

## 👥 Contributors

- Development Team By HuyK3

## 📄 License

[License information]
