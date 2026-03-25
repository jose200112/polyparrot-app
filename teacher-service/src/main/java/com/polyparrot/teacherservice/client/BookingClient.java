package com.polyparrot.teacherservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service", url = "http://localhost:8082")
public interface BookingClient {

    @GetMapping("/bookings/check")
    boolean hasBookings(
        @RequestParam Long teacherId,
        @RequestParam String startTime,
        @RequestParam String endTime
    );
}
