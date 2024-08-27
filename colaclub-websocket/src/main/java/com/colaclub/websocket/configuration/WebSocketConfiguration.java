package com.colaclub.websocket.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);
    private static final String TOPIC = "/topic";

    @Bean
    public TaskScheduler customTaskScheduler() {
        // 创建一个 ThreadPoolTaskScheduler 实例，用于调度异步任务
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

        // 设置线程池大小为 1，即只有一个线程来处理调度任务
        taskScheduler.setPoolSize(1);

        // 设置调度线程的名称前缀，所有由该 TaskScheduler 创建的线程将以此前缀开头
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");

        // 初始化 TaskScheduler，使其根据配置的属性准备好调度任务
        taskScheduler.initialize();

        // 返回配置好的 TaskScheduler 实例，Spring 将其作为一个 Bean 注册到应用上下文中
        return taskScheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的消息代理，设置消息主题的前缀为 "/topic"
        // 这个简单代理将处理来自客户端的消息，并将其路由到相应的订阅者
        config.enableSimpleBroker(TOPIC)
                // 设置自定义的 TaskScheduler，用于调度心跳任务
                // TaskScheduler 将定期调度发送和接收心跳包，以确保连接的活跃状态
                .setTaskScheduler(customTaskScheduler())
                // 设置心跳包的发送和接收间隔（单位为毫秒）
                // 在这里，设置为每 20 秒发送和接收一次心跳包
                .setHeartbeatValue(new long[]{20 * 1000, 20 * 1000});

        // 设置应用程序目的地的前缀为 "/app"
        // 当客户端发送消息时，目的地地址必须以 "/app" 开头，这样消息才能被路由到相应的消息处理方法
        config.setApplicationDestinationPrefixes("/app");
        logger.info("Configured message broker with topic prefix: {}", TOPIC);
        logger.info("Application destination prefix set to: /app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
        logger.info("Registered STOMP endpoint at /websocket with SockJS support.");
    }
}