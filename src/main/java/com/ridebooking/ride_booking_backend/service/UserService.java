package com.ridebooking.ride_booking_backend.service;

import com.ridebooking.ride_booking_backend.dto.RegisterRequest;
import com.ridebooking.ride_booking_backend.dto.UserResponse;
import com.ridebooking.ride_booking_backend.entity.User;
import com.ridebooking.ride_booking_backend.entity.UserRole;
import com.ridebooking.ride_booking_backend.exception.EmailAlreadyExistsException;
import com.ridebooking.ride_booking_backend.repository.UserRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {

        String normalizedEmail = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyExistsException(
                    "An account with this email already exists"
            );
        }

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(request.getPhone().trim());

        // Hash the password before storing it.
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Public registration always creates a passenger account.
        user.setRole(UserRole.PASSENGER);

        try {
            user = userRepository.saveAndFlush(user);
            return toUserResponse(user);
        } catch (DataIntegrityViolationException exception) {

            // Handle a duplicate email caused by concurrent requests.
            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                throw new EmailAlreadyExistsException(
                        "An account with this email already exists"
                );
            }

            throw exception;
        }
    }

private UserResponse toUserResponse(User user) {
    return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getRole().name(),
            user.getCreatedAt()
    );
}
public UserResponse getUserByEmail(String email) {
    User user = userRepository.findByEmailIgnoreCase(email.trim())
            .orElseThrow(() ->
                    new UsernameNotFoundException("User not found"));

    return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getRole().name(),
            user.getCreatedAt()
    );
}
}