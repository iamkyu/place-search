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

    private String mode;

    public Mode getMode() {
        return Optional.ofNullable(Mode.get(mode))
                .orElse(DEFAULT_MODE);
    }


    public enum Mode {
        FAST_FAIL("실패하더라도 빠르게 응답"),
        HA("정상적인 응답을 주기 위해 가능한 방법 모두 시도");

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
