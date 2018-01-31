package com.slyak.zzbid.util;

import com.slyak.support.crawler.impl.JsoupCrawlerService;
import org.jsoup.Jsoup;

/**
 * .
 *
 * @author stormning 2017/12/18
 * @since 1.3.0
 */
public class CrawlerUtil {

    public void a() {
        Jsoup.connect("http://st.zzint.com/login.jsp").response().body();
    }


    public static void main(String[] args) throws Exception {

        /*Connection.Response res1 =  Jsoup.connect("https://academics.vit.ac.in/parent/captcha.asp")
                .cookie("ASPSESSIONIDSQHDAQRQ", sessionid)
                .ignoreContentType(true)
                .method(Connection.Method.GET).timeoutMillis(30000).execute();*/
        JsoupCrawlerService crawlerService = new JsoupCrawlerService();
        String sessionId = crawlerService.initSession("http://st.zzint.com/login.jsp");

        crawlerService.getCaptcha(sessionId, "http://st.zzint.com/vcode.action");
    }

}
