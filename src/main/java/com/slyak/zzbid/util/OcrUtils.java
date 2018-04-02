package com.slyak.zzbid.util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

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
        Tesseract instance = new Tesseract();
        instance.setLanguage(LANG);
        return StringUtils.deleteWhitespace(instance.doOCR(ImageCleaner.cleanImage(input)));
    }
}
