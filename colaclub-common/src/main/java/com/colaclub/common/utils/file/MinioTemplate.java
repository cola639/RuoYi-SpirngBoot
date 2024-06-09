package com.colaclub.common.utils.file;

import io.minio.*;
import io.minio.errors.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class MinioTemplate {
  @Value("${oss.minioBucket}")
  public String bucketName;

  @Value("${oss.minioEndpoint}")
  public String minioEndpoint;

  @Autowired private MinioClient minioClient;

  /** 列出桶 */
  public boolean listBucket() {
    try {
      System.out.println("Testing connection to MinIO server...");
      List<String> buckets =
          minioClient.listBuckets().stream()
              .map(bucket -> bucket.name())
              .collect(Collectors.toList());
      System.out.println("buckets = " + buckets);

      return true;
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
      System.out.println("e = " + e);
      return false;
    }
  }

  /**
   * 判断桶是否存在,如果存在返回true,如果不存在返回false
   *
   * @param bucketName
   * @return
   */
  @SneakyThrows
  public Boolean existBucket(String bucketName) {
    boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    System.out.println("exist = " + exist);
    if (!exist) {
      return false;
    }
    return true;
  }

  /**
   * 创建桶
   *
   * @param bucketName
   * @return
   */
  @SneakyThrows
  public void makeBucket(String bucketName) {
    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
  }

  /**
   * 上传对象
   *
   * @param objectName
   * @param file
   * @return
   * @throws Exception
   */
  /**
   * 上传对象
   *
   * @param objectName
   * @param file
   * @return 文件的预览或下载 URL
   * @throws Exception
   */
  @SneakyThrows
  public String putObject(String objectName, MultipartFile file) {
    System.out.println("objectName = " + objectName);
    System.out.println("bucketName = " + bucketName);
    // 判断桶是否存在
    boolean flag = existBucket(bucketName);
    System.out.println("flag = " + flag);

    if (flag) {
      PutObjectArgs args =
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(objectName)
              .contentType(file.getContentType())
              .stream(file.getInputStream(), file.getSize(), -1)
              .build();
      minioClient.putObject(args);

      // 返回文件的预览或下载 URL
      System.out.println("url = " + minioEndpoint + "/" + bucketName + "/" + objectName);
      return minioEndpoint + "/" + bucketName + "/" + objectName;

      // 返回带签名的 URL，有效期为7天
      //      return minioClient.getPresignedObjectUrl(
      //              GetPresignedObjectUrlArgs.builder()
      //                      .method(Method.GET)
      //                      .bucket(bucketName)
      //                      .object(objectName)
      //                      .expiry(7, TimeUnit.DAYS)
      //                      .build());

    }
    return null;
  }

  /**
   * 删除对象
   *
   * @param objectName
   * @return
   * @throws Exception
   */
  @SneakyThrows
  public boolean removeObject(String objectName) {
    boolean flag = existBucket(bucketName);
    if (flag) {
      RemoveObjectArgs args =
          RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build();
      minioClient.removeObject(args);
      return true;
    }
    return false;
  }

  /**
   * 获取对象信息
   *
   * @param objectName
   * @return
   * @throws Exception
   */
  @SneakyThrows
  public ObjectStat getMessage(String objectName) {
    boolean flag = existBucket(bucketName);
    if (flag) {
      ObjectStat statObject =
          minioClient.statObject(
              StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
      return statObject;
    }
    return null;
  }

  /**
   * 返回的文件路径,不会过期.
   *
   * @param objectName
   * @return
   */
  @SneakyThrows
  public String getObjectUrl(String objectName) {
    Boolean flag = existBucket(bucketName);
    String url = "";
    if (flag) {
      url = minioClient.getObjectUrl(bucketName, objectName);
    }
    return url;
  }

  /**
   * 下载
   *
   * @param filename
   * @param response
   */
  public void getObject(String filename, HttpServletResponse response) {
    InputStream in = null;
    OutputStream out = null;
    try {
      in =
          minioClient.getObject(
              GetObjectArgs.builder().bucket(bucketName).object(filename).build());
      int length = 0;
      byte[] buffer = new byte[1024];
      out = response.getOutputStream();
      response.reset();
      response.addHeader(
          "Content-Disposition", " attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
      response.setContentType("application/octet-stream");
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
