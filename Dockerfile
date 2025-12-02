# =================================================================
# 1. Build Stage: 애플리케이션을 빌드하는 단계
# =================================================================
# Gradle과 JDK 21을 포함한 이미지를 빌더(builder)로 지정
FROM gradle:8.5.0-jdk21 AS builder

# 작업 디렉토리 설정
WORKDIR /home/gradle/src

# 빌드에 필요한 최소한의 파일만 명시적으로 복사합니다.
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

# (중요) gradlew 스크립트의 라인 엔딩(CRLF -> LF)을 수정하고 실행 권한을 부여합니다.
# Windows 호스트 환경에서 발생하는 문제를 해결합니다.
RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew

# Gradle 빌드 실행 (테스트는 제외하여 빌드 속도 향상)
RUN ./gradlew build --no-daemon -x test


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