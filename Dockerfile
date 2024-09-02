# 使用 openjdk:8-jre-alpine 作为基础镜像
FROM openjdk:8-jre-alpine

# 读取Jenkinsfile构建镜像时定义参数
ARG PROFILE
# 定义一个参数用于接收 JAR 文件名
ARG JAR_FILE

# 将ARG转化为ENV，使其可以在运行容器时使用
ENV PROFILE=${PROFILE}
ENV JAR_FILE=${JAR_FILE}

#  在 Docker 容器内创建一个名为 spring 的用户和用户组，用于运行容器进程，避免使用 root 权限，增强安全性
RUN addgroup -S spring && adduser -S spring -G spring

# 安装生成Captcha需要的字体 库
RUN apk add --no-cache ttf-dejavu

# 创建一个工作目录，用于存储日志
RUN mkdir -p /www/logs

# 避免服务监控报错
# 添加glibc的安装源
RUN apk --no-cache add ca-certificates wget && \
    wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub && \
    wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.33-r0/glibc-2.33-r0.apk && \
    apk add glibc-2.33-r0.apk

# 需要 libudev-dev 的替代方案
# 安装udev或相关库
RUN apk --no-cache add eudev-dev

# 安装FFmpeg
RUN apk add --no-cache ffmpeg

# 创建日志文件在APP容器所需的目录
# 与文件logback.xml  <property name="log.path" value="./logs"/> 一致
# 这里第一个 spring 代表用户名，第二个 spring 代表用户组名。这意味着你将文件或目录的所有权更改为用户 spring 和组 spring
RUN mkdir -p /logs && chown -R spring:spring /logs

# 切换到新的用户
USER spring:spring

# 复制本地的 JAR 文件到容器的 /app.jar
COPY ${JAR_FILE} /app.jar

# 声明作用无实际作用 实际由docker run port 暴露容器端口
EXPOSE 80 8888

# 该容器启动命令配置了 JVM 以优化其在容器环境中的性能，并指定了运行时的 Java 参数。
# 该配置启动了一个 Spring Boot 应用程序，并使用环境变量控制 Spring 配置概要。
# -XX:+UseContainerSupport：启用容器支持，确保 JVM 能够识别和尊重容器的内存限制。
# -XX:MaxRAMPercentage=70.0：设置 JVM 使用的最大内存为分配给容器内存的 70%，以确保合理的内存分配。
# -Djava.security.egd=file:/dev/./urandom：优化随机数生成器的性能，缩短应用程序启动时间。
# -Dname=target/ruoyi-vue.jar：指定 JVM 进程名称为 'target/ruoyi-vue.jar'，便于进程管理和监控。
# -Duser.timezone=Asia/Shanghai：设置 JVM 的时区为上海，以确保时间相关操作符合预期。
# -Xms512m：设置初始堆内存大小为 512MB，确保 JVM 启动时有足够的内存。
# -Xmx1024m：设置最大堆内存大小为 1024MB，限制 JVM 的最大内存使用量。
# -XX:MetaspaceSize=128m：配置元空间的初始大小为 128MB，优化类加载的内存使用。
# -XX:MaxMetaspaceSize=512m：设置元空间的最大大小为 512MB，防止内存过度使用。
# -XX:+HeapDumpOnOutOfMemoryError：在内存溢出时生成堆转储文件，便于分析内存问题。
# -XX:+PrintGCDateStamps：启用垃圾回收（GC）日志中的时间戳，便于日志分析。
# -XX:+PrintGCDetails：启用详细的垃圾回收日志，帮助优化内存管理。
# -XX:NewRatio=1：设置新生代与老年代的比例为 1:1，平衡内存分配策略。
# -XX:SurvivorRatio=30：设置 Eden 区与 Survivor 区的比例为 30:1，优化新生代内存使用。
# -XX:+UseParallelGC：启用并行垃圾回收器，适合多核 CPU 环境，提高 GC 效率。
# -XX:+UseParallelOldGC：启用老年代的并行垃圾回收器，提高整体垃圾回收性能。
ENTRYPOINT ["sh", "-c", "java \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=70.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dname=target/ruoyi-vue.jar \
    -Duser.timezone=Asia/Shanghai \
    -Xms512m \
    -Xmx1024m \
    -XX:MetaspaceSize=128m \
    -XX:MaxMetaspaceSize=512m \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:+PrintGCDateStamps \
    -XX:+PrintGCDetails \
    -XX:NewRatio=1 \
    -XX:SurvivorRatio=30 \
    -XX:+UseParallelGC \
    -XX:+UseParallelOldGC \
    -jar /app.jar --spring.profiles.active=${PROFILE}"]

