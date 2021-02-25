package com.simon.ical.controller;

import com.simon.ical.service.ICalendarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@RestController
public class ICalendarController {

    @Autowired
    private ICalendarService iCalendarService;

    @GetMapping(value = "/ical", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileSystemResource> generate() throws IOException {
        File file = iCalendarService.getOrGenerateIcsContent();
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileSystemResource);
    }
}
