package com.slyak.zzbid.service;

import com.google.common.collect.Maps;
import com.slyak.support.crawler.CrawlerService;
import com.slyak.zzbid.model.Bid;
import com.slyak.zzbid.model.Config;
import com.slyak.zzbid.repository.BidRepository;
import com.slyak.zzbid.repository.ConfigRepository;
import com.slyak.zzbid.util.Constants;
import com.slyak.zzbid.util.OcrUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/12/18
 * @since 1.3.0
 */
@Component
@Slf4j
public class BidService {

    @Autowired
    private CrawlerService<Document> crawlerService;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ConfigRepository configRepository;

    private static final String initUrl = "http://st.zzint.com/login.jsp";

    private static final String loginActionUrl = "http://st.zzint.com/login.action";

    private static final String bidListUrl = "http://st.zzint.com/pur!queryBidList.action";

    private static final String captchaUrl = "http://st.zzint.com/vcode.action";

    private static final String addBidUrl = "http://st.zzint.com/pur!addBid.action";

    private static final String saveBidUrl = "http://st.zzint.com/pur!saveBid.action";

    private static final String logoutUrl = "http://st.zzint.com/loginOut!exit.action";

    public void autoBid() {
        loginCheck();
        //http://st.zzint.com/pur!queryBidList.action
        ///pur!showBid.action?packageId=2378
        ///pur!addBid.action
        List<String> sessionIds = crawlerService.getInitUrlSessions(initUrl);
        if (sessionIds.size() == 0) return;
        String sessionId = sessionIds.get(0);
        Document document = crawlerService.fetchDocument(sessionId, bidListUrl, HttpMethod.GET, null, null);
        document.body().select("tr:gt(1)").forEach(element -> {
            String html = element.html();
            if (html.indexOf("正在投标") > 0) {
                //parse again to avoid id mixed with html
                String id = Jsoup.parse(element.child(0).text()).text();
                Bid exist = bidRepository.findOne(id);
                if (exist == null) {
                    String bidInfoUrl = element.select("td:last-child a:last-child").get(0).absUrl("href");
                    log.info("Url for bid : {}", bidInfoUrl);
                    Bid bid = new Bid();
                    bid.setId(id);
                    String realPkgId = bidInfoUrl.substring(bidInfoUrl.indexOf("=") + 1);
                    bid.setRealPkgId(realPkgId);
                    bid.setFirstType(text(element, "td:eq(1)"));
                    bid.setDept(element.child(2).text());
                    bid.setStartTime(element.child(3).text());
                    bid.setEndTime(element.child(4).text());
                    bid.setBudget(element.child(5).text());
                    bidRepository.save(bid);
                    doBid(sessionId, bid);
                } else if (exist.getBidTime() <= 0) {
                    doBid(sessionId, exist);
                }
            }
        });
    }

    @SneakyThrows
    private void loginCheck() {
        if (!isLogin()) {
            String sessionId = nextSessionId();
            byte[] captcha = getCaptcha(sessionId);
            login(sessionId, OcrUtils.doOcr(captcha));
        }
    }

    @Async
    public void doBid(String sessionId, Bid bid) {
        try {
            ///http://st.zzint.com/pur!addBid.action?packageId
            Config config = getConfig();
            Map<String, String> data = Maps.newHashMap();
            data.put("packageId", bid.getRealPkgId());
            Document document = crawlerService.fetchDocument(sessionId, addBidUrl, HttpMethod.POST, null, data);
            Element bidTable = document.select("tr:eq(1)").first().select("table").first();

            Map<String, String> bidData = Maps.newHashMap();
            //itemIds, bidPrice,returncodetijiao,packageId
            bidData.put("itemIds", bidTable.selectFirst("#itemIds").val());
            bidData.put("bidPrice", config.getMoney().toString());
            bidData.put("packageId", bid.getRealPkgId());

            doBidUntilSuccess(sessionId, bidData);
            bid.setBidTime(System.currentTimeMillis());
            bidRepository.save(bid);
        } catch (Exception e) {
            log.error("Exception occurred:", e);
        }
    }

    private void doBidUntilSuccess(String sessionId, Map<String, String> bidData) throws IOException, TesseractException {
        bidData.put("returncodetijiao", OcrUtils.doOcr(getCaptcha(sessionId)));
        Document document = crawlerService.fetchDocument(sessionId, saveBidUrl, HttpMethod.POST, null, bidData);
        if (document.toString().contains("验证码错误")) {
            doBidUntilSuccess(sessionId, bidData);
        }
    }


    private String text(Element element, String selector) {
        return StringUtils.trim(element.select(selector).text());
    }

    public Page<Bid> findAll(Pageable pageable) {
        return bidRepository.findAll(pageable);
    }

    public Config getConfig() {
        Config config = configRepository.findOne(Constants.CONFIG_ID);
        return config == null ? new Config() : config;
    }

    public boolean isLogin() {
        return crawlerService.isLogin(initUrl);
    }

    public Config saveConfig(Config config) {
        return configRepository.save(config);
    }

    public byte[] getCaptcha(String sessionId) {
        return crawlerService.getCaptcha(sessionId, captchaUrl);
    }

    public void login(String sessionId, String captcha) {
        Config config = getConfig();
        Map<String, String> data = Maps.newHashMap();
        data.put("userName", config.getName());
        data.put("password", config.getPassword());
        data.put("vcode", captcha);
        crawlerService.login(sessionId, loginActionUrl, data);
    }

    public String nextSessionId() {
        if (crawlerService.isLogin(initUrl)) {
            return "";
        }
        return crawlerService.initSession(initUrl);
    }

    public void logout() {
        List<String> sessionIds = crawlerService.getInitUrlSessions(initUrl);
        if (sessionIds.size() > 0) {
            for (String sessionId : sessionIds) {
                crawlerService.fetchDocument(sessionId, logoutUrl, HttpMethod.GET, null, null);
            }
        }
    }
}