package com.polyparrot.teacherservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polyparrot.teacherservice.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}