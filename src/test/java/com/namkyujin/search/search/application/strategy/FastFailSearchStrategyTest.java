package com.namkyujin.search.search.application.strategy;

import com.namkyujin.search.search.application.PlaceSearcher;
import com.namkyujin.search.search.application.SearchStrategy;

class FastFailSearchStrategyTest extends SearchStrategyTest {

    @Override
    SearchStrategy createStrategy(PlaceSearcher searcher) {
        return new FastFailSearchStrategy(searcher);
    }
}
