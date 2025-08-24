# 使用官方 OpenJDK 21 运行时镜像
FROM eclipse-temurin:21-jre

# 设置工作目录
WORKDIR /app

# 复制 jar 包到容器
COPY target/WebTurnTable-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口（如有需要可修改）
EXPOSE 8080

# 启动 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]

