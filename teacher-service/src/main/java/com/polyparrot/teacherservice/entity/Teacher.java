package com.polyparrot.teacherservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    @Id
    private Long userId;

    private String bio;

    private Double pricePerHour;

    private Double rating;
}