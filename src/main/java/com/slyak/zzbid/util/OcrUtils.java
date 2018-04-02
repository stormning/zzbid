package com.slyak.zzbid.util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang.StringUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * .
 *
 * @author stormning 2018/4/2
 * @since 1.3.0
 */
public class OcrUtils {

    private OcrUtils() {
    }

    private static final String LANG = "zzbid";

    public static String doOcr(byte[] input) throws IOException, TesseractException {
        return doOcr(ImageCleaner.cleanImage(input));
    }

    public static String doOcr(InputStream is) throws IOException, TesseractException {
        return doOcr(ImageCleaner.cleanImage(is));
    }

    public static String doOcr(BufferedImage image) throws IOException, TesseractException {
        Tesseract instance = new Tesseract();
        instance.setLanguage(LANG);
        return StringUtils.deleteWhitespace(instance.doOCR(image));
    }
}
