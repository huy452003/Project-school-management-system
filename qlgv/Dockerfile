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

# Build QLGV application (skip tests, use production profile)
# Build only QLGV module and its dependencies
RUN mvn clean install -DskipTests -Dmaven.test.skip=true -Pproduction -pl qlgv -am || \
    (echo "Build with -pl failed, trying full build..." && \
     mvn clean install -DskipTests -Dmaven.test.skip=true -Pproduction)

# Verify JAR file was created and list all JAR files
RUN echo "=== Checking QLGV target directory ===" && \
    ls -la /app/qlgv/target/ && \
    echo "=== All JAR files in target ===" && \
    find /app/qlgv/target -name "*.jar" -type f || echo "No JAR files found" && \
    echo "=== Checking if exec JAR exists ===" && \
    test -f /app/qlgv/target/QLGV-3.5.0-exec.jar && echo "QLGV-3.5.0-exec.jar found" || echo "QLGV-3.5.0-exec.jar NOT found"

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the executable JAR file (spring-boot-maven-plugin with classifier=exec creates *-exec.jar)
COPY --from=build /app/qlgv/target/QLGV-3.5.0-exec.jar app.jar

# Expose port - Railway will automatically set PORT env variable
# EXPOSE 8081 is just metadata, Railway will map the actual PORT automatically
EXPOSE 8081

# Run the application - Railway automatically sets PORT env variable
# Application will use PORT from Railway, fallback to 8081 if not set (matches application.properties)
CMD java -Dserver.port=${PORT:-8081} -jar app.jar

