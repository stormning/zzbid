package com.slyak.zzbid.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * .
 *
 * @author stormning 2018/4/2
 * @since 1.3.0
 */
public class ImageCleaner {

    public static BufferedImage cleanImage(byte[] bytes) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
        int width = img.getWidth();
        int height = img.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = new Color(img.getRGB(x, y), img.isAlphaPremultiplied());
                int rgbs = color.getRed() * color.getGreen() * color.getBlue();
                if (rgbs > 11000000 || rgbs < 8000) {
                    img.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    img.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }
}
