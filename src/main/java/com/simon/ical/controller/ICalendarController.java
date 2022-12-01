package com.simon.ical.controller;

import com.simon.ical.service.ICalendarService;
import com.simon.ical.service.SubscriptionRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@RestController
@CrossOrigin
public class ICalendarController {

    @Autowired
    private ICalendarService iCalendarService;

    @Autowired
    private SubscriptionRecordService subscriptionRecordService;

    @GetMapping(value = {"/ical", "/cal.ics"}, produces = "text/calendar; charset=UTF-8")
    public ResponseEntity<FileSystemResource> generate() throws IOException {
        File file = iCalendarService.getIcsFile();
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileSystemResource);
    }

    @GetMapping(value = "/ical/subscription/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Integer>> getSubCount() {
        Integer count = subscriptionRecordService.count();
        Map<String, Integer> body = Collections.singletonMap("count", count);
        return ResponseEntity.ok(body);
    }
}
