package com.namkyujin.search.search.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
// Value Object
public class SearchTrend {
    private final String keyword;
    private final long searchCount;
}
