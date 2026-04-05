package com.polyparrot.teacherservice.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String firstSurname;
    private String secondSurname;
}