package com.polyparrot.teacherservice.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {
    @Id
    private Long userId;

    @Column(length = 200)
    private String bio;

    @Column(nullable = false)
    private Double pricePerHour;

    @Column(nullable = false)
    private Double rating;

    @ManyToMany
    @JoinTable(
        name = "teacher_teaching_languages",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private List<Language> teachingLanguages;

    @ManyToMany
    @JoinTable(
        name = "teacher_spoken_languages",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private List<Language> spokenLanguages;

    @OneToMany
    @JoinColumn(name = "teacher_id")
    private List<AvailabilitySlot> availabilitySlots;
}