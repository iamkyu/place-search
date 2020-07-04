package com.namkyujin.search.search.application;


import com.namkyujin.search.search.domain.SearchCount;
import com.namkyujin.search.search.domain.SearchHistoryRepository;
import com.namkyujin.search.search.domain.SearchTrend;
import com.namkyujin.search.search.domain.SearchTrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchTrendService {
    private static final int MAX_TREND_COUNT = 10;

    private final SearchTrendRepository searchTrendRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    @Transactional(readOnly = true)
    public List<SearchTrend> getTop10Trends(LocalDate baseDate) {
        try {
            return lookASideCache(baseDate, MAX_TREND_COUNT);
        } catch (Exception e) {
            log.error("Failed to get trends from cache. cause by {}", e.getMessage(), e);
        }

        List<SearchCount> searchCounts = searchHistoryRepository
                .findTopHistory(baseDate.atTime(LocalTime.MIDNIGHT), PageRequest.of(0, MAX_TREND_COUNT));

        return  searchCounts.stream()
                .map(searchCount -> SearchTrend.of(searchCount.getKeyword(), searchCount.getSearchCount()))
                .collect(toList());
    }


    private List<SearchTrend> lookASideCache(LocalDate baseDate, int count) {
       return searchTrendRepository.findTopTrendByScoreDesc(count, baseDate);
    }

}
