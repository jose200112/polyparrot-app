package com.polyparrot.teacherservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.polyparrot.teacherservice.dto.AvailabilityRequest;
import com.polyparrot.teacherservice.entity.AvailabilitySlot;
import com.polyparrot.teacherservice.service.AvailabilityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/availability")
    public AvailabilitySlot createSlot(@RequestBody AvailabilityRequest request) {
        return availabilityService.createSlot(
            request.getStartTime(),
            request.getEndTime()
        );
    }
    
    @GetMapping("/availability/{teacherId}")
    public List<AvailabilitySlot> getSlots(@PathVariable Long teacherId) {
        return availabilityService.getSlotsByTeacher(teacherId);
    }
}