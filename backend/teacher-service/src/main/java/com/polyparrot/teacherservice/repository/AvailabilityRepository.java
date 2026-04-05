package com.polyparrot.teacherservice.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.polyparrot.teacherservice.entity.AvailabilitySlot;

public interface AvailabilityRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByTeacherId(Long teacherId);
    
    boolean existsByTeacherIdAndStartTime(
    		Long teacherId,
    		LocalDateTime start
    		);
    
    @Query("SELECT a.teacherId FROM AvailabilitySlot a WHERE a.startTime = :startTime")
    List<Long> findTeacherIdsByStartTime(@Param("startTime") LocalDateTime startTime);
}