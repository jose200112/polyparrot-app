package com.polyparrot.bookingservice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.polyparrot.bookingservice.entity.Booking;
import com.polyparrot.bookingservice.model.BookingStatus;

import jakarta.persistence.LockModeType;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
	    SELECT b FROM Booking b
	    WHERE b.teacherId = :teacherId
	    AND b.startTime < :end
	    AND b.endTime > :start
	    AND b.status IN ('CONFIRMED', 'PENDING')
	""")
	List<Booking> findOverlappingBookingsForUpdate(
	    Long teacherId,
	    LocalDateTime start,
	    LocalDateTime end
	);

    List<Booking> findByStudentId(Long studentId);
    
    List<Booking> findByTeacherIdAndStatus(Long teacherId, BookingStatus status);

	boolean existsByTeacherIdAndStartTimeAndEndTimeAndStatus(Long teacherId, LocalDateTime localDateTime,
			LocalDateTime localDateTime2, BookingStatus confirmed);
	
	List<Booking> findByTeacherIdAndStatusAndStartTimeAfter(
		    Long teacherId,
		    BookingStatus status,
		    LocalDateTime after
		);
	
	List<Booking> findByTeacherIdAndStartTimeAfterAndStatusIn(
		    Long teacherId,
		    LocalDateTime after,
		    List<BookingStatus> statuses
		);
	
	List<Booking> findByTeacherIdAndStatusIn(Long teacherId, List<BookingStatus> statuses);
	
	boolean existsByStudentIdAndStartTimeAndStatusIn(
	    Long studentId,
	    LocalDateTime startTime,
	    List<BookingStatus> statuses
	);
}