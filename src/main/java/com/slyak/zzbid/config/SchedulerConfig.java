package com.slyak.zzbid.config;

import com.slyak.zzbid.model.Config;
import com.slyak.zzbid.service.BidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * .
 *
 * @author stormning 2017/12/21
 * @since 1.3.0
 */
@Configuration
@Slf4j
public class SchedulerConfig implements SchedulingConfigurer {

    private BidService bidService;

    public SchedulerConfig(BidService bidService) {
        this.bidService = bidService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(new TriggerTask(() -> {
            Config config = bidService.getConfig();
            if (config.isStart() && bidService.isLogin()) bidService.autoBid();
        }, triggerContext -> {
            Config config = bidService.getConfig();
            Calendar next = new GregorianCalendar();
            Date last = triggerContext.lastActualExecutionTime();
            next.setTime(last == null ? new Date() : last);
            next.add(Calendar.SECOND, config.getInterval());
            log.info("Next task start will start at : {}", next);
            return next.getTime();
        }));
    }
}
