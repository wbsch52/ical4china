package com.simon.ical.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IpHolder {

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static void set(String ip) {
        holder.set(ip);
    }

    public static String get() {
        return holder.get();
    }

    public static void remove() {
        holder.remove();
    }
}
