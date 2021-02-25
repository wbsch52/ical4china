package com.simon.ical.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Data
public class Holiday {

    private String name;
    private LocalDate date;
    private String desc;
    private String tips;
    private List<Item> items;


    @Data
    public static class Item implements Comparable<Item> {

        private LocalDate date;
        private Boolean status;

        public Item(LocalDate date, Boolean status) {
            this.date = date;
            this.status = status;
        }

        @Override
        public int compareTo(Item o) {
            return this.getDate().compareTo(o.getDate());
        }
    }
}
