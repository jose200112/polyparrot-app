package com.polyparrot.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.polyparrot.userservice.dto.UpdateUserRequest;
import com.polyparrot.userservice.dto.UserDto;
import com.polyparrot.userservice.dto.UserResponse;
import com.polyparrot.userservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe() {
        return userService.getCurrentUser();
    }
    
	@GetMapping("/{id}")
	public UserDto getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}
	
	@PatchMapping("/me")
	public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest request) {
	    return ResponseEntity.ok(userService.updateCurrentUser(request));
	}
}