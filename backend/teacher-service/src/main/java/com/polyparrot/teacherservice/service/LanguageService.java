package com.polyparrot.teacherservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.polyparrot.teacherservice.dto.LanguageResponse;
import com.polyparrot.teacherservice.repository.LanguageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LanguageService {
	
    private final LanguageRepository languageRepository;

	public List<LanguageResponse> getLanguages(){
		return languageRepository.findAll()
		.stream()
		.map(lang -> new LanguageResponse(
				lang.getId(),
				lang.getName()
				))
		.toList();
	}
}
