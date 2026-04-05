package com.polyparrot.teacherservice.dto;

import java.util.List;

import com.polyparrot.teacherservice.entity.Language;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherResponse {

    private Long id;
    private String name;
    private String firstSurname;
    private String secondSurname;

    private String bio;
    private Double pricePerHour;
    private Double rating;
	private List<Language> teachingLanguages;
    private List<Language> spokenLanguages;
}