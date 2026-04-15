package com.polyparrot.teacherservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.polyparrot.teacherservice.client.BookingClient;
import com.polyparrot.teacherservice.dto.AvailabilitySlotResponse;
import com.polyparrot.teacherservice.entity.AvailabilitySlot;
import com.polyparrot.teacherservice.exception.CannotDeleteSlotException;
import com.polyparrot.teacherservice.exception.SlotAlreadyExistsException;
import com.polyparrot.teacherservice.exception.SlotInPastException;
import com.polyparrot.teacherservice.exception.SlotNotFoundException;
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
            throw new SlotInPastException();
        }
        boolean exists = availabilityRepository.existsByTeacherIdAndStartTime(teacherId, start);
        if (exists) {
            throw new SlotAlreadyExistsException();
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
                .orElseThrow(SlotNotFoundException::new);

        AuthenticatedUser caller = SecurityUtils.getCurrentUser();
        if (!slot.getTeacherId().equals(caller.getUserId())) {
        	throw new AccessDeniedException("No puedes eliminar slots de otro profesor");
        }

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
