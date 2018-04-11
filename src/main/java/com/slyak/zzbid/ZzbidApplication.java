package com.slyak.zzbid;

import com.slyak.zzbid.util.OcrUtils;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

@SpringBootApplication
@ComponentScan("com.slyak")
@EnableScheduling
@EnableAsync
@Slf4j
@EnableCaching
public class ZzbidApplication {

    private static final ResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

    public static void main(String[] args) throws IOException, TesseractException {
        SpringApplication.run(ZzbidApplication.class, args);
        try {
            Resource test = RESOURCE_LOADER.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "test.jpeg");
            String vcode = OcrUtils.doOcr(test.getInputStream());
            log.info("Ocr test : {}", vcode);
        } catch (Exception e) {
            log.error("Ocr test failed!!!", e);
        }
    }
}
