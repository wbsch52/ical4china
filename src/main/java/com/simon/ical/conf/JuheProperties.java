package com.simon.ical.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Data
@ConfigurationProperties(JuheProperties.PREFIX)
public class JuheProperties {

    public static final String PREFIX = "ical4china.juhe";

    /**
     * 聚合数据平台AppKey
     */
    private String appKey;
}
