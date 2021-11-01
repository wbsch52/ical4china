package com.simon.ical.domain;

import java.time.LocalDate;

import lombok.Data;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Data
public class SimpleHoliday {

    private LocalDate start;
    private String name;
}
