package com.namkyujin.search.search.application.strategy;

import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.application.SearchStrategy;
import com.namkyujin.search.search.model.exception.SearchFailedException;
import com.namkyujin.search.support.AbstractTestSearcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FastFailSearchStrategyTest extends SearchStrategyTest {

    @Override
    SearchStrategy createStrategy(PlaceSearcher searcher) {
        return new FastFailSearchStrategy(searcher);
    }

    @DisplayName("검색에 실패해도 바로 응답을 반환한다")
    @Test
    void shouldNotRetryWhenSearchFailed() {
        //given
        RuntimeException firstException = new RuntimeException();
        AbstractTestSearcher.ExceptionSearcher searcher = new AbstractTestSearcher.ExceptionSearcher(firstException);
        sut = createStrategy(searcher);

        //when
        Throwable result = catchThrowable(() -> sut.search(SEARCH_QUERY));

        //then
        assertThat(searcher.getCallCount()).isEqualTo(1);
        assertThat(result).isInstanceOf(SearchFailedException.class);
    }
}
