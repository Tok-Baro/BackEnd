# 실행에 필요한 최소한의 Java(JRE) 환경
FROM eclipse-temurin:21-jre

# 작업 디렉토리
WORKDIR /app

# 로컬(또는 CI)에서 빌드된 JAR 파일을 컨테이너로 복사
# 중요: 이 경로에 JAR 파일이 미리 생성되어 있어야 합니다.
COPY build/libs/*.jar /app/app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]