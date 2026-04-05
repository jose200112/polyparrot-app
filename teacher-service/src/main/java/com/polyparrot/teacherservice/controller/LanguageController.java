package com.polyparrot.teacherservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polyparrot.teacherservice.dto.LanguageResponse;
import com.polyparrot.teacherservice.service.LanguageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/languages")
public class LanguageController {
	
	private final LanguageService languageService;

	@GetMapping
	public List<LanguageResponse> getAllLanguages() {
		return languageService.getLanguages();
	}
}
