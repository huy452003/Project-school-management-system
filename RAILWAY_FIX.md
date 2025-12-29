# 🔧 Sửa lỗi Deploy Railway - Hướng dẫn nhanh

## ✅ Đã làm gì:

1. ✅ Tạo `railway.json` - Cấu hình build và start command
2. ✅ Tạo `Procfile` - Backup option cho Railway
3. ✅ Cập nhật `application.properties` - Sử dụng environment variables
4. ✅ Tạo `RAILWAY_DEPLOY.md` - Hướng dẫn chi tiết

## 🚀 Các bước deploy ngay bây giờ:

### 1. Commit và push các file mới:

```powershell
cd "D:\project IntelliJ Ultimate\modules-parent"
git add .
git commit -m "Add Railway deployment configuration"
git push origin main
```

### 2. Trên Railway Dashboard:

1. **Vào project** → **Settings** → **Build**
   - Build Command: `mvn clean install -DskipTests`
   - Start Command: `cd security && java -jar target/security-3.5.0-exec.jar`

2. **Thêm Database (PostgreSQL)**:
   - Click **"+ New"** → **"Database"** → **"Add PostgreSQL"**
   - Railway sẽ tự động set `DATABASE_URL`

3. **Cấu hình Environment Variables** (Variables tab):
   ```
   PORT=8083
   JWT_SECRET=your-super-secret-key-change-this-in-production
   JWT_EXPIRATION=3600000
   JWT_REFRESH_EXPIRATION=604800000
   FRONTEND_URL=https://your-frontend.vercel.app
   ```

   **Lưu ý**: `DATABASE_URL` sẽ được Railway tự động set khi bạn thêm PostgreSQL database.

4. **Redeploy**:
   - Click **"Deploy"** hoặc **"Redeploy"** để build lại

### 3. Kiểm tra Logs:

- Vào **Deployments** → Click deployment mới nhất → Xem **Logs**
- Tìm các dòng:
  - `Started SecurityApplication` = ✅ Thành công
  - `Port XXXX` = Kiểm tra port có đúng không
  - `Exception` hoặc `Error` = ❌ Có lỗi

## ⚠️ Vấn đề về DATABASE_URL

Railway PostgreSQL cung cấp `DATABASE_URL` dạng:
```
postgresql://postgres:password@host:port/railway
```

Nhưng Spring Boot cần JDBC format:
```
jdbc:postgresql://host:port/railway?user=postgres&password=password
```

### Giải pháp 1: Set riêng từng biến (Khuyến nghị)

Trong Railway Variables, thêm:
```
DB_HOST=containers-us-west-xxx.railway.app
DB_PORT=5432
DB_NAME=railway
DB_USERNAME=postgres
DB_PASSWORD=your-password
DB_DRIVER=org.postgresql.Driver
```

Và trong `application.properties` đã được cập nhật để đọc các biến này.

### Giải pháp 2: Convert DATABASE_URL

Nếu Railway chỉ cung cấp `DATABASE_URL`, bạn cần convert. Có thể tạo một class Java để parse và convert.

## 🔍 Debug Checklist

Nếu vẫn không chạy được, kiểm tra:

- [ ] Build có thành công không? (Xem logs)
- [ ] JAR file có được tạo không? (`target/security-3.5.0-exec.jar`)
- [ ] Port có đúng không? (Railway set PORT tự động)
- [ ] Database connection có thành công không?
- [ ] Environment variables có được set đúng không?

## 📞 Lỗi cụ thể?

Nếu gặp lỗi cụ thể, copy log và kiểm tra:

1. **"Cannot find or load main class"**
   → JAR file không đúng hoặc không có main class
   → Kiểm tra: `mvn clean install` có tạo JAR không?

2. **"Port already in use"**
   → Application không đọc PORT env variable
   → Đã fix trong `application.properties`: `server.port=${PORT:8083}`

3. **"Database connection failed"**
   → DATABASE_URL không đúng format
   → Dùng Giải pháp 1 ở trên (set riêng từng biến)

4. **"Build timeout"**
   → Maven download dependencies quá lâu
   → Tăng timeout trong Railway settings hoặc build local trước

## 🎯 Quick Test Local

Test trước khi deploy:

```powershell
# Build project
mvn clean install -DskipTests

# Test chạy với PORT env variable (giống Railway)
$env:PORT="8083"
cd security
java -jar target/security-3.5.0-exec.jar
```

Nếu chạy được local, sẽ chạy được trên Railway!

