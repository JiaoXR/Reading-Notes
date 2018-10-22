package com.jaxer.example.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jxr on 4:22 PM 2018/9/30
 * 生成带logo的二维码
 */
public class QRCodeUtil {
    private static final int QR_COLOR = 0xFF000000; // 默认黑色
    private static final int BG_COLOR = 0xFFFFFFFF; // 背景色
    private static final String QR_URL = "http:// m.sqaproxy.tangeche.com/activity/barter";

    private static BufferedImage LOGO_BUFFERED_IMAGE = null; //暂未用到

//    static {
//        //初始化二维码logo
//        String classpath = QRCodeUtil.class.getResource("/").getPath();
//        String imgPath = classpath.replaceAll("WEB-INF/classes/", "resource/logo.png");
//        try {
//            LOGO_BUFFERED_IMAGE = ImageIO.read(new FileInputStream(imgPath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        try {
//            getLogoQRCode(QR_URL, 156, 156);
            getQRCode(QR_URL, 156, 156);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成二维码图片（无logo）
     *
     * @param qrUrl    二维码跳转的URL
     * @param qrWidth  二维码宽度
     * @param qrHeight 二维码高度
     * @return
     */
    public static BufferedImage getQRCode(String qrUrl, int qrWidth, int qrHeight) {
        return getQRCodeBufferedImage(qrUrl, BarcodeFormat.QR_CODE, qrWidth, qrHeight, getDecodeHintType());
    }

    /**
     * 生成带logo的二维码图片（暂时不用了）
     *
     * @param qrUrl    二维码跳转的URL
     * @param qrWidth  二维码宽度
     * @param qrHeight 二维码高度
     */
    private static BufferedImage getLogoQRCode(String qrUrl, int qrWidth, int qrHeight) {
        try {
            BufferedImage qrBufferedImage = getQRCodeBufferedImage(qrUrl, BarcodeFormat.QR_CODE, qrWidth, qrHeight, getDecodeHintType());
            return addLogo2QRCode(qrBufferedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 给二维码图片添加Logo（静态资源）
     */
    private static BufferedImage addLogo2QRCode(BufferedImage bufferedImage) {
        try {
            Graphics2D g = bufferedImage.createGraphics();
            // 设置logo的大小,本人设置为二维码图片的20%,因为过大会盖掉二维码
            int widthLogo = LOGO_BUFFERED_IMAGE.getWidth(null) > bufferedImage.getWidth() * 3 / 10 ? (bufferedImage.getWidth() * 3 / 10) : LOGO_BUFFERED_IMAGE.getWidth(null);
            int heightLogo = LOGO_BUFFERED_IMAGE.getHeight(null) > bufferedImage.getHeight() * 3 / 10 ? (bufferedImage.getHeight() * 3 / 10) : LOGO_BUFFERED_IMAGE.getWidth(null);
            // logo放在中心
            int x = (bufferedImage.getWidth() - widthLogo) / 2;
            int y = (bufferedImage.getHeight() - heightLogo) / 2;
            g.drawImage(LOGO_BUFFERED_IMAGE, x, y, widthLogo, heightLogo, null);
            g.dispose();
            LOGO_BUFFERED_IMAGE.flush();
            bufferedImage.flush();
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成二维码BufferedImage图片
     *
     * @param content       编码内容
     * @param barcodeFormat 编码类型
     * @param width         图片宽度
     * @param height        图片高度
     * @param hints         设置参数
     * @return 生成的二维码BufferedImage图片
     */
    private static BufferedImage getQRCodeBufferedImage(String content, BarcodeFormat barcodeFormat, int width, int height, Map<EncodeHintType, ?> hints) {
        MultiFormatWriter multiFormatWriter;
        BitMatrix bm;
        BufferedImage image = null;
        try {
            multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            bm = multiFormatWriter.encode(content, barcodeFormat, width, height, hints);
            int w = bm.getWidth();
            int h = bm.getHeight();
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            // 开始利用二维码数据创建Bitmap图片，分别设为黑白两色
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? QR_COLOR : BG_COLOR);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 设置二维码的格式参数
     */
    private static Map<EncodeHintType, Object> getDecodeHintType() {
        Map<EncodeHintType, Object> hintTypes = new HashMap<>();
        hintTypes.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hintTypes.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hintTypes.put(EncodeHintType.MARGIN, 0);
        hintTypes.put(EncodeHintType.MAX_SIZE, 350);
        hintTypes.put(EncodeHintType.MIN_SIZE, 100);
        return hintTypes;
    }
}
