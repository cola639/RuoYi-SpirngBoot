// package com.colaclub.common.utils.file;
//
// import com.colaclub.common.config.MinioConfig;
// import io.minio.GetObjectArgs;
// import io.minio.MinioClient;
// import io.minio.PutObjectArgs;
// import io.minio.errors.MinioException;
// import java.io.*;
// import java.security.InvalidKeyException;
// import java.security.NoSuchAlgorithmException;
// import java.time.LocalDateTime;
// import java.util.UUID;
// import javax.annotation.PostConstruct;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import org.springframework.web.multipart.MultipartFile;
//
/// ** MinIO服务调用工具类 */
// @Component
// public class MinioOSSUtils {
//
//  private static final Logger log = LoggerFactory.getLogger(MinioOSSUtils.class);
//
//  private final MinioConfig minioConfig;
//  private MinioClient minioClient;
//
//  @Autowired
//  public MinioOSSUtils(MinioConfig minioConfig) {
//    this.minioConfig = minioConfig;
//  }
//
//  @PostConstruct
//  public void init() {
//    this.minioClient =
//        MinioClient.builder()
//            .endpoint(minioConfig.getMinioEndpoint())
//            .credentials(minioConfig.getMinioAccessKey(), minioConfig.getMinioSecretKey())
//            .build();
//    log.info("MinIO 服务连接成功！");
//  }
//
//  /**
//   * 默认路径上传本地文件
//   *
//   * @param filePath 本地文件路径
//   */
//  public String uploadFile(String filePath) {
//    return uploadFileForBucket(minioConfig.getMinioBucket(), getOssFilePath(filePath), filePath);
//  }
//
//  /**
//   * 默认路径上传 multipartFile 文件
//   *
//   * @param multipartFile 文件
//   */
//  public String uploadMultipartFile(MultipartFile multipartFile) {
//    return uploadMultipartFile(
//        minioConfig.getMinioBucket(),
//        getOssFilePath(multipartFile.getOriginalFilename()),
//        multipartFile);
//  }
//
//  /**
//   * 上传 multipartFile 类型文件
//   *
//   * @param bucketName 存储桶名称
//   * @param ossPath OSS 存储路径
//   * @param multipartFile 文件
//   */
//  public String uploadMultipartFile(
//      String bucketName, String ossPath, MultipartFile multipartFile) {
//    try (InputStream inputStream = multipartFile.getInputStream()) {
//      uploadFileInputStreamForBucket(bucketName, ossPath, inputStream);
//    } catch (IOException e) {
//      log.error("文件上传失败：", e);
//    }
//    return minioConfig.getMinioEndpoint() + "/" + ossPath;
//  }
//
//  /**
//   * 使用文件路径上传文件到指定的 bucket 实例
//   *
//   * @param bucketName 存储桶名称
//   * @param ossPath OSS 存储路径
//   * @param filePath 本地文件路径
//   */
//  public String uploadFileForBucket(String bucketName, String ossPath, String filePath) {
//    try (InputStream inputStream = new FileInputStream(filePath)) {
//      uploadFileInputStreamForBucket(bucketName, ossPath, inputStream);
//    } catch (IOException e) {
//      log.error("文件上传失败：", e);
//    }
//    return minioConfig.getMinioEndpoint() + "/" + ossPath;
//  }
//
//  /**
//   * 使用文件流上传到指定的 bucket 实例
//   *
//   * @param bucketName 存储桶名称
//   * @param ossPath OSS 存储路径
//   * @param inputStream 文件流
//   */
//  public void uploadFileInputStreamForBucket(
//      String bucketName, String ossPath, InputStream inputStream) {
//    try {
//      minioClient.putObject(
//          PutObjectArgs.builder().bucket(bucketName).object(ossPath).stream(
//                  inputStream, inputStream.available(), -1)
//              .build());
//      log.info("文件上传成功：" + ossPath);
//    } catch (MinioException | IOException e) {
//      log.error("文件上传失败：", e);
//    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  /**
//   * 下载文件
//   *
//   * @param ossFilePath OSS 存储路径
//   * @param filePath 本地文件路径
//   */
//  public void downloadFile(String ossFilePath, String filePath) {
//    downloadFileForBucket(minioConfig.getMinioBucket(), ossFilePath, filePath);
//  }
//
//  /**
//   * 下载文件
//   *
//   * @param bucketName 存储桶名称
//   * @param ossFilePath OSS 存储路径
//   * @param filePath 本地文件路径
//   */
//  public void downloadFileForBucket(String bucketName, String ossFilePath, String filePath) {
//    try (InputStream inputStream =
//            minioClient.getObject(
//                GetObjectArgs.builder().bucket(bucketName).object(ossFilePath).build());
//        OutputStream outputStream = new FileOutputStream(filePath)) {
//      byte[] buf = new byte[8192];
//      int bytesRead;
//      while ((bytesRead = inputStream.read(buf, 0, buf.length)) != -1) {
//        outputStream.write(buf, 0, bytesRead);
//      }
//      log.info("文件下载成功：" + filePath);
//    } catch (MinioException | IOException e) {
//      log.error("文件下载失败：", e);
//    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  /**
//   * 获取默认 OSS 路径
//   *
//   * @return 默认 OSS 路径
//   */
//  public String getOssDefaultPath() {
//    LocalDateTime now = LocalDateTime.now();
//    return String.format(
//        "%d/%d/%d/%d/%d/",
//        now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute());
//  }
//
//  /**
//   * 获取 OSS 文件路径
//   *
//   * @param filePath 文件路径
//   * @return OSS 文件路径
//   */
//  public String getOssFilePath(String filePath) {
//    String fileSuf = filePath.substring(filePath.lastIndexOf(".") + 1);
//    return getOssDefaultPath() + UUID.randomUUID().toString() + "." + fileSuf;
//  }
// }
