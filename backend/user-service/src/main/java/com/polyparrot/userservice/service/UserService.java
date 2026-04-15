package com.polyparrot.userservice.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.polyparrot.userservice.dto.UpdateUserRequest;
import com.polyparrot.userservice.dto.UserDto;
import com.polyparrot.userservice.dto.UserResponse;
import com.polyparrot.userservice.entity.User;
import com.polyparrot.userservice.exception.UserNotFoundException;
import com.polyparrot.userservice.repository.UserRepository;
import com.polyparrot.userservice.security.AuthenticatedUser;
import com.polyparrot.userservice.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getCurrentUser() {
        AuthenticatedUser caller = SecurityUtils.getCurrentUser(); // ← consistente
        User user = userRepository.findById(caller.getUserId())
            .orElseThrow(UserNotFoundException::new);
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
		User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
		return UserDto.builder()
				.id(user.getId())
				.name(user.getName())
				.firstSurname(user.getFirstSurname())
				.secondSurname(user.getSecondSurname()).build();
	}
	

	public UserResponse updateCurrentUser(UpdateUserRequest request) {
	    AuthenticatedUser caller = SecurityUtils.getCurrentUser();
	    User user = userRepository.findById(caller.getUserId())
	        .orElseThrow(UserNotFoundException::new);

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