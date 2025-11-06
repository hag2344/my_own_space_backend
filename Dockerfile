# 안정적이고 ARM/x86 호환되는 OpenJDK 17 이미지
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉터리 설정
WORKDIR /app

# 모든 파일 복사
COPY . .

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 빌드 (테스트 생략)
RUN ./gradlew clean build -x test

# 앱 실행
CMD ["java", "-jar", "build/libs/myownspace-0.0.1-SNAPSHOT.jar"]