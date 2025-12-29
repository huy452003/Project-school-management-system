# Hướng dẫn Deploy Project lên Web

## Bước 1: Push code lên Git Repository

### 1.1. Khởi tạo Git (nếu chưa có)

```bash
cd "D:\project IntelliJ Ultimate\modules-parent"
git init
```

### 1.2. Thêm tất cả files vào Git

```bash
git add .
```

### 1.3. Commit code

```bash
git commit -m "Initial commit: School management system"
```

### 1.4. Tạo repository trên GitHub/GitLab/Bitbucket

1. Đăng nhập vào GitHub/GitLab/Bitbucket
2. Tạo repository mới (ví dụ: `school-management-system`)
3. **KHÔNG** khởi tạo với README, .gitignore, hoặc license

### 1.5. Kết nối và push code

```bash
# Thay YOUR_USERNAME và REPO_NAME bằng thông tin của bạn
git remote add origin https://github.com/YOUR_USERNAME/REPO_NAME.git
git branch -M main
git push -u origin main
```

## Bước 2: Deploy Backend (Spring Boot)

### Option 1: Railway (Khuyến nghị - Miễn phí)

1. Truy cập [Railway.app](https://railway.app)
2. Đăng nhập bằng GitHub
3. Click "New Project" → "Deploy from GitHub repo"
4. Chọn repository của bạn
5. Railway sẽ tự động detect Spring Boot project
6. Thêm các biến môi trường cần thiết:
   - Database URL (nếu dùng)
   - JWT secret
   - Kafka config (nếu dùng)
7. Railway sẽ tự động build và deploy

**Lưu ý**: Cần tạo file `railway.json` hoặc `Procfile` để chỉ định service nào chạy:

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS"
  },
  "deploy": {
    "startCommand": "cd security && mvn spring-boot:run",
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

### Option 2: Render.com

1. Truy cập [Render.com](https://render.com)
2. Đăng nhập bằng GitHub
3. Click "New" → "Web Service"
4. Connect repository
5. Cấu hình:
   - **Build Command**: `mvn clean install -DskipTests`
   - **Start Command**: `cd security && java -jar target/security-3.5.0.jar`
   - **Environment**: Java
6. Deploy

### Option 3: Heroku

1. Cài đặt Heroku CLI
2. Login: `heroku login`
3. Tạo app: `heroku create your-app-name`
4. Thêm buildpack: `heroku buildpacks:set heroku/java`
5. Deploy: `git push heroku main`

### Option 4: VPS/Server riêng

```bash
# SSH vào server
ssh user@your-server.com

# Clone repository
git clone https://github.com/YOUR_USERNAME/REPO_NAME.git
cd REPO_NAME

# Build project
mvn clean install

# Chạy service (sử dụng systemd hoặc PM2)
cd security
nohup java -jar target/security-3.5.0.jar > app.log 2>&1 &
```

## Bước 3: Deploy Frontend (React)

### Option 1: Vercel (Khuyến nghị - Miễn phí)

1. Truy cập [Vercel.com](https://vercel.com)
2. Đăng nhập bằng GitHub
3. Click "Add New Project"
4. Import repository
5. Cấu hình:
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
   - **Install Command**: `npm install`
6. Thêm biến môi trường:
   - `VITE_API_URL`: URL của backend API
7. Deploy

### Option 2: Netlify

1. Truy cập [Netlify.com](https://netlify.com)
2. Đăng nhập bằng GitHub
3. Click "Add new site" → "Import an existing project"
4. Chọn repository
5. Cấu hình:
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `frontend/dist`
6. Deploy

### Option 3: GitHub Pages

1. Cập nhật `vite.config.js`:

```js
export default {
  base: '/your-repo-name/',
  // ... other config
}
```

2. Build và deploy:

```bash
cd frontend
npm run build
npm install -g gh-pages
gh-pages -d dist
```

### Option 4: VPS/Server riêng với Nginx

```bash
# Build frontend
cd frontend
npm run build

# Copy dist folder lên server
scp -r dist/* user@your-server.com:/var/www/html/

# Cấu hình Nginx
sudo nano /etc/nginx/sites-available/default
```

Nginx config:

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## Bước 4: Cấu hình Domain

### 4.1. Mua domain (nếu chưa có)

- Namecheap, GoDaddy, Freenom (miễn phí), etc.

### 4.2. Cấu hình DNS

#### Với Vercel/Netlify:
1. Vào dashboard của Vercel/Netlify
2. Settings → Domains
3. Add domain
4. Làm theo hướng dẫn để cấu hình DNS records

#### Với VPS:
1. Thêm A record trỏ về IP server
2. Cấu hình Nginx với domain name (như trên)

### 4.3. SSL Certificate (HTTPS)

#### Với Vercel/Netlify:
- Tự động có SSL miễn phí

#### Với VPS:
```bash
# Cài đặt Certbot
sudo apt install certbot python3-certbot-nginx

# Lấy SSL certificate
sudo certbot --nginx -d your-domain.com
```

## Bước 5: Cấu hình Environment Variables

### Backend (.env hoặc trong platform settings)

```properties
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/school_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Kafka (nếu dùng)
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# CORS
FRONTEND_URL=https://your-frontend-domain.com
```

### Frontend (.env.production)

```env
VITE_API_URL=https://your-backend-domain.com/api
```

## Checklist trước khi deploy

- [ ] Đã test code chạy được local
- [ ] Đã cập nhật `.gitignore` để không commit sensitive data
- [ ] Đã cấu hình CORS cho backend
- [ ] Đã cập nhật API URLs trong frontend
- [ ] Đã set environment variables
- [ ] Đã test kết nối database (nếu có)
- [ ] Đã cấu hình firewall/security groups

## Troubleshooting

### Backend không start được
- Kiểm tra logs trên platform
- Kiểm tra port đã được expose chưa
- Kiểm tra environment variables

### Frontend không kết nối được API
- Kiểm tra CORS settings trên backend
- Kiểm tra API URL trong frontend config
- Kiểm tra network tab trong browser console

### Database connection issues
- Kiểm tra database URL và credentials
- Kiểm tra firewall rules
- Kiểm tra database đã accessible từ internet chưa

## Liên kết hữu ích

- [Railway Documentation](https://docs.railway.app)
- [Vercel Documentation](https://vercel.com/docs)
- [Render Documentation](https://render.com/docs)
- [Spring Boot Deployment](https://spring.io/guides/gs/spring-boot-for-azure/)

