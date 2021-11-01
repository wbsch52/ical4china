package com.simon.ical.task;

import com.google.common.base.Throwables;

import com.simon.ical.service.ICalendarService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Slf4j
@Component
public class IcsFileRefreshTask implements InitializingBean {

    @Autowired
    private ICalendarService iCalendarService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void startup() {
        try {
            log.info("begin to generate ics file.");
            iCalendarService.generateIcsFile();
            log.info("ics file has been refreshed.");
        } catch (IOException e) {
            log.error("caught an error while refreshing ics file.", e);
            throw new RuntimeException(Throwables.getStackTraceAsString(e), e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        startup();
    }
}
