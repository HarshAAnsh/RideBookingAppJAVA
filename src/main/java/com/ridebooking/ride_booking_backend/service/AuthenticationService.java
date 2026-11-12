package com.ridebooking.ride_booking_backend.service;

import com.ridebooking.ride_booking_backend.dto.AuthResponse;
import com.ridebooking.ride_booking_backend.dto.LoginRequest;
import com.ridebooking.ride_booking_backend.dto.UserResponse;
import com.ridebooking.ride_booking_backend.entity.User;
import com.ridebooking.ride_booking_backend.repository.UserRepository;
import com.ridebooking.ride_booking_backend.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse login(LoginRequest request) {

        String email = request.getEmail().trim();

        // Find the registered user.
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password"));

        // Compare the submitted password with the stored BCrypt hash.
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Load Spring Security user details.
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        // Generate the JWT access token.
        String token = jwtService.generateToken(userDetails);

        // Build a response without exposing the password.
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole().name());
        userResponse.setCreatedAt(user.getCreatedAt());

        return new AuthResponse(
                token,
                "Bearer",
                userResponse
        );
    }
}