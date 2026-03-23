package com.polyparrot.teacherservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.polyparrot.teacherservice.entity.AvailabilitySlot;
import com.polyparrot.teacherservice.repository.AvailabilityRepository;
import com.polyparrot.teacherservice.security.AuthenticatedUser;
import com.polyparrot.teacherservice.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
	
	private final AvailabilityRepository availabilityRepository;
	
	public AvailabilitySlot createSlot(LocalDateTime start, LocalDateTime end) {

	    AuthenticatedUser user = SecurityUtils.getCurrentUser();
	    Long teacherId = user.getUserId();

	    if (end.isBefore(start) || end.equals(start)) {
	        throw new RuntimeException("Invalid time range");
	    }

	    if (start.isBefore(LocalDateTime.now())) {
	        throw new RuntimeException("Cannot create slots in the past");
	    }

	    boolean existsExact = availabilityRepository
	        .existsByTeacherIdAndStartTimeAndEndTime(teacherId, start, end);

	    if (existsExact) {
	        throw new RuntimeException("Slot already exists");
	    }

	    boolean overlap = availabilityRepository
	        .existsByTeacherIdAndStartTimeLessThanAndEndTimeGreaterThan(
	            teacherId, end, start
	        );

	    if (overlap) {
	        throw new RuntimeException("Slot overlaps with existing one");
	    }

	    AvailabilitySlot slot = new AvailabilitySlot();
	    slot.setTeacherId(teacherId);
	    slot.setStartTime(start);
	    slot.setEndTime(end);

	    return availabilityRepository.save(slot);
	}
	
	public List<AvailabilitySlot> getSlotsByTeacher(Long teacherId) {
	    return availabilityRepository.findByTeacherId(teacherId);
	}
}
