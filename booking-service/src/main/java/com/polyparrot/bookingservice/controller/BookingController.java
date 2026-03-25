package com.polyparrot.bookingservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polyparrot.bookingservice.dto.AvailabilitySlotDto;
import com.polyparrot.bookingservice.dto.BookingRequest;
import com.polyparrot.bookingservice.entity.Booking;
import com.polyparrot.bookingservice.service.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestBody BookingRequest request) {
        return bookingService.createBooking(
            request.getTeacherId(),
            request.getStartTime(),
            request.getEndTime()
        );
    }

    @GetMapping("/me")
    public List<Booking> myBookings() {
        return bookingService.getMyBookings();
    }
    
    @PatchMapping("/{id}/cancel")
    public Booking cancel(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }
    
    @GetMapping("/available/{teacherId}")
    public List<AvailabilitySlotDto> getAvailableSlots(@PathVariable Long teacherId) {
        return bookingService.getAvailableSlots(teacherId);
    }
    
    @GetMapping("/check")
    public boolean hasBookings(
    		@RequestParam Long teacherId,
    		@RequestParam String startTime,
    		@RequestParam String endTime) {
    	
    	return bookingService.hasBookings(teacherId, startTime, endTime);
    }
}
