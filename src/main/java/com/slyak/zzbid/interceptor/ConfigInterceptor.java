package com.slyak.zzbid.interceptor;

import com.slyak.util.WebUtils;
import com.slyak.zzbid.model.Config;
import com.slyak.zzbid.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * .
 *
 * @author stormning 2017/12/21
 * @since 1.3.0
 */
@Component
public class ConfigInterceptor extends HandlerInterceptorAdapter {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    @Autowired
    private BidService bidService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = URL_PATH_HELPER.getRequestUri(request);
        if (!WebUtils.isAjaxRequest(request) && !requestUri.contains("config") && !requestUri.contains("error")) {
            Config config = bidService.getConfig();
            if (config == null || !config.isValid()) {
                response.sendRedirect(URL_PATH_HELPER.getContextPath(request) + "/config");
                return false;
            }
        }
        return super.preHandle(request, response, handler);
    }
}
