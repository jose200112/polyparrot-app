package com.polyparrot.bookingservice.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.polyparrot.bookingservice.dto.AvailabilitySlotDto;
import com.polyparrot.bookingservice.dto.TeacherSummaryDto;

@FeignClient(name = "teacher-service", url = "${teacher-service.url}")
public interface TeacherClient {

    @GetMapping("/availability/{teacherId}")
    List<AvailabilitySlotDto> getAvailability(@PathVariable Long teacherId);

    @GetMapping("/availability/by-slot")
    List<Long> getTeacherIdsBySlot(
        @RequestParam LocalDateTime startTime,
        @RequestParam LocalDateTime endTime
    );
    
    @GetMapping("/teachers/{id}/summary")
    TeacherSummaryDto getTeacherSummary(@PathVariable Long id);
}