package com.polyparrot.teacherservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.polyparrot.teacherservice.client.BookingClient;
import com.polyparrot.teacherservice.dto.AvailabilitySlotResponse;
import com.polyparrot.teacherservice.entity.AvailabilitySlot;
import com.polyparrot.teacherservice.exception.CannotDeleteSlotException;
import com.polyparrot.teacherservice.repository.AvailabilityRepository;
import com.polyparrot.teacherservice.security.AuthenticatedUser;
import com.polyparrot.teacherservice.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final BookingClient bookingClient;

    public AvailabilitySlotResponse createSlot(LocalDateTime start) {
        AuthenticatedUser user = SecurityUtils.getCurrentUser();
        Long teacherId = user.getUserId();

        if (start.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No puedes crear slots en el pasado");
        }
        boolean exists = availabilityRepository.existsByTeacherIdAndStartTime(teacherId, start);
        if (exists) {
            throw new RuntimeException("Ya existe un slot en ese horario");
        }

        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setTeacherId(teacherId);
        slot.setStartTime(start);
        return mapToResponse(availabilityRepository.save(slot));
    }

    public List<AvailabilitySlotResponse> getSlotsByTeacher(Long teacherId) {
        return availabilityRepository.findByTeacherId(teacherId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    public void deleteSlot(Long slotId) {
        AvailabilitySlot slot = availabilityRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Slot not found"));

        LocalDateTime endTime = slot.getStartTime().plusHours(1);

        // Usar formato explícito en vez de toString()
        String start = slot.getStartTime().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String end = endTime.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        boolean hasBookings = bookingClient.hasBookings(
            slot.getTeacherId(),
            start,
            end
        );

        if (hasBookings) {
            throw new CannotDeleteSlotException();
        }

        availabilityRepository.delete(slot);
    }
	
    public List<Long> getTeacherIdsBySlot(LocalDateTime startTime, LocalDateTime endTime) {
        return availabilityRepository.findTeacherIdsByStartTime(startTime);
    }
    
    private AvailabilitySlotResponse mapToResponse(AvailabilitySlot slot) {
        return AvailabilitySlotResponse.builder()
            .id(slot.getId())
            .teacherId(slot.getTeacherId())
            .startTime(slot.getStartTime())
            .build();
    }
}
