package com.namkyujin.search.search.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    @Query("SELECT new com.namkyujin.search.search.domain.SearchCount(sh.keyword, COUNT(sh)) " +
            "FROM SearchHistory sh WHERE sh.createdAt>=?1 GROUP BY sh.keyword ORDER BY COUNT(sh) DESC")
    List<SearchCount> findTopHistory(LocalDateTime baseDateTime, Pageable pageable);
}
