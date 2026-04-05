package com.polyparrot.teacherservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.polyparrot.teacherservice.client.BookingClient;
import com.polyparrot.teacherservice.client.UserClient;
import com.polyparrot.teacherservice.dto.BookingDto;
import com.polyparrot.teacherservice.dto.CreateTeacherRequest;
import com.polyparrot.teacherservice.dto.TeacherBookingResponse;
import com.polyparrot.teacherservice.dto.TeacherResponse;
import com.polyparrot.teacherservice.dto.TeacherSummaryDto;
import com.polyparrot.teacherservice.dto.UpdateTeacherRequest;
import com.polyparrot.teacherservice.dto.UserDto;
import com.polyparrot.teacherservice.entity.Language;
import com.polyparrot.teacherservice.entity.Teacher;
import com.polyparrot.teacherservice.repository.LanguageRepository;
import com.polyparrot.teacherservice.repository.TeacherRepository;
import com.polyparrot.teacherservice.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    
    private final LanguageRepository languageRepository;
    
    private final UserClient userClient;
    
    private final BookingClient bookingClient;

    public void createTeacher(CreateTeacherRequest request) {

        Long userId = SecurityUtils.getCurrentUser().getUserId();

        Teacher teacher = Teacher.builder()
            .userId(userId)
            .bio(request.getBio())
            .pricePerHour(request.getPricePerHour())
            .rating(null)
            .build();

        teacherRepository.save(teacher);

        if (request.getTeachingLanguageIds() != null) {
            List<Language> teaching = languageRepository
                .findAllById(request.getTeachingLanguageIds());

            teacher.setTeachingLanguages(teaching);
        }

        if (request.getSpokenLanguageIds() != null) {
            List<Language> spoken = languageRepository
                .findAllById(request.getSpokenLanguageIds());

            teacher.setSpokenLanguages(spoken);
        }

        teacherRepository.save(teacher);
    }
    
    public List<TeacherResponse> getTeachers(String language) {

        List<Teacher> teachers;

        if (language != null) {
            teachers = teacherRepository.findByTeachingLanguages_Name(language);
        } else {
            teachers = teacherRepository.findAll();
        }

        return teachers.stream()
        		.map(this::mapToResponse)
        		.toList();
    }
    
    public TeacherResponse mapToResponse(Teacher teacher) {
        UserDto user = userClient.getUserById(teacher.getUserId());

        return TeacherResponse.builder()
                .id(teacher.getUserId())
                .name(user.getName())
                .firstSurname(user.getFirstSurname())
                .secondSurname(user.getSecondSurname())
                .bio(teacher.getBio())
                .pricePerHour(teacher.getPricePerHour())
                .rating(teacher.getRating())
                .spokenLanguages(teacher.getSpokenLanguages())
                .teachingLanguages(teacher.getTeachingLanguages())
                .build();
    }
    
    public List<TeacherResponse> searchTeachers(
            Double minPrice, Double maxPrice,
            String teachingLanguage, String spokenLanguage,
            String startTime, String sortOrder) {

        List<Teacher> teachers = teacherRepository.searchTeachers(
            minPrice, maxPrice, teachingLanguage, spokenLanguage
        );

        // Filtrar por disponibilidad si se especificó
        if (startTime != null && !startTime.isEmpty()) {
            String endTime = LocalDateTime.parse(startTime)
                .plusHours(1)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            List<Long> availableIds = bookingClient.getAvailableTeacherIds(startTime, endTime);
            teachers = teachers.stream()
                .filter(t -> availableIds.contains(t.getUserId()))
                .toList();
        }

        // Ordenar por precio
        if ("asc".equalsIgnoreCase(sortOrder)) {
            teachers = teachers.stream()
                .sorted(Comparator.comparingDouble(Teacher::getPricePerHour))
                .toList();
        } else if ("desc".equalsIgnoreCase(sortOrder)) {
            teachers = teachers.stream()
                .sorted(Comparator.comparingDouble(Teacher::getPricePerHour).reversed())
                .toList();
        }

        return teachers.stream().map(this::mapToResponse).toList();
    }
    
    
    public List<TeacherBookingResponse> getUpcomingBookings() {
        Long teacherId = SecurityUtils.getCurrentUser().getUserId();

        List<BookingDto> bookings = bookingClient.getBookingsByTeacher(teacherId);

        return bookings.stream().map(booking -> {
            UserDto student = userClient.getUserById(booking.getStudentId());
            return TeacherBookingResponse.builder()
                .bookingId(booking.getId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .studentName(student.getName())
                .studentFirstSurname(student.getFirstSurname())
                .studentSecondSurname(student.getSecondSurname())
                .status(booking.getStatus())
                .build();
        }).toList();
    }
    
    
    public TeacherResponse updateTeacher(UpdateTeacherRequest request) {
        Long userId = SecurityUtils.getCurrentUser().getUserId();

        Teacher teacher = teacherRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (request.getBio() != null) teacher.setBio(request.getBio());
        if (request.getPricePerHour() != null) teacher.setPricePerHour(request.getPricePerHour());

        if (request.getTeachingLanguageIds() != null) {
            List<Language> teaching = languageRepository.findAllById(request.getTeachingLanguageIds());
            teacher.setTeachingLanguages(teaching);
        }

        if (request.getSpokenLanguageIds() != null) {
            List<Language> spoken = languageRepository.findAllById(request.getSpokenLanguageIds());
            teacher.setSpokenLanguages(spoken);
        }

        teacherRepository.save(teacher);
        return mapToResponse(teacher);
    }
    
    public TeacherSummaryDto getTeacherSummary(Long teacherId) {
        UserDto user = userClient.getUserById(teacherId);
        return TeacherSummaryDto.builder()
            .id(teacherId)
            .name(user.getName())
            .firstSurname(user.getFirstSurname())
            .secondSurname(user.getSecondSurname())
            .build();
    }
    
    public TeacherResponse getTeacherResponseById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return mapToResponse(teacher);
    }
}