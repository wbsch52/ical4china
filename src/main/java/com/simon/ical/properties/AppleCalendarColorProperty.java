package com.simon.ical.properties;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
public class AppleCalendarColorProperty extends IcalPropertyAdapter {

    private static final String NAME = "X-APPLE-CALENDAR-COLOR";

    public AppleCalendarColorProperty(String value) {
        super(NAME, value);
    }
}
