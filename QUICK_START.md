# Quick Start - Push lên Git và Deploy

## 🚀 Các bước nhanh để push code lên Git

### 1. Khởi tạo Git Repository

Mở PowerShell hoặc Command Prompt tại thư mục project và chạy:

```powershell
cd "D:\project IntelliJ Ultimate\modules-parent"

# Khởi tạo git (nếu chưa có)
git init

# Kiểm tra trạng thái
git status
```

### 2. Thêm và commit code

```powershell
# Thêm tất cả files
git add .

# Commit
git commit -m "Initial commit: School management system"
```

### 3. Tạo repository trên GitHub

1. Truy cập https://github.com
2. Đăng nhập/Đăng ký
3. Click nút **"+"** → **"New repository"**
4. Đặt tên repository (ví dụ: `school-management-system`)
5. **KHÔNG** tích vào "Initialize with README"
6. Click **"Create repository"**

### 4. Kết nối và push code

Sau khi tạo repository, GitHub sẽ hiển thị hướng dẫn. Chạy các lệnh sau (thay `YOUR_USERNAME` và `REPO_NAME`):

```powershell
# Thêm remote repository
git remote add origin https://github.com/YOUR_USERNAME/REPO_NAME.git

# Đổi tên branch thành main (nếu cần)
git branch -M main

# Push code lên GitHub
git push -u origin main
```

Nếu được hỏi username/password, sử dụng:
- **Username**: Tên GitHub của bạn
- **Password**: Personal Access Token (không phải password GitHub)
  - Tạo token tại: https://github.com/settings/tokens
  - Chọn quyền: `repo`

## 🌐 Deploy lên Web

### Backend (Spring Boot)

**Khuyến nghị: Railway.app** (Miễn phí, dễ dùng)

1. Truy cập https://railway.app
2. Đăng nhập bằng GitHub
3. Click **"New Project"** → **"Deploy from GitHub repo"**
4. Chọn repository vừa push
5. Railway sẽ tự động detect và deploy
6. Lấy URL backend (ví dụ: `https://your-app.railway.app`)

### Frontend (React)

**Khuyến nghị: Vercel** (Miễn phí, nhanh)

1. Truy cập https://vercel.com
2. Đăng nhập bằng GitHub
3. Click **"Add New Project"**
4. Import repository
5. Cấu hình:
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
6. Thêm Environment Variable:
   - Key: `VITE_API_URL`
   - Value: URL backend từ Railway (ví dụ: `https://your-app.railway.app`)
7. Click **"Deploy"**
8. Lấy URL frontend (ví dụ: `https://your-app.vercel.app`)

### Cập nhật API URL trong Frontend

Sau khi có URL backend, cập nhật file `frontend/src/config/api.js`:

```javascript
const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://your-backend-url.railway.app';
```

Sau đó commit và push lại:

```powershell
git add .
git commit -m "Update API URL for production"
git push
```

Vercel sẽ tự động deploy lại với URL mới.

## 📝 Checklist

- [ ] Đã push code lên GitHub
- [ ] Đã deploy backend lên Railway/VPS
- [ ] Đã deploy frontend lên Vercel/VPS
- [ ] Đã cập nhật API URL trong frontend
- [ ] Đã test ứng dụng hoạt động

## 🔗 Xem thêm

Chi tiết đầy đủ xem file [DEPLOY.md](./DEPLOY.md)

## ❓ Cần giúp đỡ?

Nếu gặp lỗi, kiểm tra:
1. Logs trên platform (Railway/Vercel dashboard)
2. Console trong browser (F12)
3. Network tab để xem API calls

