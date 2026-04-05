package com.polyparrot.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String firstSurname;
    private String secondSurname;
}