# 使用 openjdk:8-jre-alpine 作为基础镜像
FROM openjdk:8-jre-alpine

# Jenkinsfile构建镜像时定义参数
ARG PROFILE
# 定义一个参数用于接收 JAR 文件名
ARG JAR_FILE

# 将ARG转化为ENV，使其可以在运行容器时使用
ENV PROFILE=${PROFILE}
ENV JAR_FILE=${JAR_FILE}

#  在 Docker 容器内创建一个名为 spring 的用户和用户组，用于运行容器进程，避免使用 root 权限，增强安全性
RUN addgroup -S spring && adduser -S spring -G spring

# 安装字体 Captcha 库
RUN apk add --no-cache ttf-dejavu

# 创建日志文件在APP容器所需的目录
# 与文件logback.xml  <property name="log.path" value="./logs"/> 一致
# 这里第一个 spring 代表用户名，第二个 spring 代表用户组名。这意味着你将文件或目录的所有权更改为用户 spring 和组 spring
RUN mkdir -p /logs && chown -R spring:spring /logs

# 切换到新的用户
USER spring:spring

# 复制本地的 JAR 文件到容器的 /app.jar
COPY ${JAR_FILE} /app.jar

# 声明作用无实际作用 实际由docker run port 暴露容器端口
EXPOSE 80

# 设置容器的默认启动命令，配置 JVM 以优化容器环境运行，指定使用的 Spring 配置概要
# 使用 UseContainerSupport 来使 JVM 识别容器内存限制
# 使用 MaxRAMPercentage 设置 JVM 使用的最大内存百分比
# 使用 java.security.egd 改进随机数生成性能
# 启动指定的 Jar 文件并设置 Spring 的活动配置概要
ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -jar /app.jar --spring.profiles.active=${PROFILE}"]
