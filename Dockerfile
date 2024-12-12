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

# 创建日志和配置文件目录，并设置权限
RUN mkdir -p /www/logs && chown -R spring:spring /www/logs

# 切换到新的用户
USER spring:spring

# 复制本地的 JAR 文件到容器的 /app.jar
COPY ${JAR_FILE} /app.jar

# 声明作用无实际作用 实际由docker run port 暴露容器端口
EXPOSE 80 8888

# 该容器启动命令配置了 JVM 以优化其在容器环境中的性能，并指定了运行时的 Java 参数。
# 该配置启动了一个 Spring Boot 应用程序，并使用环境变量控制 Spring 配置概要。

# -XX:+UnlockExperimentalVMOptions：解锁实验性 JVM 配置选项，允许使用最新的 GC 和内存管理参数。
# -XX:+UseContainerSupport：启用容器支持，确保 JVM 能识别和尊重容器的内存、CPU 限制。
# -XX:MaxRAMPercentage=75.0：设置 JVM 最大堆内存为容器分配内存的 75%，平衡内存使用。
# -XX:+AlwaysPreTouch：在 JVM 启动时预分配内存页，确保内存访问更快，降低延迟。

# -Djava.security.egd=file:/dev/./urandom：优化随机数生成器的性能，缩短应用启动时间。
# -Duser.timezone=Asia/Shanghai：设置 JVM 的时区为上海，确保应用时间与本地时间一致。

# -Xms512m：设置初始堆内存大小为 1024，避免启动时内存动态分配造成的性能问题。
# -Xmx1024m：设置最大堆内存大小为 2048MB，限制 JVM 内存使用，避免过度使用容器资源。
# -XX:MetaspaceSize=128m：设置元空间初始大小为 128MB，优化类加载的内存管理。
# -XX:MaxMetaspaceSize=512m：限制元空间的最大大小为 512MB，防止类加载内存占用过多。

# -XX:SurvivorRatio=8：设置 Eden 区与 Survivor 区的比例为 8:1，提升新生代垃圾回收效率。
# -XX:MaxTenuringThreshold=15：设置对象在新生代的最大年龄为 15，优化对象晋升策略。

# -XX:+HeapDumpOnOutOfMemoryError：内存溢出时生成堆转储文件，便于诊断内存问题。
# -XX:HeapDumpPath=/app/dumps：指定堆转储文件存储路径，便于文件管理。

# -XX:+UseG1GC：启用 G1 垃圾回收器，适用于大堆内存环境，提供更高的 GC 性能和低延迟。
# -XX:InitiatingHeapOccupancyPercent=45：当堆内存使用达到 45% 时触发 GC，优化回收时机。
# -XX:+ParallelRefProcEnabled：并行处理 GC 的引用队列，减少 GC 延迟。
# -XX:+ExplicitGCInvokesConcurrent：确保显式调用 System.gc() 时，触发并发 GC，而非 Full GC。

# -XX:+PrintGCDateStamps：在 GC 日志中打印时间戳，便于分析日志与性能问题。
# -XX:+PrintGCDetails：启用详细的 GC 日志记录，帮助分析内存使用和回收情况。
# -Xlog:gc*:file=/app/logs/gc.log:time,uptime,level,tags：记录 GC 日志到指定文件，便于长期分析。

# -XX:+ExitOnOutOfMemoryError：内存溢出时强制退出应用，避免容器进入不确定状态。

# -jar /app.jar：指定运行的 Spring Boot 可执行 Jar 包。
# --spring.profiles.active=${PROFILE}：动态加载 Spring 配置文件，根据环境变量选择不同的配置。
ENTRYPOINT ["sh", "-c", "java \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+AlwaysPreTouch \
    -Djava.security.egd=file:/dev/./urandom \
    -Duser.timezone=Asia/Shanghai \
    -Xms1024m \
    -Xmx2048m \
    -XX:MetaspaceSize=128m \
    -XX:MaxMetaspaceSize=512m \
    -XX:SurvivorRatio=8 \
    -XX:MaxTenuringThreshold=15 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/dumps \
    -XX:+UseG1GC \
    -XX:InitiatingHeapOccupancyPercent=45 \
    -XX:+ParallelRefProcEnabled \
    -XX:+ExplicitGCInvokesConcurrent \
    -XX:+PrintGCDateStamps \
    -XX:+PrintGCDetails \
    -Xlog:gc*:file=/app/logs/gc.log:time,uptime,level,tags \
    -XX:+ExitOnOutOfMemoryError \
    -jar /app.jar --spring.profiles.active=${PROFILE}"]

