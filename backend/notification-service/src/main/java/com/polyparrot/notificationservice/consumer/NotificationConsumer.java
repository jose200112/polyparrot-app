package com.polyparrot.notificationservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.polyparrot.notificationservice.event.BookingCancelledEvent;
import com.polyparrot.notificationservice.event.BookingConfirmedEvent;
import com.polyparrot.notificationservice.event.BookingCreatedEvent;
import com.polyparrot.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "booking-created", groupId = "notification-group",
    	    properties = {"spring.json.value.default.type=com.polyparrot.notificationservice.event.BookingCreatedEvent"})
    	public void handleBookingCreated(BookingCreatedEvent event) {
    	    notificationService.createBookingCreatedNotification(
    	        event.getTeacherId(), event.getBookingId(),
    	        event.getStartTime(), event.getStudentName()
    	    );
    	}

    	@KafkaListener(topics = "booking-confirmed", groupId = "notification-group",
    	    properties = {"spring.json.value.default.type=com.polyparrot.notificationservice.event.BookingConfirmedEvent"})
    	public void handleBookingConfirmed(BookingConfirmedEvent event) {
    	    notificationService.createBookingConfirmedNotification(
    	        event.getStudentId(), event.getBookingId(),
    	        event.getStartTime(), event.getTeacherName()
    	    );
    	}

    	@KafkaListener(topics = "booking-cancelled", groupId = "notification-group",
    	    properties = {"spring.json.value.default.type=com.polyparrot.notificationservice.event.BookingCancelledEvent"})
    	public void handleBookingCancelled(BookingCancelledEvent event) {
    	    if ("TEACHER".equals(event.getCancelledBy())) {
    	        notificationService.createBookingCancelledNotification(
    	            event.getStudentId(), event.getBookingId(),
    	            event.getStartTime(), event.getCancellerName()
    	        );
    	    } else {
    	        notificationService.createBookingCancelledByStudentNotification(
    	            event.getTeacherId(), event.getBookingId(),
    	            event.getStartTime(), event.getCancellerName()
    	        );
    	    }
    	}
}