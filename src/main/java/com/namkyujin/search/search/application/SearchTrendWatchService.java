package com.namkyujin.search.search.application;

import com.namkyujin.search.search.domain.SearchTrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchTrendWatchService {
    // patten : second, minute, hour, day, month, weekday
    private static final String EVERY_DAY_AT_10_O_CLOCK = "0 0 10 * * *";

    private final SearchTrendRepository searchTrendRepository;

    @Scheduled(cron = EVERY_DAY_AT_10_O_CLOCK)
    public void removeYesterdayTrend() {
        LocalDate yesterday = LocalDate.now().minusDays(1L);
        searchTrendRepository.expireAllScore(yesterday);
        log.info("Remove Trend. Base Date: {}", yesterday);
    }
}
