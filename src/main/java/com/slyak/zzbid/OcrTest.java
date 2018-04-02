package com.slyak.zzbid;

import com.google.common.collect.Sets;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.im4java.core.IM4JavaException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * .
 *
 * @author stormning 2018/3/28
 * @since 1.3.0
 */
public class OcrTest {

    //replace / ,  blank
    //must be [65,90] or [0,9]
    //and not in unsafe
    Set<String> unsafe = Sets.newHashSet("Q", "V", "Y", "6", "G");

    public static void main(String[] args) throws TesseractException, IOException, IM4JavaException, InterruptedException {
        /*Tesseract instance = new Tesseract();
        instance.setLanguage("zzbid");
        for (int i = 0; i <= 10; i++) {
            *//*Connection.Response response = Jsoup.connect("http://st.zzint.com/vcode.action")
                    .ignoreContentType(true)
                    .execute();*//*
            File file = new File("/Users/stormning/Downloads/vcodes/" + i + ".jpeg");
//            IOUtils.copy(response.bodyStream(), new FileOutputStream(file));
            BufferedImage img = ImageIO.read(file);
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
            File gray = new File("/Users/stormning/Downloads/vcodes/" + i + "-gray.jpeg");
            ImageIO.write(img, "jpeg", gray);
            System.out.println(i + "-" + instance.doOCR(gray));
        }*/
    }
}
