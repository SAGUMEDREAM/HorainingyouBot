package cc.thonly.horainingyoubot.util;

import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;

@Slf4j
public class HTTPReq {
    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * 下载单个文件并将其转换为 byte[]
     */
    public static byte[] downloadFile(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
//                log.info("Successfully downloaded file from {}", url);
                return response.body();
            } else {
                log.error("Failed to download file from {} with status code: {}", url, response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error downloading file from: {}", url, e);
        }

        return null;
    }

    /**
     * 下载多个文件并将其转换为 List<byte[]>（每个文件的 byte[] 数组）
     */
    public static List<byte[]> downloadFiles(List<String> urls) {
        List<byte[]> filesData = new ArrayList<>();

        for (String url : urls) {
            byte[] fileData = downloadFile(url);
            if (fileData != null) {
                filesData.add(fileData);
            }
        }

        return filesData;
    }

    /**
     * 将 List<byte[]> 转换为 JPG 图片并返回 byte[] 数组
     */
    public static List<byte[]> asJPG(List<byte[]> filesData) {
        List<byte[]> jpgImages = new ArrayList<>();

        for (int i = 0; i < filesData.size(); i++) {
            byte[] imageData = filesData.get(i);
            try {
                // 将 byte[] 转换为 BufferedImage
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

                if (image != null) {
                    // 将 BufferedImage 写入到 ByteArrayOutputStream 中
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", baos);
                    baos.flush();
                    byte[] jpgData = baos.toByteArray();
                    jpgImages.add(jpgData);  // 添加到结果列表
                    baos.close();

                    log.info("Image {} successfully converted to JPG byte[]", i + 1);
                } else {
                    log.error("Failed to convert byte[] to image for file: {}", i + 1);
                }
            } catch (IOException e) {
                log.error("Error converting image {} to byte[]", i + 1, e);
            }
        }

        return jpgImages;
    }
}