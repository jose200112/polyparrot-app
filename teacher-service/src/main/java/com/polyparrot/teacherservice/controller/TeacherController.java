package com.polyparrot.teacherservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.polyparrot.teacherservice.dto.CreateTeacherRequest;
import com.polyparrot.teacherservice.entity.Teacher;
import com.polyparrot.teacherservice.service.TeacherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    public void createTeacher(@RequestBody CreateTeacherRequest request) {
        teacherService.createTeacher(request);
    }
    
    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }
    
    @GetMapping("/{id}")
    public Teacher getTeacher(@PathVariable Long id) {
        return teacherService.getTeacherById(id);
    }
}