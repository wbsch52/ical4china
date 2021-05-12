package com.simon.ical.interceptor;

import com.simon.ical.commons.IpHolder;
import com.simon.ical.service.SubscriptionRecordService;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Aspect
@Component
public class UvInterceptor {

    @Autowired
    private SubscriptionRecordService subscriptionRecordService;

    @After("execution(* com.simon.ical.controller.ICalendarController.generate(..))")
    public void record() {
        String ip = IpHolder.get();
        subscriptionRecordService.record(ip);
    }
}
