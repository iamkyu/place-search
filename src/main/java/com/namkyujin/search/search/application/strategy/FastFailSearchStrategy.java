package com.namkyujin.search.search.application.strategy;

import com.namkyujin.search.config.SearchProperties;
import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.application.SearchStrategy;
import com.namkyujin.search.search.model.SearchQuery;
import com.namkyujin.search.search.model.SearchResult;
import com.namkyujin.search.search.model.exception.SearchFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FastFailSearchStrategy implements SearchStrategy {
    private final PlaceSearcher placeSearcher;

    @Override
    public SearchResult search(SearchQuery searchQuery) {
        try {
            return placeSearcher.search(searchQuery);
        } catch (Exception exception) {
            throw new SearchFailedException(exception);
        }
    }

    @Override
    public SearchProperties.Mode getMode() {
        return SearchProperties.Mode.FAST_FAIL;
    }
}
