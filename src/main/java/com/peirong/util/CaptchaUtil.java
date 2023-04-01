package com.peirong.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
public class CaptchaUtil {
    private int width = 300;
    private int height = 40;
    private int codeCount = 4;
    private int lineCount = 100;
    private String code = null;
    private BufferedImage buffImg = null;
    private char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    public CaptchaUtil() {
        this.createCode();
    }

    public CaptchaUtil(int width, int height) {
        this.width = width;
        this.height = height;
        this.createCode();
    }

    public CaptchaUtil(int width, int height, int codeCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        this.createCode();
    }

    public void createCode() {
        int x = 0, fontHeight = 0, codeY = 0;
        int red = 0, green = 0, blue = 0;

        x = width / (codeCount + 1);// 每个字符的宽度(左右各空出一个字符)
        fontHeight = height - 2;// 字体的高度
        codeY = height - 4;

        // 图像buffer
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        // 生成随机数
        Random random = new Random();

        // 将图像填充为输入框底色
        Color color = new Color(245,247,250);
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        // 创建字体
        Font font = new Font("Times New Roman", Font.ROMAN_BASELINE, fontHeight);
        g.setFont(font);

        for (int i = 0; i < lineCount; i++) {
            // 设置随机开始和结束坐标
            int xs = random.nextInt(width);// x坐标开始
            int ys = random.nextInt(height);// y坐标开始
            int xe = xs + random.nextInt(width / 8);// x坐标结束
            int ye = ys + random.nextInt(height / 8);// y坐标结束

            // 产生随机的颜色值，让输出的每个干扰线的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xs, ys, xe, ye);
        }

        // randomCode记录随机产生的验证码
        StringBuffer randomCode = new StringBuffer();
        // 随机产生codeCount个字符的验证码。
        for (int i = 0; i < codeCount; i++) {
            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
            red = random.nextInt(128);
            green = random.nextInt(128);
            blue = random.nextInt(128);

            g.setColor(new Color(red, green, blue));
            g.drawString(strRand, (int) ((i + 0.5) * x), codeY+1);
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        // 将四位数字的验证码保存到Session中。
        code = randomCode.toString();
    }

    /**
     * @方法名：write
     * @方法描述:将验证码图片写入指定路径
     */
    public void write(String path) throws IOException {
        OutputStream sos = new FileOutputStream(path);
        this.write(sos);
    }

    /**
     * @方法名：write
     * @方法描述:将验证码图片写入指定输出流
     */
    public void write(OutputStream sos) throws IOException {
        ImageIO.write(buffImg, "png", sos);
        sos.close();
    }

    /**
     * @方法名：getBuffImg
     * @方法描述:获取验证码图片
     */
    public BufferedImage getBuffImg() {
        return buffImg;
    }

    /**
     *
     * @方法名：getCode
     * @方法描述:获取验证码
     */
    public String getCode() {
        return code;
    }

}