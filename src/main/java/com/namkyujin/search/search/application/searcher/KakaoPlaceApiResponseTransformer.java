package com.namkyujin.search.search.application.searcher;

import com.namkyujin.search.infrastructure.client.kakao.KakaoKeywordSearchResponse;
import com.namkyujin.search.search.model.SearchResult;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class KakaoPlaceApiResponseTransformer implements PlaceApiResponseTransformer<KakaoKeywordSearchResponse> {

    @Override
    public SearchResult transform(KakaoKeywordSearchResponse response) {
        List<KakaoKeywordSearchResponse.Document> documents = response.getDocuments();

        List<SearchResult.Place> places = documents.stream()
                .map(document -> {
                    String link = SearchResult.LinkBuilder.builder(document.getY(), document.getX())
                            .withName(document.getPlaceName())
                            .build();

                    return SearchResult.Place.builder()
                            .name(document.getPlaceName())
                            .address(document.getAddressName())
                            .roadAddress(document.getRoadAddressName())
                            .phone(document.getPhone())
                            .longitude(document.getX())
                            .latitude(document.getY())
                            .link(link)
                            .build();
                })
                .collect(toList());

        KakaoKeywordSearchResponse.Meta meta = response.getMeta();
        return SearchResult.of(places.size(), meta.getPageableCount(), places);
    }
}
