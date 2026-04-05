package com.polyparrot.teacherservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.polyparrot.teacherservice.entity.Language;

public interface LanguageRepository extends JpaRepository<Language, Long> {
	
}
