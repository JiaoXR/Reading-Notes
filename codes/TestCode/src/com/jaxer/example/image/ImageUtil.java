package com.jaxer.example.image;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by jxr on 1:59 PM 2018/10/14
 * 图片合并&图片上添加文字
 */
public class ImageUtil {

    private static final String saveFilePath = "~/Desktop/pics/poster1.png";
    private static final int IMG_WIDTH = 675; //用户上传图片默认宽度
    private static final int IMG_HEIGHT = 500; //用户上传图片默认高度

    public static void main(String[] args) throws IOException {
        String sourceFilePath = "http://image-demo.img-cn-hangzhou.aliyuncs.com/example.jpg";
        String waterFilePath = "http://image-demo.img-cn-hangzhou.aliyuncs.com/panda.png";
        String appendUrl = "http://image-demo.img-cn-hangzhou.aliyuncs.com/example.jpg";

        BufferedImage bufferedImage = watermark(sourceFilePath, waterFilePath, 100, 100, 0.8f);
        appendImage(bufferedImage, appendUrl, 0, 300, 1.0f);
        appendText(bufferedImage, "测试文字", new Font("微软雅黑", Font.BOLD, 30), Color.RED, 100, 500);
        generateWaterFile(bufferedImage, saveFilePath);
    }

    /**
     * 输出水印图片
     *
     * @param buffImg  图像加水印之后的BufferedImage对象
     * @param savePath 图像加水印之后的保存路径
     */
    public static void generateWaterFile(BufferedImage buffImg, String savePath) {
        int temp = savePath.lastIndexOf(".") + 1;
        try {
            ImageIO.write(buffImg, savePath.substring(temp), new File(savePath));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * BufferedImage 添加水印图
     *
     * @param originUrl 原始图片链接
     * @param waterUrl  水印图片链接
     * @param x         x 坐标
     * @param y         y 坐标
     * @param alpha     透明度
     * @return 合成后的 BufferedImage
     * @throws IOException
     */
    public static BufferedImage watermark(String originUrl, String waterUrl, int x, int y, float alpha) throws IOException {
        BufferedImage originImg = ImageIO.read(new URL(originUrl));
        BufferedImage waterImg = ImageIO.read(new URL(waterUrl));
        Graphics2D g2d = originImg.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        g2d.drawImage(waterImg, x, y, waterImg.getWidth(), waterImg.getHeight(), null);
        g2d.dispose();
        return originImg;
    }

    /**
     * BufferedImage 添加图片
     *
     * @param originImg    原始图片
     * @param appendImgUrl 添加的图片的URL
     * @param x            x 坐标
     * @param y            y 坐标
     * @param alpha        透明度
     * @return 叠加后的图片
     */
    public static BufferedImage appendImage(BufferedImage originImg, String appendImgUrl, int x, int y, float alpha) throws IOException {
        BufferedImage waterImg = ImageIO.read(new URL(appendImgUrl));
        Graphics2D g2d = originImg.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        g2d.drawImage(waterImg, x, y, waterImg.getWidth(), waterImg.getHeight(), null);
        g2d.dispose();
        return originImg;
    }

    /**
     * BufferedImage 添加图片
     *
     * @param originImg   原始图片
     * @param appendImage 添加的图片
     * @param x           x 坐标
     * @param y           y 坐标
     * @param alpha       透明度
     * @return 叠加后的图片
     */
    public static BufferedImage appendImage(BufferedImage originImg, BufferedImage appendImage, int x, int y, float alpha) {
        Graphics2D g2d = originImg.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        g2d.drawImage(appendImage, x, y, appendImage.getWidth(), appendImage.getHeight(), null);
        g2d.dispose();
        return originImg;
    }

    /**
     * BufferedImage 添加图片
     *
     * @param originImg 原始图片
     * @param bytes     添加图片的字节数组
     * @param x         x 坐标
     * @param y         y 坐标
     * @param alpha     透明度
     * @return 叠加后的图片
     */
    public static BufferedImage appendImage(BufferedImage originImg, byte[] bytes, int x, int y, float alpha) throws IOException {
        Graphics2D g2d = originImg.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        BufferedImage appendImage = ImageIO.read(inputStream);
        g2d.drawImage(appendImage, x, y, IMG_WIDTH, IMG_HEIGHT, null);
        g2d.dispose();
        return originImg;
    }

    /**
     * BufferedImage 添加文字
     *
     * @param originImage 原始图片
     * @param text        添加的文字
     * @param font        字体
     * @param color       颜色
     * @param x           x 坐标
     * @param y           y 坐标
     * @return 修改后的 BufferedImage
     */
    public static BufferedImage appendText(BufferedImage originImage, String text, Font font, Color color, int x, int y) {
        Graphics2D g2d = originImage.createGraphics();
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(text, x, y);
        g2d.dispose();
        return originImage;
    }

    public static void myDrawString(BufferedImage originImage, String str, Font font, Color color, int x, int y, boolean isBig) {
        String tempStr;
        int tempX = x;
        int fontSpace = 64;
        if (isBig) {
            fontSpace = 30;
        }
        while (str.length() > 0) {
            tempStr = str.substring(0, 1);
            str = str.substring(1, str.length());
            originImage = appendText(originImage, tempStr, font, color, tempX, y);
            if ("“".equals(tempStr)) {
                tempX = tempX + 15;
            } else {
                if ("i".equals(tempStr) || "I".equals(tempStr) || "1".equals(tempStr)) {
                    tempX = tempX + 10;
                } else {
                    tempX = tempX + fontSpace;
                }

            }
        }
    }

    /**
     * 在 BufferedImage 上添加文本（推荐：间距更合理，上面的重载方法间距固定，英文字母看起来有间隔）
     *
     * @param originImage BufferedImage
     * @param str         要添加的文本
     * @param font        指定字体
     * @param color       字体颜色
     * @param x           横坐标
     * @param y           纵坐标
     */
    public static void myDrawString(BufferedImage originImage, String str, Font font, Color color, int x, int y) {
        int tempX = x;
        FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            originImage = appendText(originImage, String.valueOf(c), font, color, tempX, y);
            tempX = tempX + fm.charWidth(c);
        }
    }

    /**
     * 对字节数组字符串进行Base64解码，并生成字节数组
     *
     * @param imgStr Base64格式字符串
     */
    public static byte[] convert2Bytes(String imgStr) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(imgStr);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {
                bytes[i] += 256;
            }
        }
        return bytes;
    }

    /**
     * 获取裁剪后的 BufferedImage
     *
     * @param imgStr Base64格式字符串
     */
    public static BufferedImage convert2CroppedImg(String imgStr) throws IOException {
        //若传的图片以data开头，将其去除
//        imgStr = StringUtil.convertBase64Str(imgStr);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(imgStr);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {
                bytes[i] += 256;
            }
        }
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BufferedImage croppedImage = ImageIO.read(in);
        if (croppedImage == null) {
            return null;
        }
        int width = croppedImage.getWidth();
        int height = croppedImage.getHeight();
        //注意这里比较的宽高比
        if (width > IMG_WIDTH && height > IMG_HEIGHT) { //大图等比例缩放
            if ((width / height) > (IMG_WIDTH / IMG_HEIGHT)) { //横图
                Image scaledImage = croppedImage.getScaledInstance(width * IMG_HEIGHT / height, IMG_HEIGHT, BufferedImage.SCALE_SMOOTH);
                BufferedImage scaledBufferedImage = convertToBufferedImage(scaledImage);
                croppedImage = scaledBufferedImage.getSubimage((scaledBufferedImage.getWidth() - IMG_WIDTH) / 2, 0, IMG_WIDTH, IMG_HEIGHT);
            } else { //竖图或正方形
                Image scaledImage = croppedImage.getScaledInstance(IMG_WIDTH, height * IMG_WIDTH / width, BufferedImage.SCALE_SMOOTH);
                BufferedImage scaledBufferedImage = convertToBufferedImage(scaledImage);
                croppedImage = scaledBufferedImage.getSubimage(0, (scaledBufferedImage.getHeight() - IMG_HEIGHT) / 2, IMG_WIDTH, IMG_HEIGHT);
            }
        } else if (width > IMG_WIDTH && height < IMG_HEIGHT) { //宽度超过，高度不足
            Image image = croppedImage.getScaledInstance(width * IMG_HEIGHT / height, IMG_HEIGHT, BufferedImage.SCALE_SMOOTH);
            croppedImage = convertToBufferedImage(image);
        } else if (width < IMG_WIDTH && height > IMG_HEIGHT) { //宽度不足，高度超过
            Image image = croppedImage.getScaledInstance(IMG_WIDTH, height * IMG_WIDTH / width, BufferedImage.SCALE_SMOOTH);
            croppedImage = convertToBufferedImage(image);
        }
        //缩放后裁剪
        if (croppedImage.getWidth() > IMG_WIDTH || croppedImage.getHeight() > IMG_HEIGHT) {
            croppedImage = croppedImage.getSubimage((croppedImage.getWidth() - IMG_WIDTH) / 2, (croppedImage.getHeight() - IMG_HEIGHT) / 2,
                    IMG_WIDTH, IMG_HEIGHT);
        }
        return croppedImage;
    }

    /**
     * Image 转为 BufferedImage
     */
    private static BufferedImage convertToBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();
        return bufferedImage;
    }
}
