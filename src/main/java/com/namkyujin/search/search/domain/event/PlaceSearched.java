package com.namkyujin.search.search.domain.event;

import com.namkyujin.search.search.domain.SearchHistory;
import org.springframework.context.ApplicationEvent;

public class PlaceSearched extends ApplicationEvent {

    private PlaceSearched(SearchHistory source) {
        super(source);
    }

    public static PlaceSearched of(SearchHistory source) {
        return new PlaceSearched(source);
    }

    @Override
    public SearchHistory getSource() {
        return (SearchHistory) this.source;
    }
}
