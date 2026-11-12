package com.ridebooking.ride_booking_backend.security;

import com.ridebooking.ride_booking_backend.entity.User;
import com.ridebooking.ride_booking_backend.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

private final UserRepository userRepository;

public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
}

@Override
public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {

    User user = userRepository.findByEmailIgnoreCase(email.trim())
            .orElseThrow(() ->
                    new UsernameNotFoundException("Invalid email or password"));

    return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            )
    );
}

}