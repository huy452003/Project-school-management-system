# Build stage
FROM node:18-alpine AS build

WORKDIR /app

# Copy package files
COPY package.json ./
COPY package-lock.json* ./

# Install dependencies
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi

# Copy source code
COPY . .

# Build for production
RUN npm run build || (echo "Build failed!" && exit 1)

# Verify build output exists
RUN test -d dist || (echo "dist directory not found!" && exit 1)
RUN ls -la dist/

# Production stage - serve static files with nginx
FROM nginx:alpine

# Copy built files from build stage
COPY --from=build /app/dist /usr/share/nginx/html

# Create nginx config - Railway sẽ tự động map PORT
# Nếu Railway set PORT=3000, nginx sẽ listen trên port đó
RUN echo 'server { \
    listen 80; \
    server_name _; \
    root /usr/share/nginx/html; \
    index index.html; \
    location / { \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

# Expose port 80 - Railway sẽ tự động map PORT env variable
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

