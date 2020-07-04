package com.namkyujin.search.search.application.event;

import com.namkyujin.search.search.domain.SearchHistory;
import com.namkyujin.search.search.domain.SearchTrendRepository;
import com.namkyujin.search.search.domain.event.PlaceSearched;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchEventHandlerTest {
    private SearchEventHandler sut;
    private SearchTrendRepository searchTrendRepository;

    @BeforeEach
    void setUp() {
        searchTrendRepository = mock(SearchTrendRepository.class);
        sut = new SearchEventHandler(searchTrendRepository);
    }

    @DisplayName("검색 이벤트를 수신하면 해당 키워드의 스코어를 갱신한다")
    @Test
    void shouldUpdateScoreWhenReceivedEvent() {
        //given
        SearchHistory searchHistory = SearchHistory.builder().memberId(1L).keyword("foo").build();
        ReflectionTestUtils.setField(searchHistory, "createdAt", LocalDateTime.now());
        PlaceSearched event = PlaceSearched.of(searchHistory);

        //when
        sut.handle(event);

        //then
        verify(searchTrendRepository).updateScoreByKeyword(searchHistory.getKeyword(), searchHistory.getCreatedAt().toLocalDate());
    }

    @DisplayName("검색 이벤트 처리간 예외를 발생해도 예외를 무시한다")
    @Test
    void shouldIgnoreException() {
        //given
        SearchHistory searchHistory = SearchHistory.builder().memberId(1L).keyword("foo").build();
        ReflectionTestUtils.setField(searchHistory, "createdAt", LocalDateTime.now());
        PlaceSearched event = PlaceSearched.of(searchHistory);

        doThrow(new RuntimeException()).when(searchTrendRepository).updateScoreByKeyword(any(), any());


        //when
        sut.handle(event);

        //then
        assertThat(true).isTrue();
    }
}