// package com.colaclub.common.config;
//
// import io.minio.MinioClient;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
/// ** 创建Minio客户端 */
// @Configuration()
// public class MinioConfig {
//  private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);
//
//  @Value("${oss.minioAccessKey}")
//  private String accessKey;
//
//  @Value("${oss.minioSecretKey}")
//  private String secretKey;
//
//  @Value("${oss.minioEndpoint}")
//  private String url;
//
//  /** 注入客户端 */
//  @Bean
//  public MinioClient minioClient() {
//    log.info("Initializing MinioClient with URL: {}", url);
//    MinioClient minioClient =
//        MinioClient.builder().credentials(accessKey, secretKey).endpoint(url).build();
//    log.info("MinioClient initialized successfully");
//    return minioClient;
//  }
// }
