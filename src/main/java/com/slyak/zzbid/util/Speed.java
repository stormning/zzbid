package com.slyak.zzbid.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;

/**
 * .
 *
 * @author stormning 2018/4/3
 * @since 1.3.0
 */
@Slf4j
public class Speed {

    private long start;
    private String key;

    private Speed(String key) {
        this.start = System.currentTimeMillis();
        this.key = key == null ? RandomStringUtils.random(5) : key;
        log.info("SpeedTest-{}, start at {}", key, start);
    }

    public static Speed init(String key) {
        return new Speed(key);
    }

    public void spent() {
        long now = System.currentTimeMillis();
        log.info("SpeedTest-{}, stop at {} , spent {}", key, now, now - start);
        this.start = now;
    }
}