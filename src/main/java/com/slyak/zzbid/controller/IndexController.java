package com.slyak.zzbid.controller;

import com.slyak.zzbid.model.Config;
import com.slyak.zzbid.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * .
 *
 * @author stormning 2017/12/18
 * @since 1.3.0
 */
@Controller
public class IndexController {

    @Autowired
    private BidService bidService;

    @ModelAttribute("config")
    public Config getConfig() {
        return bidService.getConfig();
    }

    @RequestMapping("/")
    public String index(@PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, ModelMap modelMap) {
        modelMap.put("page", bidService.findAll(pageable));
        return "index";
    }

    @GetMapping("/captcha/{sessionId}")
    @ResponseBody
    public byte[] captcha(@PathVariable String sessionId) {
        return bidService.getCaptcha(sessionId);
    }

    @PostMapping("/startBid")
    public String startBid(String sessionId, String captcha) {
        bidService.login(sessionId, captcha);
        return "redirect:/";
    }

    @RequestMapping("/nextSessionId")
    @ResponseBody
    public String nextSessionId() {
        return bidService.nextSessionId();
    }

    @GetMapping("/config")
    public void config() {

    }

    @PostMapping("/config")
    public String doConfig(Config config) {
        bidService.saveConfig(config);
        return "redirect:config";
    }

    @GetMapping("/logout")
    public void logout() {
        bidService.logout();
    }
}
