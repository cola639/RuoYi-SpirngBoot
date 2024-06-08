package com.colaclub.common.utils.file;

import com.colaclub.common.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author
 * @description MinIO 对象存储上传工具类
 * @date 2024/06/09
 */
@Component
public class MinioOssUploadUtils {

  private static MinioConfig minioConfig;

  /** 使用构造方法注入配置信息 */
  @Autowired
  public MinioOssUploadUtils(MinioConfig minioConfig) {
    MinioOssUploadUtils.minioConfig = minioConfig;
  }

  /**
   * 上传文件
   *
   * @param file
   * @return
   * @throws Exception
   */
  public static String uploadFile(MultipartFile file) throws Exception {
    // 创建 MinioClient
    MinioClient minioClient =
        MinioClient.builder()
            .endpoint(minioConfig.getMinioEndpoint())
            .credentials(minioConfig.getMinioAccessKey(), minioConfig.getMinioSecretKey())
            .build();

    // 原始文件名称
    // String originalFilename = file.getOriginalFilename();

    // 编码文件名
    String filePathName = FileUploadUtils.extractFilename(file);
    // 文件路径名称
    filePathName = minioConfig.getMinioBucket() + "/" + filePathName;

    try {
      minioClient.putObject(
          PutObjectArgs.builder().bucket(minioConfig.getMinioBucket()).object(filePathName).stream(
                  file.getInputStream(), file.getSize(), -1)
              .contentType(file.getContentType())
              .build());
    } catch (MinioException | IOException e) {
      e.printStackTrace();
      throw new RuntimeException("文件上传失败", e);
    }

    return minioConfig.getMinioEndpoint() + "/" + filePathName;
  }
}
