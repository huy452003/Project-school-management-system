# 🔧 Fix Build Failed trên Railway

## Các lỗi thường gặp và cách fix:

### 1. ❌ Test Compilation Error

**Lỗi**: `cannot access unnamed package` hoặc test compilation failed

**Nguyên nhân**: File test có vấn đề về package hoặc import

**Giải pháp**: 
- Xóa hoặc fix file test có vấn đề
- Hoặc skip test compilation hoàn toàn

### 2. ❌ Missing Dependencies

**Lỗi**: `Could not resolve dependencies`

**Giải pháp**: Đảm bảo tất cả dependencies có trong `pom.xml`

### 3. ❌ Profile không tồn tại

**Lỗi**: `The requested profile "production" could not be activated`

**Giải pháp**: Bỏ `-Pproduction` khỏi build command vì profile này không tồn tại

## 🛠️ Quick Fix:

### Option 1: Bỏ profile production (Khuyến nghị)

Cập nhật `railway.json`:

```json
{
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean install -DskipTests"
  }
}
```

### Option 2: Skip test compilation hoàn toàn

```json
{
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean install -DskipTests -Dmaven.test.skip=true"
  }
}
```

### Option 3: Fix test file

Nếu có file test lỗi, xóa hoặc fix nó.

