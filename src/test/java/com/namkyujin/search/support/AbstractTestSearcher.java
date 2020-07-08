package com.namkyujin.search.support;

import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.model.SearchQuery;
import com.namkyujin.search.search.model.SearchResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class AbstractTestSearcher implements PlaceSearcher {
    private int callCount;

    @Override
    public SearchResult search(SearchQuery searchQuery) {
        callCount++;
        return delegate(searchQuery);
    }

    protected abstract SearchResult delegate(SearchQuery searchQuery);


    @Override
    public int getOrder() {
        return 0;
    }



    public static class NormalSearcher extends AbstractTestSearcher {
        @Override
        protected SearchResult delegate(SearchQuery searchQuery) {
            return SearchResult.of(1, 1, Collections.emptyList());
        }
    }

    public static class ExceptionSearcher extends AbstractTestSearcher {
        private final List<RuntimeException> exceptions;
        private int lastIndex = 0;

        public ExceptionSearcher(RuntimeException... exceptions) {
            this.exceptions = new ArrayList<>(Arrays.asList(exceptions));
        }

        @Override
        protected SearchResult delegate(SearchQuery searchQuery) {
            if (exceptions.size() > lastIndex) {
                RuntimeException willThrow = exceptions.get(lastIndex);
                lastIndex++;
                throw willThrow;
            }

            return SearchResult.of(1, 1, Collections.emptyList());
        }
    }
}
