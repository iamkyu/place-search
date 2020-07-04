package com.namkyujin.search.search.api;

import com.namkyujin.search.context.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TrendControllerIntegrationTest extends IntegrationTest {

    @DisplayName("인증 받지 않은 사용자도 인기 검색어를 조회할 수 있다")
    @Test
    void shouldQueryTrendsWithoutAuthorization_200() {
        //when
        ResponseEntity<String> response = get(TrendController.BASE_PATH, null, String.class, new HashMap<>());

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}