package com.namkyujin.search.search.application.strategy;

import com.namkyujin.search.context.IntegrationTest;
import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.model.SearchQuery;
import com.namkyujin.search.search.model.SearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FastFailSearchStrategyIntegrationTest extends IntegrationTest {
    @Autowired
    private FastFailSearchStrategy sut;

    @DisplayName("PlaceSearcher 는 Order 순으로 정렬되어 있다")
    @Test
    void shouldSortedSearchersByOrderNumber() {
        //when then
        List<PlaceSearcher> placeSearchers = sut.placeSearchers;
        assertThat(placeSearchers).hasSizeGreaterThanOrEqualTo(2);

        for (int index = 1; index < placeSearchers.size(); index++) {
            assertThat(placeSearchers.get(index).getOrder())
                    .isGreaterThanOrEqualTo(placeSearchers.get(index - 1).getOrder());
        }
    }


    @TestConfiguration
    static class TestConfig {
        @Bean
        public PlaceSearcher testPlaceSearcher3() {
            return new PlaceSearcher() {
                @Override
                public SearchResult search(SearchQuery searchQuery) {
                    throw new UnsupportedOperationException("test bean");
                }

                @Override
                public int getOrder() {
                    return Order.TERTIARY.getOrderNumber();
                }
            };
        }

        @Bean
        public PlaceSearcher testPlaceSearcher99() {
            return new PlaceSearcher() {
                @Override
                public SearchResult search(SearchQuery searchQuery) {
                    throw new UnsupportedOperationException("test bean");
                }

                @Override
                public int getOrder() {
                    return -99;
                }
            };
        }
    }
}
