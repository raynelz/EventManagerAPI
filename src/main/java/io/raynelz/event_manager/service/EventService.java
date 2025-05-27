package io.raynelz.event_manager.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import io.raynelz.event_manager.metrics.EventMetricsService;
import io.raynelz.event_manager.model.Event;
import io.raynelz.event_manager.repository.EventRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventPublisher eventPublisher;
    private final EventMetricsService metricsService;

    public EventService(
        EventRepository eventRepository,
        EventPublisher eventPublisher,
        EventMetricsService metricsService
        ) {
        this.eventRepository = eventRepository;
        this.eventPublisher = eventPublisher;
        this.metricsService = metricsService;
    }

    @Cacheable("events")
    public List<Event> getAllEvents() {
        System.out.println(">>> Fetching events from database...");
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @CacheEvict(value = "events", allEntries = true)
    public Event createEvent(Event event) {
        return metricsService.getEventCreationTimer().record(() -> {
            Event savedEvent = eventRepository.save(event);
            eventPublisher.sendCreatedEvent(savedEvent);
            metricsService.incrementCreatedEvents();
            metricsService.incrementActiveEvents();
            return savedEvent;
        });
    }

    @CacheEvict(value = "events", allEntries = true)
    public Optional<Event> updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(existing -> {
            existing.setTitle(updatedEvent.getTitle());
            existing.setDescription(updatedEvent.getDescription());
            existing.setStatus(updatedEvent.getStatus());
            return eventRepository.save(existing);
        });
    }

    @CacheEvict(value = "events", allEntries = true)
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
        metricsService.decrementActiveEvents();
    }
}
