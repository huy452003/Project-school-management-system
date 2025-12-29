# TIÊU CHÍ CHẤM ĐIỂM ĐỒ ÁN CHUYÊN ĐỀ
## Website Bán Túi Xách - PHP/MySQL

**Tổng điểm**: 100 điểm  
**Môn học**: Phát triển ứng dụng Web  
**Năm học**: 2025

---

## I. PHÂN TÍCH VÀ THIẾT KẾ HỆ THỐNG (15 điểm)

### 1.1 Thiết Kế Database (8 điểm)
- **Cấu trúc bảng đầy đủ** (3 điểm)
  - Bảng `sanpham`: đầy đủ thuộc tính (id, tensanpham, danhmuc, gia, hinhanh, mota, noibat, ngaytao, ngaycapnhat)
  - Bảng `donhang`: quản lý thông tin đơn hàng
  - Bảng `chitiet_donhang`: chi tiết sản phẩm trong đơn
  - Bảng `khachhang`: thông tin khách hàng
  - Bảng `admin`: tài khoản quản trị
  - Bảng `lienhe`: quản lý tin nhắn liên hệ

- **Quan hệ giữa các bảng** (2 điểm)
  - Foreign Key giữa `chitiet_donhang` và `donhang`
  - Foreign Key giữa `chitiet_donhang` và `sanpham`
  - Ràng buộc dữ liệu hợp lý

- **Kiểu dữ liệu phù hợp** (2 điểm)
  - VARCHAR cho chuỗi với độ dài hợp lý
  - DECIMAL cho giá tiền
  - ENUM cho trạng thái
  - TIMESTAMP cho ngày tháng
  - TINYINT cho cờ boolean

- **File database.sql** (1 điểm)
  - File SQL chạy được, không lỗi
  - Có dữ liệu mẫu (ít nhất 10 sản phẩm)
  - Có tài khoản admin mẫu

### 1.2 Sơ Đồ Cấu Trúc Thư Mục (3 điểm)
- Tổ chức thư mục rõ ràng (admin/, assets/, includes/)
- Phân tách assets hợp lý (css/, js/, images/)
- File config riêng biệt

### 1.3 Documentation (4 điểm)
- File README.md đầy đủ và chi tiết
- Hướng dẫn cài đặt rõ ràng
- Mô tả tính năng đầy đủ
- Cấu trúc database được mô tả

---

## II. CHỨC NĂNG PHẦN KHÁCH HÀNG (35 điểm)

### 2.1 Trang Chủ - index.php (5 điểm)
- **Giao diện** (2 điểm)
  - Banner quảng cáo đẹp mắt
  - Layout responsive
  - Hiển thị thống kê nổi bật
  
- **Sản phẩm nổi bật** (2 điểm)
  - Hiển thị đúng sản phẩm có noibat = 1
  - Layout grid hợp lý (6 sản phẩm)
  - Hover effect mượt mà
  
- **Các tính năng nổi bật** (1 điểm)
  - Icons Font Awesome
  - 4 tính năng: Miễn phí ship, Đổi trả, Thanh toán, Hỗ trợ

### 2.2 Trang Sản Phẩm - sanpham.php (6 điểm)
- **Hiển thị danh sách** (2 điểm)
  - Query database đúng
  - Grid layout responsive
  - Placeholder cho ảnh trống
  
- **Bộ lọc danh mục** (2 điểm)
  - Lọc theo: Tất cả, Túi da, Túi vải, Túi chéo, Túi công sở, Ba lô
  - URL query string (?category=xxx)
  - Highlight danh mục đang chọn
  
- **Phân trang** (2 điểm)
  - Pagination (12 sản phẩm/trang)
  - Next/Previous buttons
  - Hiển thị số trang

### 2.3 Chi Tiết Sản Phẩm - chitiet.php (5 điểm)
- **Hiển thị thông tin** (2 điểm)
  - Hình ảnh lớn
  - Tên, giá, danh mục, mô tả đầy đủ
  - Breadcrumb navigation
  
- **Thêm vào giỏ hàng** (2 điểm)
  - Input số lượng
  - Nút "Thêm vào giỏ"
  - Lưu vào session
  
- **Sản phẩm liên quan** (1 điểm)
  - Query sản phẩm cùng danh mục
  - Hiển thị 4-6 sản phẩm

