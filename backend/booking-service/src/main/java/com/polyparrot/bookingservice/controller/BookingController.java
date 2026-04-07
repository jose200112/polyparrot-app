package com.polyparrot.bookingservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polyparrot.bookingservice.dto.AvailabilitySlotDto;
import com.polyparrot.bookingservice.dto.BookingDto;
import com.polyparrot.bookingservice.dto.BookingRequest;
import com.polyparrot.bookingservice.dto.BookingResponse;
import com.polyparrot.bookingservice.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
				bookingService.createBooking(request.getTeacherId(), request.getStartTime(), request.getEndTime()));
	}

	@GetMapping("/me")
	public List<BookingResponse> myBookings() {
		return bookingService.getMyBookings();
	}

	@PatchMapping("/{id}/cancel")
	public ResponseEntity<BookingResponse> cancel(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.cancelBooking(id));
	}

	@PatchMapping("/{id}/cancel-by-teacher")
	public ResponseEntity<BookingResponse> cancelByTeacher(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.cancelByTeacher(id));
	}

	@GetMapping("/available/{teacherId}")
	public List<AvailabilitySlotDto> getAvailableSlots(@PathVariable Long teacherId) {
		return bookingService.getAvailableSlots(teacherId);
	}

	@GetMapping("/check")
	public boolean hasBookings(@RequestParam Long teacherId, @RequestParam String startTime,
			@RequestParam String endTime) {

		return bookingService.hasBookings(teacherId, startTime, endTime);
	}

	@GetMapping("/teacher")
	public ResponseEntity<List<BookingDto>> getBookingsByTeacher(@RequestParam Long teacherId) {
		return ResponseEntity.ok(bookingService.getBookingsByTeacherInternal(teacherId));
	}

	@GetMapping("/available-teachers")
	public List<Long> getAvailableTeacherIds(@RequestParam String startTime, @RequestParam String endTime) {
		return bookingService.getAvailableTeacherIds(startTime, endTime);
	}

	@PatchMapping("/{id}/confirm")
	public ResponseEntity<BookingResponse> confirm(@PathVariable Long id) {
		return ResponseEntity.ok(bookingService.confirmBooking(id));
	}

}
