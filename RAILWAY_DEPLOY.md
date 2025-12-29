# Hướng dẫn Deploy Backend lên Railway - Chi tiết

## Vấn đề thường gặp và cách khắc phục

### ❌ Lỗi thường gặp:
1. **Build failed**: Maven không build được
2. **Port không đúng**: Railway cần dùng biến môi trường `PORT`
3. **Database connection failed**: Cần cấu hình database URL
4. **JAR file không tìm thấy**: Path hoặc tên file không đúng

## ✅ Giải pháp từng bước

### Bước 1: Cập nhật application.properties để dùng Environment Variables

File `security/src/main/resources/application.properties` cần được cập nhật để sử dụng env vars:

```properties
# Port - Railway sẽ tự động set PORT env variable
server.port=${PORT:8083}

# Database - Sử dụng Railway's PostgreSQL hoặc MySQL
spring.datasource.url=${DATABASE_URL:jdbc:mysql://localhost:3306/security_module}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:huy12345}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Redis - Nếu dùng Railway Redis
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
spring.data.redis.password=${REDIS_PASSWORD:}

# JWT
jwt.secret=${JWT_SECRET:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}
jwt.expiration=${JWT_EXPIRATION:3600000}
```

### Bước 2: Deploy trên Railway

#### 2.1. Tạo Project trên Railway

1. Truy cập https://railway.app
2. Đăng nhập bằng GitHub
3. Click **"New Project"** → **"Deploy from GitHub repo"**
4. Chọn repository: `huy452003/school-management-system`
5. Railway sẽ tự động detect và bắt đầu build

#### 2.2. Cấu hình Build Settings

Trong Railway dashboard, vào **Settings** → **Build**:

- **Build Command**: `mvn clean install -DskipTests`
- **Start Command**: `cd security && java -jar target/security-3.5.0-exec.jar`

Hoặc Railway sẽ tự động đọc từ `railway.json` hoặc `Procfile`.

#### 2.3. Thêm Database (PostgreSQL - Khuyến nghị)

1. Trong Railway project, click **"+ New"** → **"Database"** → **"Add PostgreSQL"**
2. Railway sẽ tự động tạo database và set biến môi trường `DATABASE_URL`
3. Format: `postgresql://user:password@host:port/dbname`

**Lưu ý**: Nếu project dùng MySQL, bạn có thể:
- Option 1: Chuyển sang PostgreSQL (khuyến nghị cho Railway)
- Option 2: Dùng MySQL từ nhà cung cấp khác (PlanetScale, AWS RDS) và set `DATABASE_URL` manually

#### 2.4. Thêm Redis (Tùy chọn)

1. Click **"+ New"** → **"Database"** → **"Add Redis"**
2. Railway sẽ tự động set `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`

#### 2.5. Cấu hình Environment Variables

Vào **Variables** tab và thêm:

```
PORT=8083
JWT_SECRET=your-super-secret-key-here-min-256-bits
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# CORS - Thêm URL frontend của bạn
FRONTEND_URL=https://your-frontend.vercel.app

# Database (nếu không dùng Railway Database)
# DATABASE_URL=jdbc:mysql://host:port/dbname
# DB_USERNAME=username
# DB_PASSWORD=password

# Redis (nếu không dùng Railway Redis)
# REDIS_HOST=localhost
# REDIS_PORT=6379
# REDIS_PASSWORD=
```

**Lưu ý quan trọng về PORT**:
- Railway tự động set biến `PORT` khi deploy
- Ứng dụng phải listen trên port này, không phải hardcode 8083
- Spring Boot sẽ tự động đọc `server.port=${PORT:8083}`

### Bước 3: Kiểm tra Logs

1. Vào **Deployments** tab
2. Click vào deployment mới nhất
3. Xem **Logs** để kiểm tra:
   - Build có thành công không?
   - Application có start được không?
   - Có lỗi database connection không?
   - Port có đúng không?

### Bước 4: Test API

Sau khi deploy thành công, Railway sẽ cung cấp URL dạng:
`https://your-app-name.up.railway.app`

Test API:
```bash
curl https://your-app-name.up.railway.app/auth/health
```

## 🔧 Troubleshooting

### Lỗi: "Cannot find JAR file"

**Nguyên nhân**: Tên file JAR không đúng hoặc path sai

**Giải pháp**:
1. Kiểm tra tên file JAR trong `target/` sau khi build
2. Có thể là `security-3.5.0.jar` hoặc `security-3.5.0-exec.jar`
3. Cập nhật `railway.json` hoặc `Procfile` với tên đúng

Hoặc dùng Maven để chạy trực tiếp:
```bash
cd security && mvn spring-boot:run
```

Cập nhật `Procfile`:
```
web: cd security && mvn spring-boot:run
```

### Lỗi: "Port already in use" hoặc "Address already in use"

**Nguyên nhân**: Application không đọc biến `PORT` từ Railway

**Giải pháp**:
1. Đảm bảo `application.properties` có: `server.port=${PORT:8083}`
2. Hoặc set trong code:
```java
@SpringBootApplication
public class SecurityApplication {
    public static void main(String[] args) {
        String port = System.getenv("PORT");
        if (port != null) {
            System.setProperty("server.port", port);
        }
        SpringApplication.run(SecurityApplication.class, args);
    }
}
```

### Lỗi: "Database connection failed"

**Nguyên nhân**: Database URL không đúng format

**Giải pháp**:
1. Nếu dùng Railway PostgreSQL, `DATABASE_URL` sẽ có format:
   ```
   postgresql://postgres:password@host:port/railway
   ```
2. Spring Boot cần format JDBC:
   ```
   jdbc:postgresql://host:port/railway?user=postgres&password=password
   ```
3. Tạo converter script hoặc dùng library để convert

Hoặc set riêng:
```
DB_HOST=host
DB_PORT=5432
DB_NAME=railway
DB_USERNAME=postgres
DB_PASSWORD=password
```

Và trong `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### Lỗi: "Build timeout" hoặc "Build takes too long"

**Nguyên nhân**: Maven download dependencies quá lâu

**Giải pháp**:
1. Tăng build timeout trong Railway settings
2. Hoặc build local và push JAR file lên (không khuyến nghị)
3. Sử dụng Maven cache trong Railway

### Lỗi: "Module not found" hoặc "Dependency resolution failed"

**Nguyên nhân**: Multi-module project cần build parent trước

**Giải pháp**:
- Railway đã tự động build từ root với `mvn clean install`
- Đảm bảo tất cả modules được build thành công
- Kiểm tra `pom.xml` parent có đúng không

## 📝 Checklist trước khi deploy

- [ ] Đã test build local: `mvn clean install -DskipTests`
- [ ] Đã kiểm tra JAR file được tạo: `ls security/target/*.jar`
- [ ] Đã cập nhật `application.properties` để dùng env vars
- [ ] Đã chuẩn bị database (PostgreSQL trên Railway)
- [ ] Đã set tất cả environment variables cần thiết
- [ ] Đã test chạy local với env vars: `PORT=8083 java -jar security/target/security-3.5.0-exec.jar`

## 🚀 Quick Deploy Commands

Sau khi setup xong trên Railway, mỗi lần push code mới:

```bash
git add .
git commit -m "Update code"
git push origin main
```

Railway sẽ tự động detect và deploy lại.

## 📚 Tài liệu tham khảo

- [Railway Java Documentation](https://docs.railway.app/guides/java)
- [Railway Environment Variables](https://docs.railway.app/develop/variables)
- [Spring Boot on Railway](https://docs.railway.app/examples/spring-boot)

