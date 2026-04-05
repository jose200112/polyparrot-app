package com.polyparrot.notificationservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.polyparrot.notificationservice.event.BookingConfirmedEvent;
import com.polyparrot.notificationservice.event.BookingCreatedEvent;
import com.polyparrot.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "booking-created", groupId = "notification-group",
    	    properties = {"spring.json.value.default.type=com.polyparrot.notificationservice.event.BookingCreatedEvent"})
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Evento recibido booking-created: bookingId={}", event.getBookingId());
        notificationService.createBookingCreatedNotification(
            event.getTeacherId(),
            event.getBookingId(),
            event.getStartTime()
        );
    }

    @KafkaListener(topics = "booking-confirmed", groupId = "notification-group",
    	    properties = {"spring.json.value.default.type=com.polyparrot.notificationservice.event.BookingConfirmedEvent"})    
    public void handleBookingConfirmed(BookingConfirmedEvent event) {
        log.info("Evento recibido booking-confirmed: bookingId={}", event.getBookingId());
        notificationService.createBookingConfirmedNotification(
            event.getStudentId(),
            event.getBookingId(),
            event.getStartTime()
        );
    }
}