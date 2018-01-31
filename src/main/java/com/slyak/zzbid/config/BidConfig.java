package com.slyak.zzbid.config;

import com.slyak.support.crawler.CrawlerService;
import com.slyak.support.crawler.impl.JsoupCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * .
 *
 * @author stormning 2017/12/25
 * @since 1.3.0
 */
@Configuration
@Slf4j
public class BidConfig {

    private static final String extraInvalidUrl = "http://st.zzint.com/index.jsp";

    private static final String licValidUrl = "http://www.slyak.com/license?key=zzbid";

    @Bean
    public CrawlerService<Document> crawlerService() {
        return new JsoupCrawlerService() {
            @Override
            protected boolean isSessionValid(String initUrl, Connection.Response response) {
                return super.isSessionValid(initUrl, response) && !StringUtils.equals(extraInvalidUrl, response.url().toString());
            }
        };
    }


    @Bean
    public VersionDetector versionDetector() {
        return new VersionDetector(crawlerService());
    }

    public class VersionDetector implements ApplicationRunner {

        private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        private CrawlerService<Document> crawlerService;

        public VersionDetector(CrawlerService<Document> crawlerService) {
            this.crawlerService = crawlerService;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
            executorService.scheduleWithFixedDelay(() -> {
                try {
                    Document document = crawlerService.fetchDocument(licValidUrl, HttpMethod.GET, null, null);
                    if (!BooleanUtils.toBoolean(StringUtils.trim(document.text()))){
                        log.error("Invalid license, please contact seller");
                        System.exit(0);
                    }
                } catch (Exception e) {
                    //donoting
                }
            }, 10, 10, TimeUnit.SECONDS);
        }
    }
}
