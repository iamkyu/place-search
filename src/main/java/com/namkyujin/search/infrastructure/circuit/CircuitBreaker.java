package com.namkyujin.search.infrastructure.circuit;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * TODO 서킷브레이커 개선
 * synchronized 또는 {@link java.util.concurrent.locks.Lock} 사용할수도 있지만 서킷 상태를 완전히 무결하게 보장하려면 supplier 실행 부분도 임계구역에 포함해야 함.
 * 하지만 여기서 supplier 는 외부 API 와 통신하는 일을 하고, 외부 API 는 통제할 수 없는 부분이기 때문에 예상치 못하게 시간이 지연될 수 있음.
 * 그에 따라 잠금 시간이 길어지면 활동성이 지나치게 떨어짐.
 * 따라서 일단은 서킷의 failureCount, lastFailureTimeInUnixTime 의 완전한 무결성은 조금 포기하더라도 활성성을 높이는 트레이드오프를 선택.
 * 조금이라도 보완하기 위해 compareAndSet 시 compare 에 실패한다면 다시 set 을 시도하진 않음. (이미 비슷한 시간대 다른 스레드에서 값 변경했다고 가정)
 */
@Slf4j
public class CircuitBreaker<T> {
    private CircuitProperties circuitProperties;
    private AtomicInteger failureCount = new AtomicInteger();
    private AtomicLong lastFailureTimeInUnixTime = new AtomicLong();

    public CircuitBreaker(CircuitProperties circuitProperties) {
        this.circuitProperties = circuitProperties;
    }

    public T invoke(Supplier<T> supplier) {
        long invokeTimeInUnixTime = System.currentTimeMillis();
        long currentLastFailureTimeInUnixTime = lastFailureTimeInUnixTime.get();
        int currentFailureCount = failureCount.get();

        if (isFailureThreshold(currentFailureCount)) {
            if (inTimeLimit(invokeTimeInUnixTime, currentLastFailureTimeInUnixTime)) {
                log.warn("Call not permitted. Threshold = {}, FailureCount = {}, lastFailureTime = {}",
                        circuitProperties.getFailureCountThreshold(), currentFailureCount, currentLastFailureTimeInUnixTime);
                throw new CircuitBreakingException();
            }
            reset();
        }


        try {
            return supplier.get();
        } catch (Exception exception) {
            lastFailureTimeInUnixTime.compareAndSet(currentLastFailureTimeInUnixTime, System.currentTimeMillis());
            failureCount.compareAndSet(currentFailureCount, currentFailureCount + 1);
            if (isFailureThreshold()) {
                log.info("CircuitBreaker changed state to OPEN");
            }

            throw exception;
        }
    }

    /* for test */ int getFailureCount() {
        return failureCount.get();
    }

    /* for test */ long getLastFailureTimeInUnixTime() {
        return lastFailureTimeInUnixTime.get();
    }

    private boolean isFailureThreshold() {
        return isFailureThreshold(failureCount.get());
    }

    private boolean isFailureThreshold(int failureCount) {
        return failureCount >= circuitProperties.getFailureCountThreshold();
    }

    private boolean inTimeLimit(long invokeTimeInUnixTime, long currentLastFailureTimeInUnixTime) {
        long waitTimeInOpenState = circuitProperties.getWaitDurationInOpenState().toMillis();
        return (waitTimeInOpenState + currentLastFailureTimeInUnixTime) > invokeTimeInUnixTime;
    }

    private void reset() {
        failureCount.set(0);
        lastFailureTimeInUnixTime.set(0);
        log.info("CircuitBreaker changed state to CLOSE");
    }
}
