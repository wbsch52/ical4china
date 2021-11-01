package com.simon.ical;

import com.simon.ical.conf.JuheProperties;

import org.springframework.boot.ResourceBanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(JuheProperties.class)
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .banner(new ResourceBanner(new ClassPathResource("banner.txt")))
                .sources(Application.class)
                .build().run(args);
    }
}
