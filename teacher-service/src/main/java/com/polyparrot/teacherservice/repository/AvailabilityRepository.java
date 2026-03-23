package com.polyparrot.teacherservice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polyparrot.teacherservice.entity.AvailabilitySlot;

public interface AvailabilityRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByTeacherId(Long teacherId);
    
    boolean existsByTeacherIdAndStartTimeAndEndTime(
    	    Long teacherId,
    	    LocalDateTime startTime,
    	    LocalDateTime endTime
    	);
    
    boolean existsByTeacherIdAndStartTimeLessThanAndEndTimeGreaterThan(
    	    Long teacherId,
    	    LocalDateTime end,
    	    LocalDateTime start
    	);
}