package com.namkyujin.search.search.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
// Value Object
public class SearchCount {
    private final String keyword;
    private final long searchCount;
}
