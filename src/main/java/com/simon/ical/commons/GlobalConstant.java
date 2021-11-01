package com.simon.ical.commons;

import org.springframework.boot.system.ApplicationHome;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalConstant {

    public static final String ICS_FILE_PATH = new ApplicationHome(GlobalConstant.class).getDir().getPath() + "/cache/";
    public static final String ICS_FILE_NAME = "festival_of_china.ics";
    public static final String ICS_FULL_PATH = ICS_FILE_PATH + ICS_FILE_NAME;
}
