# Multi-stage build for QLGV Spring Boot application
# This Dockerfile must be built from the project root directory
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom files first for better caching
COPY pom.xml .
COPY */pom.xml ./

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B -Pproduction || true

# Copy source code (excluding target directories via .dockerignore)
COPY . .

# Remove problematic files and test directories before build
RUN find . -name "*.drawio" -type f -delete 2>/dev/null || true && \
    find . -name "*.drawio.xml" -type f -delete 2>/dev/null || true && \
    find . -type d -name "test" -path "*/src/test" -exec rm -rf {} + 2>/dev/null || true

# Build application (skip tests, use production profile)
# Build entire project to ensure all dependencies are built (same as Security service)
RUN mvn clean install -DskipTests -Dmaven.test.skip=true -Pproduction

# Verify and prepare JAR file for copying
RUN echo "=== Listing all files in qlgv/target ===" && \
    ls -la /app/qlgv/target/ && \
    echo "=== Finding all JAR files ===" && \
    find /app/qlgv/target -name "*.jar" -type f && \
    echo "=== Checking for exec JAR ===" && \
    if [ -f /app/qlgv/target/QLGV-3.5.0-exec.jar ]; then \
        echo "Found QLGV-3.5.0-exec.jar"; \
        cp /app/qlgv/target/QLGV-3.5.0-exec.jar /app/qlgv/target/app.jar; \
    elif [ -f /app/qlgv/target/QLGV-3.5.0.jar ]; then \
        echo "Found QLGV-3.5.0.jar, copying as app.jar"; \
        cp /app/qlgv/target/QLGV-3.5.0.jar /app/qlgv/target/app.jar; \
    else \
        echo "ERROR: No JAR file found!"; \
        find /app/qlgv/target -name "*.jar" -type f -exec cp {} /app/qlgv/target/app.jar \; && \
        ls -la /app/qlgv/target/app.jar || exit 1; \
    fi

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the prepared JAR file
COPY --from=build /app/qlgv/target/app.jar app.jar

# Expose port - Railway will automatically set PORT env variable
# EXPOSE 8081 is just metadata, Railway will map the actual PORT automatically
EXPOSE 8081

# Run the application - Railway automatically sets PORT env variable
# Application will use PORT from Railway, fallback to 8081 if not set (matches application.properties)
CMD java -Dserver.port=${PORT:-8081} -jar app.jar

