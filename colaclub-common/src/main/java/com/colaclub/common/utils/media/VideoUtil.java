package com.colaclub.common.utils.media;

import com.colaclub.common.utils.DateUtils;
import com.colaclub.common.utils.file.MinioOSSUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class VideoUtil {
    // 截图保存路径
    private static final String savePath = "D:\\screenshots";
    // 取第几秒帧
    private static final Integer seconds = 2;
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoUtil.class);
    @Autowired
    private MinioOSSUtils minioOSSUtils;

    /**
     * 计算图片旋转大小
     *
     * @param src   Rectangle
     * @param angle int
     * @return Rectangle
     */
    public Rectangle calcRotatedSize(Rectangle src, int angle) {
        if (angle >= 90) {
            if (angle / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angle = angle % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angle) / 2) * r;
        double angelAlpha = (Math.PI - Math.toRadians(angle)) / 2;
        double angelDeltaWidth = Math.atan((double) src.height / src.width);
        double angelDeltaHeight = Math.atan((double) src.width / src.height);

        int lenDeltaWidth = (int) (len * Math.cos(Math.PI - angelAlpha - angelDeltaWidth));
        int lenDeltaHeight = (int) (len * Math.cos(Math.PI - angelAlpha - angelDeltaHeight));
        int desWidth = src.width + lenDeltaWidth * 2;
        int desHeight = src.height + lenDeltaHeight * 2;
        return new Rectangle(new Dimension(desWidth, desHeight));
    }

    /**
     * 从上传的MultipartFile视频截图
     *
     * @param file 上传的MultipartFile文件
     * @return Map<String, Object>
     */
    public Map<String, Object> getScreenshot(MultipartFile file) {
        File tempFile = null;
        try {
            // 将MultipartFile转换为临时文件
            tempFile = File.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);
            tempFile.deleteOnExit();
            return getScreenshotFromFile(tempFile);
        } catch (IOException e) {
            LOGGER.error("获取视频截图失败: {}", e.getMessage());
            return new HashMap<>();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    LOGGER.warn("无法删除临时文件: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 从本地文件获取视频截图
     *
     * @param videoFile 本地视频文件
     * @return Map<String, Object>
     * @throws IOException
     */
    private Map<String, Object> getScreenshotFromFile(File videoFile) throws IOException {
        return getScreenshotFromGrabber(new FFmpegFrameGrabber(videoFile));
    }

    /**
     * 生成文件路径
     *
     * @param type
     * @return
     */
    private String generateFilepath(String type) {
        String timestamp = DateUtils.dateTimeNow();            // 时间戳
        // 文件类型
        String fileExtension = ".jpg";     // 文件后缀

        // 生成文件名
        String finalFileName = String.format("%s/%s%s", "images", timestamp, fileExtension);
        return finalFileName;
    }

    /**
     * 根据视频旋转度来调整图片
     *
     * @param src   BufferedImage
     * @param angle 视频旋转度
     * @return BufferedImage
     */
    public BufferedImage rotate(BufferedImage src, int angle) {
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int type = src.getColorModel().getTransparency();
        Rectangle rectDes = calcRotatedSize(new Rectangle(new Dimension(srcWidth, srcHeight)), angle);

        BufferedImage bi = new BufferedImage(rectDes.width, rectDes.height, type);
        Graphics2D g2 = bi.createGraphics();
        g2.translate((rectDes.width - srcWidth) / 2, (rectDes.height - srcHeight) / 2);
        g2.rotate(Math.toRadians(angle), srcWidth / 2.0, srcHeight / 2.0);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return bi;
    }

    /**
     * 使用FFmpegFrameGrabber获取视频截图
     *
     * @param grabber FFmpegFrameGrabber实例
     * @return Map<String, Object>
     * @throws IOException
     */
    private Map<String, Object> getScreenshotFromGrabber(FFmpegFrameGrabber grabber) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            grabber.start();

            // 定位到特定时间
            grabber.setTimestamp(seconds * 1000000L); // 秒转换为微秒
            Frame frame = grabber.grabImage();

            // 获取视频元数据中的旋转信息
            String rotate = grabber.getVideoMetadata("rotate");
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bi = converter.getBufferedImage(frame);

            // 旋转图片
            if (rotate != null) {
                bi = rotate(bi, Integer.parseInt(rotate));
            }

            // 保存图片到savePath
            File targetDir = new File(savePath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            String fileName = grabber.getFormatContext().url().getString(StandardCharsets.UTF_8);
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            String targetFileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lastIndexOf("."));
            LOGGER.debug("fileName: {}", fileName);
            LOGGER.debug("savePath: {}", savePath);
            LOGGER.debug("separator: {}", File.separator);
            LOGGER.debug("targetFileName: {}", targetFileName);
            // 生成时间戳
            String timestamp = DateUtils.dateTimeNow();
            String imagePath = String.format("%s%s%s_%s.jpg", savePath, File.separator, timestamp, targetFileName);
            File output = new File(imagePath);
            // 保存图片到本地
            ImageIO.write(bi, "jpg", output);

            // 上传到 Minio
            String finalFileName = generateFilepath("image");
            LOGGER.debug("finalFileName: {}", finalFileName);
            String minioUrl = uploadToMinio(finalFileName, output);
            LOGGER.debug("minioUrl: {}", minioUrl);

            // 拼接Map信息
            result.put("minioUrl", minioUrl);
            result.put("videoWidth", bi.getWidth());
            result.put("videoHeight", bi.getHeight());
            result.put("rotate", rotate != null ? rotate : "0");
            result.put("format", grabber.getFormat());
            result.put("imgPath", output.getPath());
            result.put("duration", grabber.getLengthInTime() / (1000 * 1000));

            LOGGER.debug("视频的宽: {}", bi.getWidth());
            LOGGER.debug("视频的高: {}", bi.getHeight());
            LOGGER.debug("视频的旋转度: {}", rotate);
            LOGGER.debug("视频的格式: {}", grabber.getFormat());
            LOGGER.debug("视频的时长（秒）: {}", grabber.getLengthInTime() / (1000 * 1000));
            LOGGER.debug("截图保存路径: {}", imagePath); // 日志记录截图保存路径

            grabber.stop();
        } catch (Exception e) {
            LOGGER.error("获取视频截图失败: {}", e.getMessage());
        } finally {
            try {
                grabber.close();
            } catch (IOException e) {
                LOGGER.error("关闭抓取器失败: {}", e.getMessage());
            }
        }
        return result;
    }

    private String getFileExtension(String mimeType) {
        if (mimeType != null && mimeType.contains("/")) {
            return mimeType.substring(mimeType.lastIndexOf("/") + 1);
        }
        return "";
    }

    private String uploadToMinio(String finalFileName, File output) {
        try {
            return minioOSSUtils.putFileObject(finalFileName, output);
        } catch (Exception e) {
            LOGGER.error("上传文件到Minio失败: {}", e.getMessage());
            return null;
        }
    }

    public void testVideo() throws IOException {
        // 本地视频文件路径
        String localVideoPath = "D:\\test.mp4";
        // 网络视频文件路径
        String remoteVideoPath = "https://vjs.zencdn.net/v/oceans.mp4";

        // 处理本地视频文件
        Map<String, Object> localScreenshot = getScreenshot(localVideoPath);
        System.out.println("本地视频截图信息: " + localScreenshot);

        // 处理网络视频文件
        Map<String, Object> remoteScreenshot = getScreenshot(remoteVideoPath);
        System.out.println("网络视频截图信息: " + remoteScreenshot);
    }

    /**
     * 从视频流中获取视频截图
     *
     * @param videoUrl 视频流URL
     * @return Map<String, Object>
     * @throws IOException
     */
    private Map<String, Object> getScreenshotFromStream(String videoUrl) throws IOException {
        return getScreenshotFromGrabber(new FFmpegFrameGrabber(videoUrl));
    }

    /**
     * 通过JavaCV获取视频截图（处理本地文件路径或URL）
     *
     * @param filePath 视频文件路径或URL
     * @return Map<String, Object>
     */
    public Map<String, Object> getScreenshot(String filePath) {
        try {
            // 判断是否为外链视频
            if (filePath.startsWith("http")) {
                return getScreenshotFromStream(filePath);
            } else {
                return getScreenshotFromFile(new File(filePath));
            }
        } catch (IOException e) {
            LOGGER.error("获取视频截图失败: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
