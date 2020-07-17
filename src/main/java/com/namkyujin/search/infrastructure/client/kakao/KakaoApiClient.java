package com.namkyujin.search.infrastructure.client.kakao;

import com.namkyujin.search.infrastructure.client.ApiClient;
import com.namkyujin.search.infrastructure.client.ApiClientProperties;
import com.namkyujin.search.infrastructure.client.IntegrationFailedException;
import com.namkyujin.search.infrastructure.client.KakaoApiSpec;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class KakaoApiClient extends ApiClient implements KakaoApiSpec {

    public KakaoApiClient(ApiClientProperties.InstanceProperties properties, RestTemplateBuilder restTemplateBuilder) {
        super(properties, restTemplateBuilder);
    }

    private static final String PATH_KEYWORD_SEARCH = "/v2/local/search/keyword.json?page={page}&size={size}&query={query}";

    @Override
    public KakaoKeywordSearchResponse search(KakaoKeywordSearchRequest request) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", request.getPage());
        queryParams.put("size", request.getSize());
        queryParams.put("query", request.getQuery());

        URI uri = restTemplate.getUriTemplateHandler().expand(PATH_KEYWORD_SEARCH, queryParams);

        try {
            return restTemplate.getForObject(uri, KakaoKeywordSearchResponse.class);
        } catch (RestClientResponseException e) {
            handleException(uri, e);
            throw new IntegrationFailedException(e);
        }
    }
}
