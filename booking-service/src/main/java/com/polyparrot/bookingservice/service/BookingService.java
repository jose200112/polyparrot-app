package com.polyparrot.bookingservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.polyparrot.bookingservice.client.TeacherClient;
import com.polyparrot.bookingservice.dto.AvailabilitySlotDto;
import com.polyparrot.bookingservice.dto.BookingDto;
import com.polyparrot.bookingservice.dto.BookingResponse;
import com.polyparrot.bookingservice.dto.TeacherSummaryDto;
import com.polyparrot.bookingservice.entity.Booking;
import com.polyparrot.bookingservice.entity.BookingConfirmedEvent;
import com.polyparrot.bookingservice.event.BookingCreatedEvent;
import com.polyparrot.bookingservice.event.BookingEventProducer;
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
	
	private final BookingEventProducer bookingEventProducer;
	
	@Transactional
	public BookingResponse createBooking(Long teacherId, LocalDateTime start, LocalDateTime end) {
		
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
	    booking.setStatus(BookingStatus.PENDING);

	    try {
	        Booking saved = bookingRepository.save(booking);
	        // Publicar evento Kafka
	        bookingEventProducer.publishBookingCreated(new BookingCreatedEvent(
	            saved.getId(),
	            saved.getTeacherId(),
	            saved.getStudentId(),
	            saved.getStartTime(),
	            saved.getEndTime()
	        ));
	        return mapToResponse(saved);
	    } catch (DataIntegrityViolationException e) {
	        throw new SlotAlreadyBookedException();
	    }
	}
	
	public List<BookingResponse> getMyBookings() {
	    AuthenticatedUser user = SecurityUtils.getCurrentUser();
	    Long studentId = user.getUserId();
	    return bookingRepository.findByStudentId(studentId)
	        .stream()
	        .map(this::mapToResponse)
	        .toList();
	}
	
	public BookingResponse cancelBooking(Long id) {
	    Booking booking = bookingRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("Booking not found"));

	    if (booking.getStatus() == BookingStatus.CANCELLED) {
	        throw new RuntimeException("La reserva ya está cancelada");
	    }
	    if (booking.getStartTime().isBefore(LocalDateTime.now())) {
	        throw new RuntimeException("No puedes cancelar una clase que ya ha pasado");
	    }
	    if (booking.getStartTime().isBefore(LocalDateTime.now().plusHours(24))) {
	        throw new RuntimeException("Solo puedes cancelar con al menos 24h de antelación");
	    }

	    booking.setStatus(BookingStatus.CANCELLED);
	    return mapToResponse(bookingRepository.save(booking));
	}
	
	public List<AvailabilitySlotDto> getAvailableSlots(Long teacherId) {

	    // 1. obtener availability (Feign)
	    List<AvailabilitySlotDto> slots = teacherClient.getAvailability(teacherId);

	    // 2. obtener bookings confirmados
	    List<Booking> bookings = bookingRepository
	    	    .findByTeacherIdAndStatusIn(
	    	        teacherId,
	    	        List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)
	    	    );

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
	
	public boolean hasBookings(Long teacherId, String startTime, String endTime) {
	    return bookingRepository.existsByTeacherIdAndStartTimeAndEndTimeAndStatus(
	        teacherId,
	        LocalDateTime.parse(startTime),
	        LocalDateTime.parse(endTime),
	        BookingStatus.CONFIRMED
	    );
	}
	
	public List<Long> getAvailableTeacherIds(String startTime, String endTime) {
	    LocalDateTime start = LocalDateTime.parse(startTime);
	    LocalDateTime end = LocalDateTime.parse(endTime);

	    // Profesores que tienen el slot en availability
	    List<Long> teachersWithSlot = teacherClient.getTeacherIdsBySlot(start, end);

	    // De esos, quitar los que ya tienen reserva confirmada en ese rango
	    return teachersWithSlot.stream()
	        .filter(teacherId -> !bookingRepository
	            .existsByTeacherIdAndStartTimeAndEndTimeAndStatus(
	                teacherId, start, end, BookingStatus.CONFIRMED
	            ))
	        .toList();
	}
	
	public List<BookingResponse> getBookingsByTeacher(Long teacherId) {
	    return bookingRepository
	        .findByTeacherIdAndStatusAndStartTimeAfter(
	            teacherId, BookingStatus.CONFIRMED, LocalDateTime.now()
	        )
	        .stream()
	        .map(this::mapToResponse)
	        .toList();
	}
	
	private BookingResponse mapToResponse(Booking booking) {
	    TeacherSummaryDto teacher = teacherClient.getTeacherSummary(booking.getTeacherId());
	    return BookingResponse.builder()
	        .id(booking.getId())
	        .startTime(booking.getStartTime())
	        .endTime(booking.getEndTime())
	        .status(booking.getStatus().name())
	        .teacherName(teacher.getName())
	        .teacherFirstSurname(teacher.getFirstSurname())
	        .teacherSecondSurname(teacher.getSecondSurname())
	        .build();
	}
	
	public BookingResponse confirmBooking(Long bookingId) {
	    Booking booking = bookingRepository.findById(bookingId)
	        .orElseThrow(() -> new RuntimeException("Booking not found"));

	    if (booking.getStatus() != BookingStatus.PENDING) {
	        throw new RuntimeException("Solo se pueden confirmar reservas pendientes");
	    }

	    booking.setStatus(BookingStatus.CONFIRMED);
	    Booking saved = bookingRepository.save(booking);

	    bookingEventProducer.publishBookingConfirmed(new BookingConfirmedEvent(
	        saved.getId(),
	        saved.getTeacherId(),
	        saved.getStudentId(),
	        saved.getStartTime(),
	        saved.getEndTime()
	    ));

	    return mapToResponse(saved);
	}
	
	public List<BookingDto> getBookingsByTeacherInternal(Long teacherId) {
	    return bookingRepository
	        .findByTeacherIdAndStartTimeAfterAndStatusIn(
	            teacherId,
	            LocalDateTime.now(),
	            List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)
	        )
	        .stream()
	        .map(b -> {
	            BookingDto dto = new BookingDto();
	            dto.setId(b.getId());
	            dto.setStudentId(b.getStudentId());
	            dto.setTeacherId(b.getTeacherId());
	            dto.setStartTime(b.getStartTime());
	            dto.setEndTime(b.getEndTime());
	            dto.setStatus(b.getStatus().name());
	            return dto;
	        })
	        .toList();
	}
}
