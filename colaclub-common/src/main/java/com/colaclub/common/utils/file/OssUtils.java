 package com.colaclub.common.utils.file;

 import com.aliyun.oss.OSS;
 import com.aliyun.oss.OSSClientBuilder;
 import com.aliyun.oss.model.GetObjectRequest;
 import com.aliyun.oss.model.PutObjectRequest;
 import lombok.extern.log4j.Log4j2;
 import org.springframework.web.multipart.MultipartFile;

 import java.io.*;
 import java.time.LocalDateTime;
 import java.util.UUID;

/ **
 * Oss服务调用
 */
 @Log4j2
 public class OssUtils {

    /**
     * Endpoint 存储对象概述  阿里云主账号AccessKey，accessKeySecret拥有所有API的访问权限  访问路径前缀  存储对象概述
     */
    private static String endPoint = "oss-cn-shanghai.aliyuncs.com";
    private static String accessKeyId = "替换成自己的accessKeyId ";
    private static String accessKeySecret = "替换成自己的accessKeySecret";
    private static String accessPre = "https://dzlmuyu.oss-cn-shanghai.aliyuncs.com/";

    /**
     * bucket名称
     *
     * @return
     */
    private static String bucketName = "dzlmuyu";

    private static OSS ossClient;

    static {
        ossClient = new OSSClientBuilder().build(
                endPoint,
                accessKeyId,
                accessKeySecret);
        log.info("oss服务连接成功！");
    }

    /**
     * 默认路径上传本地文件
     *
     * @param filePath
     */
    public static String uploadFile(String filePath) {
        return uploadFileForBucket(bucketName, getOssFilePath(filePath), filePath);
    }

    /**
     * 默认路径上传multipartFile文件
     *
     * @param multipartFile
     */
    public static String uploadMultipartFile(MultipartFile multipartFile) {
        return uploadMultipartFile(bucketName,
 getOssFilePath(multipartFile.getOriginalFilename()), multipartFile);
    }

    /**
     * 上传 multipartFile 类型文件
     *
     * @param bucketName
     * @param ossPath
     * @param multipartFile
     */
    public static String uploadMultipartFile(String bucketName, String ossPath, MultipartFile
 multipartFile) {
        InputStream inputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadFileInputStreamForBucket(bucketName, ossPath, inputStream);
        return accessPre + ossPath;
    }

    /**
     * 使用File上传PutObject上传文件 ** 程序默认使用次方法上传
     *
     * @param bucketName 实例名称
     * @param ossPath    oss存储路径
     * @param filePath   本地文件路径
     */
    public static String uploadFileForBucket(String bucketName, String ossPath, String filePath) {
        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, ossPath, new
 File(filePath));

        // 上传
        ossClient.putObject(putObjectRequest);
        return accessPre + ossPath;
    }

    /**
     * 使用文件流上传到指定的bucket实例
     *
     * @param bucketName 实例名称
     * @param ossPath    oss存储路径
     * @param filePath   本地文件路径
     */
    public static String uploadFileInputStreamForBucket(String bucketName, String ossPath, String
 filePath) {

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        uploadFileInputStreamForBucket(bucketName, ossPath, inputStream);
        return accessPre + ossPath;
    }

    public static void uploadFileInputStreamForBucket(String bucketName, String ossPath,
 InputStream inputStream) {
        ossClient.putObject(bucketName, ossPath, inputStream);
    }

    /**
     * 下载
     *
     * @param ossFilePath
     * @param filePath
     */
    public static void downloadFile(String ossFilePath, String filePath) {
        downloadFileForBucket(bucketName, ossFilePath, filePath);
    }

    /**
     * 下载
     *
     * @param bucketName  实例名称
     * @param ossFilePath oss存储路径
     * @param filePath    本地文件路径
     */
    public static void downloadFileForBucket(String bucketName, String ossFilePath, String
 filePath) {
        ossClient.getObject(new GetObjectRequest(bucketName, ossFilePath), new File(filePath));
    }

    /**
     * @return
     */
    public static String getOssDefaultPath() {
        LocalDateTime now = LocalDateTime.now();
        String url =
                now.getYear() + "/" +
                        now.getMonth() + "/" +
                        now.getDayOfMonth() + "/" +
                        now.getHour() + "/" +
                        now.getMinute() + "/";
        return url;
    }

    public static String getOssFilePath(String filePath) {
        String fileSuf = filePath.substring(filePath.indexOf(".") + 1);
        return getOssDefaultPath() + UUID.randomUUID().toString() + "." + fileSuf;
    }

 }
