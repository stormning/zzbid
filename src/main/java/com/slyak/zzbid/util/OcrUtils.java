package com.slyak.zzbid.util;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OcrUtils {

    private OcrUtils() {
    }

    private static final String LANG = "zzbid";

    public static String doOcr(byte[] input) throws IOException, TesseractException {
        return doOcr(ImageCleaner.cleanImage(input));
    }

    public static String doOcr(InputStream is) throws IOException, TesseractException {
        try {
            return doOcr(ImageCleaner.cleanImage(is));
        } finally {
            is.close();
        }
    }

    public static String doOcr(BufferedImage image) throws IOException, TesseractException {
        if (image == null) {
            return null;
        }
        Tesseract instance = new Tesseract();
        instance.setLanguage(LANG);
        String result = StringUtils.deleteWhitespace(instance.doOCR(image));
        log.info("Ocr result is : {}", result);
        return result;
    }
}
