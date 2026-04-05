package com.polyparrot.teacherservice.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.polyparrot.teacherservice.dto.AvailabilityRequest;
import com.polyparrot.teacherservice.dto.AvailabilitySlotResponse;
import com.polyparrot.teacherservice.service.AvailabilityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/availability")
    public ResponseEntity<AvailabilitySlotResponse> createSlot(@Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(availabilityService.createSlot(request.getStartTime()));
    }

    @GetMapping("/availability/{teacherId}")
    public ResponseEntity<List<AvailabilitySlotResponse>> getSlots(@PathVariable Long teacherId) {
        return ResponseEntity.ok(availabilityService.getSlotsByTeacher(teacherId));
    }

    @DeleteMapping("/availability/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) {
        availabilityService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/availability/by-slot")
    public List<Long> getTeacherIdsBySlot(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        return availabilityService.getTeacherIdsBySlot(startTime, endTime);
    }
}