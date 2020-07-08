package com.namkyujin.search.config;

import com.namkyujin.search.infrastructure.client.KakaoApiSpec;
import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.application.searcher.KakaoPlaceApiResponseTransformer;
import com.namkyujin.search.search.application.searcher.KakaoPlaceSearcher;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(ApiClientConfiguration.class)
public class SearchConfiguration {

    @Bean
    @ConditionalOnProperty(name = "search.vender", havingValue = "kakao")
    public PlaceSearcher placeSearcher(KakaoApiSpec kakaoApiSpec) {
        return new KakaoPlaceSearcher(kakaoApiSpec, new KakaoPlaceApiResponseTransformer());
    }
}
