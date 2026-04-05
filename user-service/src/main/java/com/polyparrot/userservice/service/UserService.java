package com.polyparrot.userservice.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.polyparrot.userservice.dto.UpdateUserRequest;
import com.polyparrot.userservice.dto.UserDto;
import com.polyparrot.userservice.dto.UserResponse;
import com.polyparrot.userservice.entity.User;
import com.polyparrot.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .firstSurname(user.getFirstSurname())   
                .secondSurname(user.getSecondSurname()) 
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
    
	public UserDto getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		return UserDto.builder()
				.id(user.getId())
				.name(user.getName())
				.firstSurname(user.getFirstSurname())
				.secondSurname(user.getSecondSurname()).build();
	}
	

	public UserResponse updateCurrentUser(UpdateUserRequest request) {
	    String email = SecurityContextHolder.getContext().getAuthentication().getName();
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    if (request.getName() != null) user.setName(request.getName());
	    if (request.getFirstSurname() != null) user.setFirstSurname(request.getFirstSurname());
	    if (request.getSecondSurname() != null) user.setSecondSurname(request.getSecondSurname());

	    userRepository.save(user);

	    return UserResponse.builder()
	            .id(user.getId())
	            .name(user.getName())
	            .firstSurname(user.getFirstSurname())
	            .secondSurname(user.getSecondSurname())
	            .email(user.getEmail())
	            .role(user.getRole().name())
	            .build();
	}
}