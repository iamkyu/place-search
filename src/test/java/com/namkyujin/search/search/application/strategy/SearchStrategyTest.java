package com.namkyujin.search.search.application.strategy;

import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.application.SearchStrategy;
import com.namkyujin.search.search.model.SearchQuery;
import com.namkyujin.search.support.AbstractTestSearcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class SearchStrategyTest {
    protected static final SearchQuery SEARCH_QUERY = SearchQuery.of("test", 1, 10);

    protected SearchStrategy sut;

    @DisplayName("검색에 성공하면 바로 응답을 반환한다")
    @Test
    void shouldReturnResultWhenSearchIsSuccess() {
        //given
        AbstractTestSearcher searcher = new AbstractTestSearcher.NormalSearcher();
        sut = createStrategy(searcher);

        //when
        sut.search(SEARCH_QUERY);

        //then
        assertThat(searcher.getCallCount()).isEqualTo(1);
    }

    abstract SearchStrategy createStrategy(PlaceSearcher searcher);
}
