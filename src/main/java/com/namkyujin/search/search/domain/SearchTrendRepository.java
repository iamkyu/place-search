package com.namkyujin.search.search.domain;

import java.time.LocalDate;
import java.util.List;

public interface SearchTrendRepository {
    void updateScoreByKeyword(String keyword, LocalDate baseDate);

    List<SearchTrend> findTopTrendByScoreDesc(int count, LocalDate baseDate);

    void expireAllScore(LocalDate baseDate);
}
