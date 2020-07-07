package com.namkyujin.search.search.application.event;

import com.namkyujin.search.search.application.SearchEventPublisher;
import com.namkyujin.search.search.domain.SearchHistory;
import com.namkyujin.search.search.domain.event.PlaceSearched;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SearchEventMulticasterTest {
    private SearchEventMulticaster sut;
    private List<SearchEventPublisher> publishers = new ArrayList<>();

    @DisplayName("등록 된 모든 이벤트 퍼블리셔에게 이벤트를 통지한다")
    @Test
    void shouldNotifyAllPublishers() {
        //given
        PlaceSearched event = PlaceSearched.of(SearchHistory.builder().keyword("keyword").build());

        SearchEventPublisher first = mock(SearchEventPublisher.class);
        SearchEventPublisher second = mock(SearchEventPublisher.class);
        publishers.addAll(Arrays.asList(first, second));

        sut = new SearchEventMulticaster(publishers);

        //when
        sut.publish(event);

        //then
        for (SearchEventPublisher publisher : publishers) {
            verify(publisher).publish(eq(event));
        }
    }
}