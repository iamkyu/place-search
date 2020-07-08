package com.namkyujin.search.config;

import com.namkyujin.search.infrastructure.circuit.CircuitProperties;
import com.namkyujin.search.infrastructure.client.ApiClientProperties;
import com.namkyujin.search.infrastructure.client.KakaoApiSpec;
import com.namkyujin.search.infrastructure.client.kakao.KakaoApiCircuitClient;
import com.namkyujin.search.infrastructure.client.kakao.KakaoApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApiClientProperties.class, CircuitProperties.class})
public class ApiClientConfiguration {

    @Bean
    @ConditionalOnProperty(name = "search.vender", havingValue = "kakao")
    public KakaoApiSpec kakaoApiCircuitClient(ApiClientProperties apiClientProperties, CircuitProperties circuitProperties,
                                              RestTemplateBuilder restTemplateBuilder) {
        return new KakaoApiCircuitClient(circuitProperties, new KakaoApiClient(apiClientProperties.getKakao(), restTemplateBuilder));
    }
}
