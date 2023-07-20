# 使用 openjdk:8-jre-alpine 作为基础镜像
FROM openjdk:8-jre-alpine

# 创建一个新的用户和用户组
RUN addgroup -S spring && adduser -S spring -G spring

# 安装字体
RUN apk add --no-cache ttf-dejavu

# 创建日志文件在APP容器所需的目录
RUN mkdir -p /home/ruoyi/logs && chown -R spring:spring /home/ruoyi/logs

# 切换到新的用户
USER spring:spring

# 复制本地的 ruoyi-admin.jar 文件到容器的 /app.jar
COPY ruoyi-admin.jar /app.jar

# 声明作用 实际作用由 run port 暴露容器的 80 端口
EXPOSE 80

# 定义容器启动时执行的命令
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Dspring.active=dev -jar /app.jar"]

