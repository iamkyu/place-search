package com.namkyujin.search.search.application.searcher;

import com.namkyujin.search.infrastructure.client.KakaoApiSpec;
import com.namkyujin.search.infrastructure.client.kakao.KakaoKeywordSearchRequest;
import com.namkyujin.search.infrastructure.client.kakao.KakaoKeywordSearchResponse;
import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.model.SearchQuery;
import com.namkyujin.search.search.model.SearchResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoPlaceSearcher implements PlaceSearcher {
    private final KakaoApiSpec kakaoApiSpec;
    private final KakaoPlaceApiResponseTransformer transformer;

    @Override
    public SearchResult search(SearchQuery searchQuery) {
        KakaoKeywordSearchRequest request = KakaoKeywordSearchRequest.builder()
                .query(searchQuery.getKeyword())
                .page(searchQuery.getPage())
                .size(searchQuery.getPageSize())
                .build();

        KakaoKeywordSearchResponse response = kakaoApiSpec.search(request);
        return transformer.transform(response);
    }

    @Override
    public int getOrder() {
        return Order.PRIMARY.getOrderNumber();
    }
}
