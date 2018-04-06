package com.slyak.zzbid.config;

import com.google.common.collect.Sets;
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

import java.util.Set;
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

    private static final Set<String> extraInvalidUrls
            = Sets.newHashSet("http://st.zzint.com/login.jsp", "http://st.zzint.com/index.jsp", "http://st.zzint.com/login.action");


    private static final String licValidUrl = "http://www.slyak.com/license?key=zzbid";

    @Bean
    public CrawlerService<Document> crawlerService() {
        JsoupCrawlerService crawlerService = new JsoupCrawlerService() {
            @Override
            protected boolean isSessionValid(Connection.Response response) {
                return !extraInvalidUrls.contains(response.url().toString());
            }
        };
        crawlerService.setTimeoutMillis(4000);
        return crawlerService;
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
                    if (!BooleanUtils.toBoolean(StringUtils.trim(document.text()))) {
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
