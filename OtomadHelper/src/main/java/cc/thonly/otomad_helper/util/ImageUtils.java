package cc.thonly.otomad_helper.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

@Slf4j
public class ImageUtils {

    public static byte[] generateAvatar(String framePath, String imageUrl) throws IOException {
        try {
            BufferedImage userAvatar = ImageIO.read(new URL(imageUrl));
            BufferedImage frame = ImageIO.read(new File(framePath));
            int width = frame.getWidth();
            int height = frame.getHeight();
            BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = canvas.createGraphics();
            g.drawImage(frame, 0, 0, null);

            int size = Math.min(userAvatar.getWidth(), userAvatar.getHeight());
            BufferedImage roundAvatar = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D avatarGraphics = roundAvatar.createGraphics();
            avatarGraphics.setClip(new Ellipse2D.Float(0, 0, size, size));
            avatarGraphics.drawImage(userAvatar, 0, 0, size, size, null);
            avatarGraphics.dispose();

            g.drawImage(roundAvatar, 158, 190, 854, 854, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(canvas, "PNG", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error: ", e);
            throw new IOException("Image composition failed");
        }
    }

    public static byte[] generateAvatar(Class<?> clazz, String framePath, String imageUrl) throws IOException {
        try {
            BufferedImage userAvatar = ImageIO.read(new URL(imageUrl));

            try (InputStream frameStream = clazz.getResourceAsStream(framePath)) {
                if (frameStream == null) {
                    throw new FileNotFoundException("Frame resource not found: " + framePath);
                }
                BufferedImage frame = ImageIO.read(frameStream);

                int width = frame.getWidth();
                int height = frame.getHeight();
                BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = canvas.createGraphics();

                g.drawImage(frame, 0, 0, null);

                int size = Math.min(userAvatar.getWidth(), userAvatar.getHeight());
                BufferedImage roundAvatar = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D avatarGraphics = roundAvatar.createGraphics();
                avatarGraphics.setClip(new Ellipse2D.Float(0, 0, size, size));
                avatarGraphics.drawImage(userAvatar, 0, 0, size, size, null);
                avatarGraphics.dispose();

                g.drawImage(roundAvatar, 158, 190, 854, 854, null);
                g.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(canvas, "PNG", baos);
                return baos.toByteArray();
            }

        } catch (IOException e) {
            log.error("Error: ", e);
            throw new IOException("Image composition failed", e);
        }
    }

    public static byte[] generateAvatar(Class<?> clazz, String framePath, byte[] avatarBytes) throws IOException {
        try {
            BufferedImage userAvatar = ImageIO.read(new ByteArrayInputStream(avatarBytes));
            if (userAvatar == null) {
                throw new IOException("Invalid user avatar bytes");
            }

            try (InputStream frameStream = clazz.getResourceAsStream(framePath)) {
                if (frameStream == null) {
                    throw new FileNotFoundException("Frame resource not found: " + framePath);
                }
                BufferedImage frame = ImageIO.read(frameStream);

                int width = frame.getWidth();
                int height = frame.getHeight();
                BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = canvas.createGraphics();

                g.drawImage(frame, 0, 0, null);

                int size = Math.min(userAvatar.getWidth(), userAvatar.getHeight());
                BufferedImage roundAvatar = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D avatarGraphics = roundAvatar.createGraphics();
                avatarGraphics.setClip(new Ellipse2D.Float(0, 0, size, size));
                avatarGraphics.drawImage(userAvatar, 0, 0, size, size, null);
                avatarGraphics.dispose();

                g.drawImage(roundAvatar, 158, 190, 854, 854, null);
                g.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(canvas, "PNG", baos);
                return baos.toByteArray();
            }

        } catch (IOException e) {
            log.error("Error: ", e);
            throw new IOException("Image composition failed", e);
        }
    }

    public static byte[] imsoHappy(byte[] imageBuffer, boolean direction) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBuffer));
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, image.getType());

        int centerX = width / 2;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int mirroredX = (direction ? (x < centerX ? x : 2 * centerX - x - 1) : (x >= centerX ? x : 2 * centerX - x - 1));
                int color = image.getRGB(mirroredX, y);
                newImage.setRGB(x, y, color);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newImage, "PNG", baos);
        return baos.toByteArray();
    }

    public static byte[] spherical(byte[] imageBuffer) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBuffer));
        int width = image.getWidth();
        int height = image.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2;

        // 使用 TYPE_INT_ARGB 确保透明背景
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int dx = x - centerX;
                int dy = y - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < radius) {
                    double factor = Math.sqrt(1 - (distance / radius) * (distance / radius));
                    int srcX = (int) (centerX + dx * factor);
                    int srcY = (int) (centerY + dy * factor);
                    if (srcX >= 0 && srcX < width && srcY >= 0 && srcY < height) {
                        int color = image.getRGB(srcX, srcY);
                        newImage.setRGB(x, y, color);
                    }
                } else {
                    // 半径外保持透明
                    newImage.setRGB(x, y, 0x00000000);
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newImage, "PNG", baos);
        return baos.toByteArray();
    }

    public static byte[] fisheye(byte[] imageBuffer, double intensity) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBuffer));
        int width = image.getWidth();
        int height = image.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2;
        BufferedImage newImage = new BufferedImage(width, height, image.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int dx = x - centerX;
                int dy = y - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                double factor = Math.pow(distance / radius, intensity);
                int srcX = (int) (centerX + dx * factor);
                int srcY = (int) (centerY + dy * factor);

                if (srcX >= 0 && srcX < width && srcY >= 0 && srcY < height) {
                    int color = image.getRGB(srcX, srcY);
                    newImage.setRGB(x, y, color);
                } else {
                    int edgeX = Math.min(Math.max(srcX, 0), width - 1);
                    int edgeY = Math.min(Math.max(srcY, 0), height - 1);
                    int color = image.getRGB(edgeX, edgeY);
                    newImage.setRGB(x, y, color);
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newImage, "PNG", baos);
        return baos.toByteArray();
    }

    public static byte[] defisheye(byte[] imageBuffer, double intensity) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBuffer));
        int width = image.getWidth();
        int height = image.getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2;
        BufferedImage newImage = new BufferedImage(width, height, image.getType());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int dx = x - centerX;
                int dy = y - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= radius) {
                    double factor = Math.pow(distance / radius, 1 / intensity);
                    int srcX = (int) (centerX + dx * factor);
                    int srcY = (int) (centerY + dy * factor);

                    if (srcX >= 0 && srcX < width && srcY >= 0 && srcY < height) {
                        int color = image.getRGB(srcX, srcY);
                        newImage.setRGB(x, y, color);
                    } else {
                        int edgeX = Math.min(Math.max(srcX, 0), width - 1);
                        int edgeY = Math.min(Math.max(srcY, 0), height - 1);
                        int color = image.getRGB(edgeX, edgeY);
                        newImage.setRGB(x, y, color);
                    }
                } else {
                    int color = image.getRGB(x, y);
                    newImage.setRGB(x, y, color);
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newImage, "PNG", baos);
        return baos.toByteArray();
    }
}