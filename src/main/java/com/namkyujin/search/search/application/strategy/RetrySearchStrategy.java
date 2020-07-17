package com.namkyujin.search.search.application.strategy;

import com.namkyujin.search.config.SearchProperties;
import com.namkyujin.search.infrastructure.circuit.CircuitBreakingException;
import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.application.SearchStrategy;
import com.namkyujin.search.search.model.SearchQuery;
import com.namkyujin.search.search.model.SearchResult;
import com.namkyujin.search.search.model.exception.SearchFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RetrySearchStrategy implements SearchStrategy {
    private final SearchProperties searchProperties;
    private final PlaceSearcher placeSearcher;

    @Override
    public SearchResult search(SearchQuery searchQuery) {
        List<Throwable> throwables = new ArrayList<>();
        for (int retryCount = 0; retryCount < searchProperties.getMaxRetry(); retryCount++) {
            try {
                return placeSearcher.search(searchQuery);

            } catch (CircuitBreakingException exception) {
                throw new SearchFailedException(exception);

            } catch (Exception exception) {
                throwables.add(exception);
                // retry except CircuitBreakingException
            }
        }

        throw SearchFailedException.createMultiSearchFailedException(throwables);
    }

    @Override
    public SearchProperties.Mode getMode() {
        return SearchProperties.Mode.RETRY;
    }
}
