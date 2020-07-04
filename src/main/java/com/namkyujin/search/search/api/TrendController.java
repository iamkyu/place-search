package com.namkyujin.search.search.api;

import com.namkyujin.search.common.model.CommonResponse;
import com.namkyujin.search.search.application.SearchTrendService;
import com.namkyujin.search.search.domain.SearchTrend;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping(TrendController.BASE_PATH)
@RestController
@RequiredArgsConstructor
public class TrendController {
    protected static final String BASE_PATH = "/v1/trends";

    private final SearchTrendService searchTrendService;

    @GetMapping
    public CommonResponse<List<SearchTrend>> trends() {
        LocalDate now = LocalDate.now();
        return CommonResponse.of(searchTrendService.getTop10Trends(now));
    }
}
