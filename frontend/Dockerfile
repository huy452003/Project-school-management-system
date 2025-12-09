# Sử dụng Node.js LTS làm base image
FROM node:18-alpine

# Đặt thư mục làm việc
WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./

# Cài đặt dependencies
RUN npm install

# Copy toàn bộ source code
COPY . .

# Expose port 3000
EXPOSE 3000

# Chạy development server
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]

