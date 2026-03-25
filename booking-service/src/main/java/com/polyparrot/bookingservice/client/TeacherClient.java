package com.polyparrot.bookingservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.polyparrot.bookingservice.dto.AvailabilitySlotDto;

@FeignClient(name = "teacher-service", url = "http://localhost:8081")
public interface TeacherClient {

    @GetMapping("/availability/{teacherId}")
    List<AvailabilitySlotDto> getAvailability(@PathVariable Long teacherId);
}