package com.simon.ical.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeUtils {

    public static int now() {
        return ((int) (System.currentTimeMillis() / 1000));
    }
}