### 2.4 Giỏ Hàng - giohang.php (5 điểm)
- **Hiển thị giỏ hàng** (2 điểm)
  - Danh sách sản phẩm từ session
  - Hình ảnh, tên, giá, số lượng
  - Tính tổng tiền đúng
  
- **Cập nhật số lượng** (2 điểm)
  - Nút +/- hoạt động
  - AJAX hoặc form submit
  - Validation số lượng > 0
  
- **Xóa sản phẩm** (1 điểm)
  - Nút xóa với icon
  - Confirm trước khi xóa

### 2.5 Thanh Toán - thanhtoan.php (6 điểm)
- **Form thông tin** (3 điểm)
  - Họ tên, email, SĐT, địa chỉ
  - Validation đầy đủ
  - Required fields
  
- **Phương thức thanh toán** (1 điểm)
  - Radio buttons: COD / Chuyển khoản
  - Icon rõ ràng
  
- **Tóm tắt đơn hàng** (1 điểm)
  - Danh sách sản phẩm
  - Tổng tiền
  
- **Xử lý đặt hàng** (1 điểm)
  - Insert vào `donhang`
  - Insert vào `chitiet_donhang`
  - Clear session giỏ hàng

### 2.6 Đăng Ký & Đăng Nhập (4 điểm)
- **Đăng ký - dangky.php** (2 điểm)
  - Form đầy đủ (họ tên, email, SĐT, mật khẩu, xác nhận MK)
  - Validation: email hợp lệ, MK khớp
  - Kiểm tra email đã tồn tại
  - Mã hóa mật khẩu
  
- **Đăng nhập - dangnhap.php** (2 điểm)
  - Form login (email, password)
  - Kiểm tra thông tin đúng
  - Session management
  - Kiểm tra trạng thái tài khoản

### 2.7 Tài Khoản - taikhoan.php (4 điểm)
- **Lịch sử đơn hàng** (2 điểm)
  - Hiển thị đơn hàng của khách
  - Badge trạng thái màu sắc
  - Sắp xếp theo ngày mới nhất
  
- **Chi tiết đơn hàng** (1 điểm)
  - Modal hoặc trang riêng
  - Danh sách sản phẩm trong đơn
  - Thông tin giao hàng
  
- **Hủy đơn hàng** (1 điểm)
  - Chỉ hủy được đơn "Chờ xác nhận"
  - Confirm trước khi hủy
  - Cập nhật trạng thái DB

---

## III. CHỨC NĂNG PHẦN ADMIN (30 điểm)

### 3.1 Đăng Nhập Admin - login.php (3 điểm)
- Form login riêng cho admin
- Xác thực với bảng `admin`
- Session admin riêng biệt
- Redirect đúng sau login

### 3.2 Dashboard - admin/index.php (6 điểm)
- **Thống kê tổng quan** (4 điểm)
  - Tổng doanh thu (từ đơn hoàn thành)
  - Tổng đơn hàng
  - Tổng sản phẩm
  - Tổng khách hàng
  - Cards với icon và màu sắc
  
- **Biểu đồ/Thống kê** (2 điểm)
  - Đơn hàng theo trạng thái
  - Sản phẩm theo danh mục
  - Hoặc chart đơn giản

### 3.3 Quản Lý Sản Phẩm - admin/sanpham.php (9 điểm)
- **Hiển thị danh sách** (2 điểm)
  - Bảng table với đầy đủ thông tin
  - Hiển thị hình ảnh thumbnail
  - Badge "Nổi bật"
  
- **Thêm sản phẩm** (3 điểm)
  - Modal hoặc form riêng
  - Upload hình ảnh (validate định dạng)
  - Checkbox "Nổi bật"
  - Insert vào database
  
- **Sửa sản phẩm** (2 điểm)
  - Load dữ liệu vào form
  - Giữ ảnh cũ nếu không upload mới
  - Update database
  
- **Xóa sản phẩm** (2 điểm)
  - Confirm trước khi xóa
  - Kiểm tra sản phẩm có trong đơn hàng không
  - Delete khỏi database

### 3.4 Quản Lý Đơn Hàng - admin/donhang.php (8 điểm)
- **Danh sách đơn hàng** (2 điểm)
  - Hiển thị: ID, khách hàng, tổng tiền, trạng thái, ngày đặt
  - Badge trạng thái với màu sắc
  - Sắp xếp theo ngày mới nhất
  
