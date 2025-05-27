package io.raynelz.event_manager.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import io.raynelz.event_manager.config.RabbitMQConfig;
import io.raynelz.event_manager.model.Event;

@Component
public class EventListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleEventCreated(Event event) {
        System.out.println("Received Event: " + event.getTitle());
    }
}
