# =================================================================
# 1. Build Stage: 애플리케이션을 빌드하는 단계
# =================================================================
# Gradle과 JDK 21을 포함한 이미지를 빌더(builder)로 지정
FROM gradle:8.5.0-jdk21 AS builder

# 작업 디렉토리 설정
WORKDIR /home/gradle/src

# 빌드에 필요한 소스 파일 및 빌드 스크립트 복사
# .dockerignore에 의해 호스트의 build, .gradle 폴더는 제외됩니다.
COPY . .

# Gradle 빌드 실행 (테스트는 제외하여 빌드 속도 향상)
# ./gradlew 실행 권한이 없는 경우를 대비해 권한을 부여합니다.
RUN chmod +x ./gradlew && ./gradlew build --no-daemon -x test

# =================================================================
# 2. Final Stage: 빌드된 애플리케이션을 실행하는 단계
# =================================================================
# JRE(Java Runtime Environment)만 포함된 가벼운 이미지를 사용
FROM eclipse-temurin:21-jre

# 애플리케이션 실행 디렉토리
WORKDIR /app

# Build Stage에서 생성된 JAR 파일만 복사
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]