- **Chi tiết đơn hàng** (3 điểm)
  - Modal với đầy đủ thông tin
  - Thông tin khách hàng
  - Danh sách sản phẩm trong đơn
  - Phương thức thanh toán
  
- **Cập nhật trạng thái** (2 điểm)
  - Dropdown select trạng thái
  - 5 trạng thái: Chờ xác nhận → Đang xử lý → Đang giao hàng → Hoàn thành / Đã hủy
  - Update database
  
- **Lọc theo trạng thái** (1 điểm)
  - Tabs hoặc dropdown
  - Query WHERE theo trạng thái

### 3.5 Quản Lý Khách Hàng - admin/khachhang.php (4 điểm)
- **Danh sách khách hàng** (2 điểm)
  - Hiển thị: Họ tên, Email, SĐT, Ngày đăng ký
  - Thống kê tổng đơn hàng
  - Thống kê tổng chi tiêu
  
- **Khóa/Mở khóa tài khoản** (2 điểm)
  - Toggle button
  - Cập nhật trạng thái trong DB
  - Khách bị khóa không đăng nhập được

---

## IV. GIAO DIỆN & TRẢI NGHIỆM NGƯỜI DÙNG (10 điểm)

### 4.1 CSS & Styling (5 điểm)
- **File assets/css/style.css** (2 điểm)
  - CSS tổ chức rõ ràng
  - Sử dụng CSS Grid/Flexbox
  - Variables cho màu sắc
  
- **Responsive Design** (2 điểm)
  - Media queries cho mobile/tablet
  - Menu hamburger trên mobile
  - Grid responsive
  
- **Hiệu ứng & Animation** (1 điểm)
  - Hover effects
  - Transitions mượt mà
  - Gradient backgrounds

### 4.2 Header & Footer - includes/ (2 điểm)
- **Header** (1 điểm)
  - Logo
  - Navigation menu
  - Cart badge với số lượng
  - User dropdown (nếu đã login)
  
- **Footer** (1 điểm)
  - Thông tin liên hệ
  - Social links
  - Copyright

### 4.3 UX & Usability (3 điểm)
- Breadcrumb navigation rõ ràng
- Messages thông báo (success/error)
- Loading states
- Empty states (giỏ hàng trống, không có đơn hàng)
- Confirm dialogs cho hành động quan trọng

---

## V. KỸ THUẬT LẬP TRÌNH (8 điểm)

### 5.1 PHP Code Quality (4 điểm)
- **Cấu trúc code** (2 điểm)
  - File config.php riêng biệt
  - Includes cho header/footer
  - Không lặp code (DRY principle)
  
- **Database Connection** (1 điểm)
  - mysqli hoặc PDO
  - Connection pooling
  - Error handling
  
- **Session Management** (1 điểm)
  - session_start() đúng vị trí
  - Session variables rõ ràng
  - Logout clear session

### 5.2 SQL Queries (2 điểm)
- Queries tối ưu (JOIN khi cần)
- Không có N+1 query problem
- LIMIT cho pagination
- ORDER BY hợp lý

### 5.3 JavaScript (2 điểm)
- Form validation client-side
- AJAX cho cập nhật giỏ hàng (bonus)
- Modal open/close
- Confirm dialogs

---

## VI. BẢO MẬT (7 điểm)

### 6.1 SQL Injection Prevention (2 điểm)
- mysqli_real_escape_string() cho tất cả input
- Hoặc Prepared Statements (PDO)
- Validate input types

### 6.2 Authentication & Authorization (3 điểm)
- **Password Security** (1 điểm)
  - MD5 hash (tối thiểu)
  - Bonus: bcrypt/password_hash
  
- **Session Security** (1 điểm)
  - Check session trước khi access trang cần auth
  - Separate admin session
  
- **Authorization** (1 điểm)
  - Admin pages kiểm tra quyền
  - Khách hàng chỉ xem đơn của mình

### 6.3 XSS & File Upload (2 điểm)
- **XSS Prevention** (1 điểm)
  - htmlspecialchars() cho output
  - Validate input
  
- **File Upload Security** (1 điểm)
  - Kiểm tra extension (jpg, png, gif)
  - Kiểm tra MIME type
  - Giới hạn kích thước file

---

## VII. TÍNH NĂNG BỔ SUNG (5 điểm)

