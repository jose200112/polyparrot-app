package com.polyparrot.teacherservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.polyparrot.teacherservice.dto.CreateTeacherRequest;
import com.polyparrot.teacherservice.dto.TeacherBookingResponse;
import com.polyparrot.teacherservice.dto.TeacherResponse;
import com.polyparrot.teacherservice.dto.TeacherSummaryDto;
import com.polyparrot.teacherservice.dto.UpdateTeacherRequest;
import com.polyparrot.teacherservice.entity.Teacher;
import com.polyparrot.teacherservice.service.TeacherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    
    @GetMapping("/search")
    public List<TeacherResponse> searchTeachers(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String teachingLanguage,
            @RequestParam(required = false) String spokenLanguage,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String sortOrder) {
        return teacherService.searchTeachers(minPrice, maxPrice, teachingLanguage, spokenLanguage, startTime, sortOrder);
    }
    
    @GetMapping("/me/bookings/upcoming")
    public ResponseEntity<List<TeacherBookingResponse>> getMyBookings() {
        return ResponseEntity.ok(teacherService.getUpcomingBookings());
    }
    

    @PostMapping
    public ResponseEntity<Void> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        teacherService.createTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED).build(); 
    }
    
    @GetMapping
    public List<TeacherResponse> getTeachers(
            @RequestParam(required = false) String language) {

        return teacherService.getTeachers(language);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherResponseById(id));
    }
    
    
    @PatchMapping("/me")
    public ResponseEntity<TeacherResponse> updateMe(@Valid @RequestBody UpdateTeacherRequest request) {
        return ResponseEntity.ok(teacherService.updateTeacher(request));
    }
    
    @GetMapping("/{id}/summary")
    public TeacherSummaryDto getTeacherSummary(@PathVariable Long id) {
        return teacherService.getTeacherSummary(id);
    }
}

