package com.namkyujin.search.search.domain;

import com.namkyujin.search.common.ArgumentValidator;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_created_at_keyword", columnList = "createdAt, keyword")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long memberId;

    private String keyword;

    private LocalDateTime createdAt;

    @Builder
    public SearchHistory(long memberId, String keyword) {
        setMemberId(memberId);
        setKeyword(keyword);
    }

    /* self encapsulation */ private void setMemberId(long memberId) {
        ArgumentValidator.minNumber(1, (int) memberId, "memberId");
        this.memberId = memberId;
    }

    /* self encapsulation */ private void setKeyword(String keyword) {
        ArgumentValidator.notEmpty(keyword, "keyword");
        this.keyword = keyword;
    }

    @PrePersist
    protected void onPersist() {
        createdAt = LocalDateTime.now();
    }
}
