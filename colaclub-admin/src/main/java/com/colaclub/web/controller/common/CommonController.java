package com.colaclub.web.controller.common;

import com.colaclub.common.annotation.Anonymous;
import com.colaclub.common.config.RuoYiConfig;
import com.colaclub.common.constant.Constants;
import com.colaclub.common.core.domain.AjaxResult;
import com.colaclub.common.utils.DateUtils;
import com.colaclub.common.utils.StringUtils;
import com.colaclub.common.utils.file.FileUploadUtils;
import com.colaclub.common.utils.file.FileUtils;
import com.colaclub.common.utils.file.MinioOSSUtils;
import com.colaclub.common.utils.uuid.UUID;
import com.colaclub.framework.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用请求处理
 *
 * @author colaclub
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);
    private static final String FILE_DELIMETER = ",";

    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private MinioOSSUtils minioOSSUtils;

    private static String getFileExtension(String mimeType) {
        if (mimeType != null && mimeType.contains("/")) {
            return mimeType.substring(mimeType.lastIndexOf("/") + 1);
        }
        return "";
    }

    /**
     * 生成文件路径
     *
     * @param file
     * @return
     */
    private static String generateFilepath(String type, MultipartFile file) {
        String timestamp = DateUtils.dateTimeNow();            // 时间戳
        String originalFilename = file.getOriginalFilename();  // 文件名
        String mimeType = file.getContentType();               // 文件类型
        String fileExtension = getFileExtension(mimeType);     // 文件后缀
        String baseName = originalFilename;                    // 去除文件后缀
        if (originalFilename != null && originalFilename.contains(".")) {
            baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        }

        // 生成文件名
        String finalFileName = String.format("%s/%s_%s.%s", type, timestamp, baseName, fileExtension);
        return finalFileName;
    }

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (!FileUtils.checkAllowDownload(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /** 通用上传请求（单个） */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception {
        try {
            // 本地保存文件
            //      String filePath = RuoYiConfig.getUploadPath(); // 上传文件路径
            //      String fileName = FileUploadUtils.upload(filePath, file); // 上传并返回新文件名称
            //      System.out.println(fileName);
            //      String url = serverConfig.getUrl() + fileName;

            // minio 上传文件
            String originalFilename = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String mimeType = file.getContentType();
            String fileExtension = getFileExtension(mimeType);
            String finalFileName = "images/" + uuid + "." + fileExtension;
            // 使用 MinioOSSUtils 上传文件
            String fileName = minioOSSUtils.putObject(finalFileName, file);

            // 下面一致
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", fileName); // 上传文件保存返回路径
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());

            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /** 通用上传请求（多个） */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception {
        try {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /** 本地资源通用下载 */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            if (!FileUtils.checkAllowDownload(resource)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = RuoYiConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 获取封面
     */
    @Anonymous
    @PostMapping("/generateThumbnail")
    public AjaxResult generateThumbnail(@RequestParam("videos") MultipartFile[] videoFiles, @RequestParam("thumbnails") MultipartFile[] thumbnailFiles) {
        AjaxResult ajax = AjaxResult.success();
        List<Map<String, Object>> dataList = new ArrayList<>();

        try {
            for (int i = 0; i < videoFiles.length; i++) {
                MultipartFile videoFile = videoFiles[i];
                MultipartFile thumbnailFile = thumbnailFiles[i];

                String finalVideoFileName = generateFilepath("video", videoFile);
                String videoFileName = minioOSSUtils.putObject(finalVideoFileName, videoFile);

                String finalThumbnailFileName = generateFilepath("thumbnail", thumbnailFile);
                String thumbnailFileName = minioOSSUtils.putObject(finalThumbnailFileName, thumbnailFile);

                Map<String, Object> data = new HashMap<>();
                data.put("url", videoFileName);
                data.put("thumbnailUrl", thumbnailFileName);
                data.put("newFileName", FileUtils.getName(videoFileName));
                data.put("originalFilename", videoFile.getOriginalFilename());

                dataList.add(data);
            }
            ajax.put("data", dataList);
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error("上传过程出错");
        }
    }

}
