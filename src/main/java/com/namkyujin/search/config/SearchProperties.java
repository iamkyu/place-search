package com.namkyujin.search.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Setter
@Configuration
@ConfigurationProperties("search")
public class SearchProperties {
    private static final Mode DEFAULT_MODE = Mode.FAST_FAIL;
    private static final Integer DEFAULT_MAX_RETRY = 1;

    private String mode;
    private Integer maxRetry;

    public Mode getMode() {
        return Optional.ofNullable(Mode.get(mode))
                .orElse(DEFAULT_MODE);
    }

    public Integer getMaxRetry() {
        return Optional.ofNullable(maxRetry)
                .orElse(DEFAULT_MAX_RETRY);
    }

    public enum Mode {
        FAST_FAIL("실패하더라도 빠르게 응답"),
        RETRY("정해진 횟수만큼 재시도");

        private String description;

        Mode(String description) {
            this.description = description;
        }

        public static Mode get(String modeAsString) {
            for (Mode mode : Mode.values()) {
                if (mode.name().equals(modeAsString)) {
                    return mode;
                }
            }
            return null;
        }
    }
}
