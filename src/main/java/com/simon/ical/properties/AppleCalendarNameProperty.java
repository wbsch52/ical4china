package com.simon.ical.properties;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
public class AppleCalendarNameProperty extends IcalPropertyAdapter {

    private static final String NAME = "X-WR-CALNAME";

    private static final String DEFAULT_VALUE = "MY_Calendar";

    public AppleCalendarNameProperty() {
        this(DEFAULT_VALUE);
    }

    public AppleCalendarNameProperty(String value) {
        super(NAME, value);
    }

}