### 7.1 Trang Liên Hệ - lienhe.php (2 điểm)
- Form liên hệ lưu vào database
- Thông tin cửa hàng
- Google Maps nhúng
- Social media links

### 7.2 Tính Năng Nâng Cao (3 điểm)
**Chọn ít nhất 2 trong các tính năng sau:**
- AJAX real-time cart updates (1.5 điểm)
- Tìm kiếm sản phẩm (1.5 điểm)
- Email thông báo đơn hàng (1.5 điểm)
- Xuất đơn hàng ra PDF (1.5 điểm)
- Mã giảm giá/Coupon (1.5 điểm)
- Đánh giá & bình luận sản phẩm (1.5 điểm)

---

## VIII. TRIỂN KHAI & BÁO CÁO (5 điểm)

### 8.1 Cài Đặt & Chạy (2 điểm)
- Chạy được trên localhost
- Database import thành công
- Không có lỗi fatal
- Có dữ liệu mẫu

### 8.2 Demo & Trình Bày (3 điểm)
- Demo đầy đủ tính năng
- Giải thích được code
- Trả lời câu hỏi
- Chuẩn bị tốt

---

## THANG ĐIỂM CHI TIẾT

| Mức Điểm | Đánh Giá | Mô Tả |
|----------|----------|-------|
| 90-100 | Xuất sắc | Hoàn thành đầy đủ, code chất lượng cao, có tính năng nâng cao |
| 80-89 | Tốt | Hoàn thành đầy đủ các tính năng chính, code tốt |
| 70-79 | Khá | Hoàn thành phần lớn tính năng, còn một số lỗi nhỏ |
| 60-69 | Trung bình | Hoàn thành tính năng cơ bản, nhiều lỗi |
| < 60 | Yếu | Thiếu nhiều tính năng, lỗi nghiêm trọng |

---

## GHI CHÚ QUAN TRỌNG

### Điểm Trừ
- **Lỗi nghiêm trọng**: -5 điểm/lỗi
  - SQL Injection vulnerability
  - XSS vulnerability
  - Không kiểm tra authentication
  
- **Lỗi trung bình**: -2 điểm/lỗi
  - PHP errors/warnings
  - Broken links
  - UI bị vỡ
  
- **Lỗi nhỏ**: -0.5 điểm/lỗi
  - Typos
  - CSS nhỏ
  - Console errors

### Điểm Cộng
- **Code quality cao**: +3 điểm
  - Comments đầy đủ
  - Code structure tốt
  - Best practices
  
- **UI/UX xuất sắc**: +2 điểm
  - Design đẹp, professional
  - Animations mượt mà
  - Responsive hoàn hảo

---

## CHECKLIST TRƯỚC KHI NỘP

### Bắt Buộc
- [ ] Database import thành công
- [ ] Có ít nhất 10 sản phẩm mẫu
- [ ] Tài khoản admin: username `admin`, password `admin123`
- [ ] Tài khoản khách hàng demo
- [ ] File README.md đầy đủ
- [ ] Không có lỗi PHP fatal
- [ ] Config.php đúng thông tin

### Chức Năng Chính
- [ ] Xem danh sách sản phẩm
- [ ] Lọc theo danh mục
- [ ] Xem chi tiết sản phẩm
- [ ] Thêm vào giỏ hàng
- [ ] Cập nhật giỏ hàng
- [ ] Thanh toán và tạo đơn hàng
- [ ] Đăng ký tài khoản
- [ ] Đăng nhập
- [ ] Xem lịch sử đơn hàng
- [ ] Admin đăng nhập
- [ ] Admin xem dashboard
- [ ] Admin quản lý sản phẩm (CRUD)
- [ ] Admin quản lý đơn hàng
- [ ] Admin cập nhật trạng thái đơn

### Giao Diện
- [ ] Responsive trên mobile
- [ ] Header/Footer đầy đủ
- [ ] Navigation rõ ràng
- [ ] Messages thông báo
- [ ] Form validation

### Bảo Mật
- [ ] SQL Injection prevention
- [ ] Password hashing
- [ ] Session authentication
- [ ] File upload validation
- [ ] XSS prevention

---

**Ngày cập nhật**: 29/12/2025  
**Giảng viên**: [LE THUY DAON TRANG]  
**Khoa**: Công nghệ thông tin
