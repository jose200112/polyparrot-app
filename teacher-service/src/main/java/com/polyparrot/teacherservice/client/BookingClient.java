package com.polyparrot.teacherservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.polyparrot.teacherservice.dto.BookingDto;

@FeignClient(name = "booking-service", url = "${booking-service.url}")
public interface BookingClient {

    @GetMapping("/bookings/check")
    boolean hasBookings(
        @RequestParam Long teacherId,
        @RequestParam String startTime,
        @RequestParam String endTime
    );

    @GetMapping("/bookings/available-teachers")
    List<Long> getAvailableTeacherIds(
        @RequestParam String startTime,
        @RequestParam String endTime
    );
    
    @GetMapping("/bookings/teacher")
    List<BookingDto> getBookingsByTeacher(@RequestParam Long teacherId);
}