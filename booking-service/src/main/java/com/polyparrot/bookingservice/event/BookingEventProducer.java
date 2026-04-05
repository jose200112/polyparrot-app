package com.polyparrot.bookingservice.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.polyparrot.bookingservice.entity.BookingConfirmedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishBookingCreated(BookingCreatedEvent event) {
        kafkaTemplate.send("booking-created", event);
        log.info("Evento booking-created publicado: bookingId={}", event.getBookingId());
    }

    public void publishBookingConfirmed(BookingConfirmedEvent event) {
        kafkaTemplate.send("booking-confirmed", event);
        log.info("Evento booking-confirmed publicado: bookingId={}", event.getBookingId());
    }
}