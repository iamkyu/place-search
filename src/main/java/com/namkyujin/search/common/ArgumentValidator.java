package com.namkyujin.search.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArgumentValidator {

    /**
     * 빈 문자열 아님을 확인
     *
     * @param target       확인 대상
     * @param argumentName 인자명
     * @throws IllegalArgumentException 유효하지 않음
     */
    public static void notEmpty(String target, String argumentName) throws IllegalArgumentException {
        if (StringUtils.isEmpty(target)) {
            throw new IllegalArgumentException(
                    String.format("%s should not be empty.", argumentName));
        }
    }

    /**
     * 숫자 최소값 확인 (null 아닌 경우만 확인)
     *
     * @param target       확인 대상
     * @param minimum      최소값 (이상)
     * @param argumentName 인자명
     * @throws IllegalArgumentException 유효하지 않음
     */
    public static void minNumber(int target, int minimum, String argumentName) throws IllegalArgumentException {
        if (target < minimum) {
            throw new IllegalArgumentException(
                    String.format("%s should be %d or more.", argumentName, minimum));
        }
    }

    public static void minNumber(long target, long minimum, String argumentName) throws IllegalArgumentException {
        if (target < minimum) {
            throw new IllegalArgumentException(
                    String.format("%s should be %d or more.", argumentName, minimum));
        }
    }
}
