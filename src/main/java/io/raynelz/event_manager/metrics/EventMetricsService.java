package io.raynelz.event_manager.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EventMetricsService {

    private final Counter eventCreatedCounter;
    private final AtomicInteger activeEventCount;
    private final Gauge eventActiveGauge;
    private final Timer eventCreationTimer;

    public EventMetricsService(MeterRegistry meterRegistry) {
        this.eventCreatedCounter = Counter.builder("event_total")
                .description("Total number of events created")
                .tag("source", "api")
                .register(meterRegistry);

        this.activeEventCount = new AtomicInteger(0);
        this.eventActiveGauge = Gauge.builder("event_active_count", activeEventCount, AtomicInteger::get)
                .description("Current number of active events")
                .register(meterRegistry);

        this.eventCreationTimer = Timer.builder("event_creation_duration")
                .description("Time taken to create a event")
                .tag("source", "api")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    public void incrementCreatedEvents() {
        eventCreatedCounter.increment();
    }

    public void incrementActiveEvents() {
        activeEventCount.incrementAndGet();
    }

    public void decrementActiveEvents() {
        activeEventCount.decrementAndGet();
    }

    public Timer getEventCreationTimer() {
        return eventCreationTimer;
    }
}
