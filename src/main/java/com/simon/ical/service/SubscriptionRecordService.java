package com.simon.ical.service;

import com.simon.ical.dao.entities.SubscriptionRecord;
import com.simon.ical.dao.repositories.SubscriptionRecordRepository;
import com.simon.ical.utils.TimeUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Slf4j
@Service
public class SubscriptionRecordService {

    @Autowired
    private SubscriptionRecordRepository recordRepository;

    @Transactional
    public void record(String ip) {
        if (StringUtils.isBlank(ip)) {
            log.info("skipped operation, because the ip is empty.");
            return;
        }
        SubscriptionRecord existed = recordRepository.findByIp(ip);
        if (existed != null) {
            existed.setModifiedDate(TimeUtils.now());
            existed.setTimes(existed.getTimes() + 1);
            recordRepository.save(existed);
        } else {
            int now = TimeUtils.now();
            SubscriptionRecord record = new SubscriptionRecord();
            record.setIp(ip);
            record.setTimes(1);
            record.setCreatedDate(now);
            record.setModifiedDate(now);
            recordRepository.save(record);
        }
    }

    public Integer count() {
        return recordRepository.countAll();
    }
}
