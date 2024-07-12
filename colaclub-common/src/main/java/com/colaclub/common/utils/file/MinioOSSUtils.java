package com.colaclub.common.utils.file;

import com.colaclub.common.config.MinioConfig;
import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MinioOSSUtils {
    private final String bucketName;
    private final String minioEndpoint;
    private final MinioClient minioClient;

    @Autowired
    public MinioOSSUtils(MinioConfig minioConfig, MinioClient minioClient) {
        this.bucketName = minioConfig.getMinioBucket();
        this.minioEndpoint = minioConfig.getMinioEndpoint();
        this.minioClient = minioClient;
    }

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
    public Boolean existBucket(String bucketName) throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建桶
     *
     * @param bucketName
     * @throws Exception
     */
    public void makeBucket(String bucketName) throws Exception {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 上传对象
     *
     * @param objectName
     * @param file
     * @return 文件的预览或下载 URL
     * @throws Exception
     */
    public String putObject(String objectName, MultipartFile file) throws Exception {
        // 判断桶是否存在
        boolean flag = existBucket(bucketName);

        // 过滤文件
        FileUploadUtils.assertAllowed(file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);

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
            return minioEndpoint + "/" + bucketName + "/" + objectName;

            // 如果需要带签名的 URL，可以使用下面的代码返回带签名的 URL
//            return minioClient.getPresignedObjectUrl(
//                    GetPresignedObjectUrlArgs.builder()
//                            .method(Method.GET)
//                            .bucket(bucketName)
//                            .object(objectName)
//                            .expiry(7, TimeUnit.DAYS)
//                            .build());
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
    public boolean removeObject(String objectName) throws Exception {
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
    public ObjectStat getMessage(String objectName) throws Exception {
        boolean flag = existBucket(bucketName);
        if (flag) {
            return minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        }
        return null;
    }

    /**
     * 返回的文件路径,不会过期.
     *
     * @param objectName
     * @return
     * @throws Exception
     */
    public String getObjectUrl(String objectName) throws Exception {
        boolean flag = existBucket(bucketName);
        if (flag) {
            return minioClient.getObjectUrl(bucketName, objectName);
        }
        return null;
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
            int length;
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
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
