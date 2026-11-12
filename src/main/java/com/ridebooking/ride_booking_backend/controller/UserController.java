package com.ridebooking.ride_booking_backend.controller;

import com.ridebooking.ride_booking_backend.dto.RegisterRequest;
import com.ridebooking.ride_booking_backend.dto.UserResponse;
import com.ridebooking.ride_booking_backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request
    ) {
        UserResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
public ResponseEntity<UserResponse> getCurrentUser(
        Authentication authentication) {

    String email = authentication.getName();

    UserResponse user = userService.getUserByEmail(email);

    return ResponseEntity.ok(user);
}
}