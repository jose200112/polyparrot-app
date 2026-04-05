package com.polyparrot.teacherservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherSummaryDto {
    private Long id;
    private String name;
    private String firstSurname;
    private String secondSurname;
}
