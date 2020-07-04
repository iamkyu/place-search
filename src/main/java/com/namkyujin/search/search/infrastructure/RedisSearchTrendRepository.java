package com.namkyujin.search.search.infrastructure;

import com.namkyujin.search.search.domain.SearchTrend;
import com.namkyujin.search.search.domain.SearchTrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisSearchTrendRepository implements SearchTrendRepository {
    private static final String KEY_FORMAT = "search-rank:%s";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void updateScoreByKeyword(String keyword, LocalDate baseDate) {
        redisTemplate.opsForZSet().incrementScore(toKey(baseDate), keyword, 1d);
    }

    @Override
    public List<SearchTrend> findTopTrendByScoreDesc(int count, LocalDate baseDate) {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(toKey(baseDate), 0, count - 1);

        if (Objects.isNull(tuples)) {
            return Collections.emptyList();
        }

        return tuples.stream()
                .map(tuple -> SearchTrend.of(tuple.getValue(), tuple.getScore().longValue()))
                .collect(toList());
    }

    @Override
    public void expireAllScore(LocalDate baseDate) {
        redisTemplate.expire(toKey(baseDate), 10L, TimeUnit.SECONDS);
    }

    private String toKey(LocalDate localDate) {
        return String.format(KEY_FORMAT, localDate) ;
    }
}
