package com.polyparrot.bookingservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.polyparrot.bookingservice.client.TeacherClient;
import com.polyparrot.bookingservice.dto.AvailabilitySlotDto;
import com.polyparrot.bookingservice.entity.Booking;
import com.polyparrot.bookingservice.exception.InvalidBookingTimeException;
import com.polyparrot.bookingservice.exception.SlotAlreadyBookedException;
import com.polyparrot.bookingservice.exception.SlotNotAvailableException;
import com.polyparrot.bookingservice.model.BookingStatus;
import com.polyparrot.bookingservice.repository.BookingRepository;
import com.polyparrot.bookingservice.security.AuthenticatedUser;
import com.polyparrot.bookingservice.security.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
	
	private final BookingRepository bookingRepository;
	
	private final TeacherClient teacherClient;
	
	@Transactional
	public Booking createBooking(Long teacherId, LocalDateTime start, LocalDateTime end) {
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime minTime = now.plusHours(24);

		if (start.isBefore(minTime)) {
		    throw new InvalidBookingTimeException();
		}

	    AuthenticatedUser user = SecurityUtils.getCurrentUser();
	    Long studentId = user.getUserId();

	    List<Booking> overlaps = bookingRepository
	        .findOverlappingBookingsForUpdate(teacherId, start, end);

	    if (!overlaps.isEmpty()) {
	        throw new SlotAlreadyBookedException();
	    }

	    List<AvailabilitySlotDto> slots = teacherClient.getAvailability(teacherId);

	    boolean exists = slots.stream()
	        .anyMatch(slot ->
	            slot.getStartTime().equals(start) &&
	            slot.getEndTime().equals(end)
	        );

	    if (!exists) {
	        throw new SlotNotAvailableException();
	    }

	    Booking booking = new Booking();
	    booking.setStudentId(studentId);
	    booking.setTeacherId(teacherId);
	    booking.setStartTime(start);
	    booking.setEndTime(end);
	    booking.setStatus(BookingStatus.CONFIRMED);
	    
        try {
    	    return bookingRepository.save(booking);
        } catch (DataIntegrityViolationException e) {
            throw new SlotAlreadyBookedException();
        }
	}
	
	public List<Booking> getMyBookings() {

	    AuthenticatedUser user = SecurityUtils.getCurrentUser();
	    Long studentId = user.getUserId();

	    return bookingRepository.findByStudentId(studentId);
	}
	
	public Booking cancelBooking(Long id) {

	    Booking booking = bookingRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Booking not found"));

	    booking.setStatus(BookingStatus.CANCELLED);

	    return bookingRepository.save(booking);
	}
	
	public List<AvailabilitySlotDto> getAvailableSlots(Long teacherId) {

	    // 1. obtener availability (Feign)
	    List<AvailabilitySlotDto> slots = teacherClient.getAvailability(teacherId);

	    // 2. obtener bookings confirmados
	    List<Booking> bookings = bookingRepository
	        .findByTeacherIdAndStatus(teacherId, BookingStatus.CONFIRMED);

	    // 3. calcular tiempo mínimo (24h)
	    LocalDateTime minTime = LocalDateTime.now().plusHours(24);

	    // 4. filtrar
	    return slots.stream()
	        // solo slots futuros + margen
	        .filter(slot -> slot.getStartTime().isAfter(minTime))

	        // quitar slots ya reservados
	        .filter(slot -> bookings.stream().noneMatch(b ->
	            overlaps(slot, b)
	        ))
	        .toList();
	}
	
	private boolean overlaps(AvailabilitySlotDto slot, Booking booking) {
	    return slot.getStartTime().isBefore(booking.getEndTime()) &&
	           slot.getEndTime().isAfter(booking.getStartTime());
	}
	
	@GetMapping("/bookings/check")
	public boolean hasBookings(
	    @RequestParam Long teacherId,
	    @RequestParam String startTime,
	    @RequestParam String endTime
	) {
	    return bookingRepository.existsByTeacherIdAndStartTimeAndEndTimeAndStatus(
	        teacherId,
	        LocalDateTime.parse(startTime),
	        LocalDateTime.parse(endTime),
	        BookingStatus.CONFIRMED
	    );
	}
}
