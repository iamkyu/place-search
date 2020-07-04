package com.namkyujin.search.search.application.event;

import com.namkyujin.search.search.domain.SearchHistory;
import com.namkyujin.search.search.domain.SearchTrendRepository;
import com.namkyujin.search.search.domain.event.PlaceSearched;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEventHandler {
    private final SearchTrendRepository searchTrendRepository;

    @EventListener
    public void handle(PlaceSearched message) {
        log.info("Receive {}: [event = {}]", ClassUtils.getShortName(message.getClass()), message);

        try {
            SearchHistory source = message.getSource();
            searchTrendRepository.updateScoreByKeyword(source.getKeyword(), source.getCreatedAt().toLocalDate());
        } catch (Exception e) {
            // just ignore
            log.error("Failed to consume event. Cause By {}", e.getMessage(), e);
        }
    }
}
