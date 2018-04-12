package com.slyak.zzbid.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import com.slyak.concurrent.ExecutorUtils;
import com.slyak.web.support.crawler.CrawlerService;
import com.slyak.zzbid.model.Bid;
import com.slyak.zzbid.model.Config;
import com.slyak.zzbid.repository.BidRepository;
import com.slyak.zzbid.repository.ConfigRepository;
import com.slyak.zzbid.util.Constants;
import com.slyak.zzbid.util.OcrUtils;
import com.slyak.zzbid.util.Speed;
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
import org.springframework.util.CollectionUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    private int maxBidSessionCount = 10;

    //    private static final String initUrl = "http://st.zzint.com/login.jsp";
    private static final String initUrl = "http://st.zzint.com/vcode.action";


    private static final String loginActionUrl = "http://st.zzint.com/login.action";

    private static final String bidListUrl = "http://st.zzint.com/pur!queryBidList.action";

    private static final String captchaUrl = "http://st.zzint.com/vcode.action";

    private static final String addBidUrl = "http://st.zzint.com/pur!addBid.action";

    private static final String saveBidUrl = "http://st.zzint.com/pur!saveBid.action";

    private static final String logoutUrl = "http://st.zzint.com/loginOut!exit.action";

    private static final String snapshotUrl = "http://st.zzint.com/pur!queryBid.action";

    //bidId->sessionId
    private static final Map<String, String> BID_SESSION_CACHE = Maps.newConcurrentMap();

    private Cache<String, Document> documentCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).build();

    private static final String DOC_KEY = "doc";

    public void autoBid() {
        initSessions();
        //async
        initDocument();
        startBid();
    }

    private void startBid() {
        List<Bid> unbidBids = bidRepository.findUnbidBids();
        if (!CollectionUtils.isEmpty(unbidBids)) {
            for (Bid unbidBid : unbidBids) {
                bidOne(assignSessionId(unbidBid.getId()), unbidBid);
            }
        }
    }

    @Async
    public void initDocument() {
        List<String> sessionIds = crawlerService.getUrlLoginSessions(initUrl);
        if (CollectionUtils.isEmpty(sessionIds)) {
            return;
        }
        Document document = documentCache.getIfPresent(DOC_KEY);
        if (document == null) {
            log.info("sessionIds to crawl {}", sessionIds);
            document = crawlerService.fetchDocument(sessionIds, bidListUrl, HttpMethod.GET, null, null);
            if (document != null && document.toString().contains("正在投标")) {
                document.body().select("tr:gt(1)").forEach(element -> {
                    String html = element.html();
                    if (html.indexOf("正在投标") > 0) {
                        //cache the document
                        //parse again to avoid id mixed with html
                        String id = Jsoup.parse(element.child(0).text()).text();
                        Bid bid = bidRepository.findOne(id);
                        if (bid == null) {
                            String bidInfoUrl = element.select("td:last-child a:last-child").get(0).absUrl("href");
                            log.info("Url for bid : {}", bidInfoUrl);
                            bid = new Bid();
                            bid.setId(id);
                            String realPkgId = bidInfoUrl.substring(bidInfoUrl.indexOf("=") + 1);
                            bid.setRealPkgId(realPkgId);
                            bid.setFirstType(text(element, "td:eq(1)"));
                            bid.setDept(element.child(2).text());
                            bid.setStartTime(element.child(3).text());
                            bid.setEndTime(element.child(4).text());
                            bid.setBudget(element.child(5).text());
                            bid.setTaskTime(System.currentTimeMillis());
                            bidRepository.save(bid);
                        }
                    }
                });
                documentCache.put(DOC_KEY, document);
            }
        }
    }

    private String assignSessionId(String bidId) {
        return BID_SESSION_CACHE.computeIfAbsent(bidId, bid -> findUnusedSession());
    }

    @SneakyThrows
    public String findUnusedSession() {
        List<String> sessions = crawlerService.getUrlLoginSessions(initUrl);
        for (String session : sessions) {
            if (!BID_SESSION_CACHE.containsValue(session)) {
                return session;
            }
        }
        Thread.sleep(50);
        return findUnusedSession();
    }

    public void initSessions() {
        List<String> sessions = crawlerService.getUrlLoginSessions(initUrl);
        //确保有一个session
        if (CollectionUtils.isEmpty(sessions)) {
            synchronized (this) {
                if (CollectionUtils.isEmpty(sessions)) {
                    Boolean result = ExecutorUtils.startCompetition((index) -> {
                        login(nextSessionId());
                        return true;
                    }, maxBidSessionCount, 5000);
                    log.info("init sessions result : {}", result);
                }
            }
        }
    }

    @Async
    public void bidOne(String sessionId, Bid bid) {
        log.info("bid {} with sessionId {}", bid.getId(), sessionId);
        Speed bidOne = Speed.init("bidOne");
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
            BID_SESSION_CACHE.remove(bid.getId());
            updateSnapshot(bid);

        } catch (Exception e) {
            log.error("Exception occurred:", e);
            BID_SESSION_CACHE.remove(bid.getId());
        }
        bidOne.spent();
    }

    @Async
    public void updateSnapshot(Bid bid) {
        String snapshot = getSnapshotByBid(bid);
        bid.setSnapshot(snapshot);
        bidRepository.save(bid);
    }

    @SneakyThrows
    public String getSnapshotByBid(Bid bid) {
        List<String> sessions = crawlerService.getUrlLoginSessions(initUrl);
        if (CollectionUtils.isEmpty(sessions)) {
            Thread.sleep(100);
            return getSnapshotByBid(bid);
        }
        String sessionId = sessions.get(sessions.size() - 1);
        Map<String, String> data = Maps.newHashMap();
        data.put("packageId", bid.getRealPkgId());
        Document document = crawlerService.fetchDocument(sessionId, snapshotUrl, HttpMethod.GET, null, data);
        return document.select("table").get(1).toString();
    }

    public String getSnaphostById(String id) {
        return getSnapshotByBid(bidRepository.findOne(id));
    }

    private void doBidUntilSuccess(String sessionId, Map<String, String> bidData) throws IOException, TesseractException {
        bidData.put("returncodetijiao", getCaptcha(sessionId));
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

    public Config saveConfig(Config config) {
        return configRepository.save(config);
    }

    public String getCaptcha(String sessionId) {
        BufferedInputStream is = crawlerService.getCaptcha(sessionId, captchaUrl);
        try {
            String code = OcrUtils.doOcr(is);
            if (isCodeValid(code)) {
                return code;
            } else {
                return getCaptcha(sessionId);
            }
        } catch (Exception e) {
            return getCaptcha(sessionId);
        }
    }

    private boolean isCodeValid(String code) {
        if (code != null && code.length() == 4) {
            for (int i = 0; i < code.length(); i++) {
                char c = code.charAt(i);
                if (!((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public void login(String sessionId) {
        Config config = getConfig();
        Map<String, String> data = Maps.newHashMap();
        data.put("userName", config.getName());
        data.put("password", config.getPassword());
        data.put("vcode", getCaptcha(sessionId));
        boolean login = crawlerService.login(sessionId, loginActionUrl, data);
        if (!login) {
            log.info("try to re login , session id {}", sessionId);
            login(nextSessionId());
        }
    }

    public String nextSessionId() {
        return crawlerService.initSession(initUrl);
    }

    public void logout() {
        List<String> sessionIds = crawlerService.getUrlLoginSessions(initUrl);
        if (sessionIds.size() > 0) {
            for (String sessionId : sessionIds) {
                crawlerService.fetchDocument(sessionId, logoutUrl, HttpMethod.GET, null, null);
            }
        }
    }

    public void clear() {
        bidRepository.deleteAllInBatch();
    }
}