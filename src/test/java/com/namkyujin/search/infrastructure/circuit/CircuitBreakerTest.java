package com.namkyujin.search.infrastructure.circuit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.awaitility.Awaitility.await;

class CircuitBreakerTest {
    private CircuitBreaker<Boolean> sut;
    private CircuitProperties testProperties;

    private Counter counter;

    @BeforeEach
    void setUp() {
        counter = new Counter();
        testProperties = new CircuitProperties();
        sut = new CircuitBreaker<>(testProperties);
    }

    @DisplayName("서킷브레이커 내에서 supplier 가 실행된다")
    @Test
    void shouldInvoke() {
        //when
        sut.invoke(counter::run);

        //then
        assertThat(counter.getCallCount()).isEqualTo(1);
        assertThat(sut.getFailureCount()).isZero();
        assertThat(sut.getLastFailureTimeInUnixTime()).isEqualTo(-1);
    }

    @DisplayName("supplier 에서 발생한 예외가 전파된다")
    @Test
    void shouldThrowExceptionFromSupplier() {
        //given
        TestException expectedException = new TestException();

        //when
        long before = System.currentTimeMillis();
        Throwable result = catchThrowable(() -> sut.invoke(() -> counter.occurredException(expectedException)));
        long after = System.currentTimeMillis();

        //then
        assertThat(counter.getCallCount()).isEqualTo(1);
        assertThat(result).isInstanceOf(expectedException.getClass());
        assertThat(sut.getFailureCount()).isEqualTo(1);
        assertThat(sut.getLastFailureTimeInUnixTime()).isBetween(before, after);
    }

    @DisplayName("서킷이 닫힌 상태면 supplier 를 실행하지 않고 예외를 발생시킨다")
    @Test
    void shouldThrowCircuitBreakingExceptionWhenCircuitIsClosed() {
        //given
        testProperties.setFailureCountThreshold(1);
        testProperties.setWaitDurationInOpenState(Duration.ofDays(1));

        long before = System.currentTimeMillis();
        Throwable firstResult = catchThrowable(() -> sut.invoke(counter::occurredException));
        long after = System.currentTimeMillis();

        assertThat(counter.getCallCount()).isEqualTo(1);

        //circuit closed

        //when
        Throwable secondResult = catchThrowable(() -> sut.invoke(counter::run));

        //then
        assertThat(counter.getCallCount()).isEqualTo(1);
        assertThat(firstResult).isInstanceOf(RuntimeException.class);
        assertThat(secondResult).isInstanceOf(CircuitBreakingException.class);
        assertThat(sut.getFailureCount()).isEqualTo(1);
        assertThat(sut.getLastFailureTimeInUnixTime()).isBetween(before, after);
    }

    @DisplayName("서킷 닫힘 유지 시간이 지나면 서킷 카운트가 초기화 된다")
    @Test
    void shouldCallPermittedWhenPassTheTimeLimit() {
        //given
        testProperties.setFailureCountThreshold(1);
        testProperties.setWaitDurationInOpenState(Duration.ofMillis(100));

        // when
        Throwable firstResult = catchThrowable(() -> sut.invoke(counter::occurredException));

        // then
        assertThat(counter.getCallCount()).isEqualTo(1);
        assertThat(firstResult).isInstanceOf(RuntimeException.class);
        assertThat(sut.getFailureCount()).isEqualTo(1);

        // and when
        await()
                .atMost(Duration.ofMillis(testProperties.getWaitDurationInOpenState().toMillis() * 2))
                .until(() -> sut.invoke(counter::run));

        //then
        assertThat(counter.getCallCount()).isEqualTo(2);
        assertThat(sut.getFailureCount()).isZero();
        assertThat(sut.getLastFailureTimeInUnixTime()).isEqualTo(-1);
    }


    private static class Counter {
        private int callCount;

        public boolean run() {
            callCount++;
            return true;
        }

        public boolean occurredException() throws RuntimeException {
            callCount++;
            throw new RuntimeException();
        }

        public boolean occurredException(RuntimeException rethrow) throws RuntimeException {
            callCount++;
            throw rethrow;
        }

        public int getCallCount() {
            return callCount;
        }
    }

    private static class TestException extends RuntimeException {
    }

}