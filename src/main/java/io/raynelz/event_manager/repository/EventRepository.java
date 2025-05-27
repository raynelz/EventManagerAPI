package io.raynelz.event_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.raynelz.event_manager.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
