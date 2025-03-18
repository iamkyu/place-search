package com.namkyujin.search.infrastructure.circuit;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
public class CircuitBreaker<T> {
    private enum State {
        CLOSED, OPEN
    }

    private final CircuitProperties circuitProperties;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTimeInUnixTime = new AtomicLong(-1);
    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);

    public CircuitBreaker(CircuitProperties circuitProperties) {
        this.circuitProperties = circuitProperties;
    }

    public T invoke(Supplier<T> supplier) {
        long now = System.currentTimeMillis();

        if (state.get() == State.OPEN) {
            if (now - lastFailureTimeInUnixTime.get() < circuitProperties.getWaitDurationInOpenState().toMillis()) {
                log.warn("Call not permitted. Circuit is OPEN. FailureCount = {}, LastFailureTime = {}",
                        failureCount.get(), LocalDateTime.ofInstant(Instant.ofEpochMilli(lastFailureTimeInUnixTime.get()), ZoneId.systemDefault()));

                throw new CircuitBreakingException();
            }
            reset();
        }

        try {
            T result = supplier.get();
            reset();
            return result;
        } catch (Exception ex) {
            onFailure(now);
            throw ex;
        }
    }

    private void onFailure(long now) {
        int currentFailures = failureCount.incrementAndGet();
        lastFailureTimeInUnixTime.set(now);

        if (currentFailures >= circuitProperties.getFailureCountThreshold() && state.compareAndSet(State.CLOSED, State.OPEN)) {
            log.debug("Circuit changed state to OPEN");
        }

    }

    private void reset() {
        failureCount.set(0);
        lastFailureTimeInUnixTime.set(-1);
        state.set(State.CLOSED);
    }

    /* for test */ int getFailureCount() {
        return failureCount.get();
    }

    /* for test */ long getLastFailureTimeInUnixTime() {
        return lastFailureTimeInUnixTime.get();
    }


    /* for test */ void setState() {
        state.set(State.OPEN);
    }
}
