package com.colaclub.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** MinIO 配置 */
@Component
@ConfigurationProperties(prefix = "oss")
public class MinioConfig {
  /** 地域节点 */
  private String minioEndpoint;

  /** AccessKey */
  private String minioAccessKey;

  /** SecretKey */
  private String minioSecretKey;

  /** bucket名称 */
  private String minioBucket;

  public String getMinioEndpoint() {
    return minioEndpoint;
  }

  public void setMinioEndpoint(String minioEndpoint) {
    this.minioEndpoint = minioEndpoint;
  }

  public String getMinioAccessKey() {
    return minioAccessKey;
  }

  public void setMinioAccessKey(String minioAccessKey) {
    this.minioAccessKey = minioAccessKey;
  }

  public String getMinioSecretKey() {
    return minioSecretKey;
  }

  public void setMinioSecretKey(String minioSecretKey) {
    this.minioSecretKey = minioSecretKey;
  }

  public String getMinioBucket() {
    return minioBucket;
  }

  public void setMinioBucket(String minioBucket) {
    this.minioBucket = minioBucket;
  }

  @Override
  public String toString() {
    return "MinioConfig{"
        + "minioEndpoint='"
        + minioEndpoint
        + '\''
        + ", minioAccessKey='"
        + minioAccessKey
        + '\''
        + ", minioSecretKey='"
        + minioSecretKey
        + '\''
        + ", minioBucket='"
        + minioBucket
        + '\''
        + '}';
  }
}
