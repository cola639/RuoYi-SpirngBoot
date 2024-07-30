package com.colaclub.common.utils.media;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class FFmpegUtils {

    private static final Logger logger = LoggerFactory.getLogger(FFmpegUtils.class);

    /**
     * 从视频 URL 生成第一帧截图
     *
     * @param videoUrl        视频 URL
     * @param outputImagePath 输出图像路径
     * @return 是否成功
     */
    public static boolean generateThumbnail(String videoUrl, String outputImagePath) {
        String ffmpegPath = "C:\\ffmpeg\\bin\\ffmpeg";  // 请根据你的实际安装路径修改

        // FFmpeg 命令
        String command = String.format("%s -i %s -frames:v 1 %s", ffmpegPath, videoUrl, outputImagePath);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            Process process = processBuilder.start();

            // 创建读取标准输出流的线程
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
            outputGobbler.start();
            errorGobbler.start();

            int exitCode = process.waitFor();
            outputGobbler.join();
            errorGobbler.join();

            if (exitCode != 0) {
                logger.error("FFmpeg exited with code {}", exitCode);
            }
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            logger.error("Exception while generating thumbnail", e);
            return false;
        }
    }

    private static class StreamGobbler extends Thread {
        private final InputStream inputStream;
        private final String type;

        StreamGobbler(InputStream inputStream, String type) {
            this.inputStream = inputStream;
            this.type = type;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if ("ERROR".equals(type)) {
                        logger.error(line);
                    } else {
                        logger.info(line);
                    }
                }
            } catch (IOException e) {
                logger.error("Exception while reading the stream", e);
            }
        }
    }
}