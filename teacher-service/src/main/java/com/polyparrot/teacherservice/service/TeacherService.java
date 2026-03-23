package com.polyparrot.teacherservice.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.polyparrot.teacherservice.dto.CreateTeacherRequest;
import com.polyparrot.teacherservice.entity.Teacher;
import com.polyparrot.teacherservice.repository.TeacherRepository;
import com.polyparrot.teacherservice.security.AuthenticatedUser;
import com.polyparrot.teacherservice.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public void createTeacher(CreateTeacherRequest request) {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long userId = SecurityUtils.getCurrentUser().getUserId();

        if (teacherRepository.existsById(userId)) {
            throw new RuntimeException("Teacher already exists");
        }

        Teacher teacher = Teacher.builder()
                .userId(userId)
                .bio(request.getBio())
                .pricePerHour(request.getPricePerHour())
                .rating(0.0)
                .build();

        teacherRepository.save(teacher);
    }
    
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }
    
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }
}