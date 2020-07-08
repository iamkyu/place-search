package com.namkyujin.search.search.application.strategy;

import com.namkyujin.search.config.SearchProperties;
import com.namkyujin.search.infrastructure.circuit.CircuitBreakingException;
import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.application.SearchStrategy;
import com.namkyujin.search.search.model.exception.SearchFailedException;
import com.namkyujin.search.support.AbstractTestSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RetrySearchStrategyTest extends SearchStrategyTest {
    private SearchProperties searchProperties;

    @BeforeEach
    void setUp() {
        searchProperties = new SearchProperties();
    }

    @DisplayName("검색에 실패하면 지정된 횟수만큼 재시도 한다")
    @Test
    void shouldRetry() {
        //given
        int maxRetry = 3;
        searchProperties.setMaxRetry(maxRetry);

        RuntimeException firstException = new RuntimeException();
        RuntimeException secondException = new RuntimeException();
        AbstractTestSearcher.ExceptionSearcher searcher = new AbstractTestSearcher.ExceptionSearcher(firstException, secondException);

        sut = createStrategy(searcher);

        //when
        sut.search(SEARCH_QUERY);

        //then
        assertThat(searcher.getCallCount()).isEqualTo(maxRetry);
    }

    @DisplayName("지정된 retry 횟수동안 검색에 성공하지 못하면 예외를 발생시킨다")
    @Test
    void shouldOccurredExceptionWhenAllRetriesFailed() {
        //given
        int maxRetry = 3;
        searchProperties.setMaxRetry(maxRetry);

        RuntimeException firstException = new RuntimeException();
        RuntimeException secondException = new RuntimeException();
        RuntimeException thirdException = new RuntimeException();
        AbstractTestSearcher.ExceptionSearcher searcher = new AbstractTestSearcher.ExceptionSearcher(firstException, secondException, thirdException);

        sut = createStrategy(searcher);

        //when
        Throwable result = catchThrowable(() -> sut.search(SEARCH_QUERY));

        //then
        assertThat(searcher.getCallCount()).isEqualTo(maxRetry);
        assertThat(result).isInstanceOf(SearchFailedException.class);
    }

    @DisplayName("CircuitBreakingException 발생하면 더이상 재시도 하지 않는다")
    @Test
    void shouldNotRetryWhenOccurredCircuitBreakingException() {
        //given
        int maxRetry = 5;
        searchProperties.setMaxRetry(maxRetry);

        RuntimeException firstException = new RuntimeException();
        CircuitBreakingException secondException = new CircuitBreakingException();
        RuntimeException thirdException = new RuntimeException();
        AbstractTestSearcher.ExceptionSearcher searcher = new AbstractTestSearcher.ExceptionSearcher(firstException, secondException, thirdException);

        sut = createStrategy(searcher);

        //when
        Throwable result = catchThrowable(() -> sut.search(SEARCH_QUERY));

        //then
        assertThat(searcher.getCallCount()).isEqualTo(2);
        assertThat(result).isInstanceOf(SearchFailedException.class);
    }

    @Override
    SearchStrategy createStrategy(PlaceSearcher searcher) {
        return new RetrySearchStrategy(searchProperties, searcher);
    }
}
