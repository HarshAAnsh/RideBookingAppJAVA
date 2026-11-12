package com.ridebooking.ride_booking_backend.controller;

import com.ridebooking.ride_booking_backend.dto.RideRequest;
import com.ridebooking.ride_booking_backend.dto.RideResponse;
import com.ridebooking.ride_booking_backend.service.RideService;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideResponse> bookRide(
            @Valid @RequestBody RideRequest request,
            Authentication authentication) {

        RideResponse response = rideService.bookRide(
                authentication.getName(),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/my-rides")
public ResponseEntity<List<RideResponse>> getMyRides(
Authentication authentication) {

List<RideResponse> rides =
        rideService.getMyRides(authentication.getName());

return ResponseEntity.ok(rides);

}
@GetMapping("/available")
public ResponseEntity<List<RideResponse>> getAvailableRides(
Authentication authentication) {

List<RideResponse> rides =
        rideService.getAvailableRides(authentication.getName());

return ResponseEntity.ok(rides);

}
@PatchMapping("/{rideId}/accept")
public ResponseEntity<RideResponse> acceptRide(
        @PathVariable Long rideId,
        Authentication authentication) {

    RideResponse response =
            rideService.acceptRide(authentication.getName(), rideId);

    return ResponseEntity.ok(response);
}
@PatchMapping("/{rideId}/start")
public ResponseEntity<RideResponse> startRide(
        @PathVariable Long rideId,
        Authentication authentication) {

    RideResponse response = rideService.startRide(
            authentication.getName(), rideId);

    return ResponseEntity.ok(response);
}

@PatchMapping("/{rideId}/complete")
public ResponseEntity<RideResponse> completeRide(
        @PathVariable Long rideId,
        Authentication authentication) {

    RideResponse response = rideService.completeRide(
            authentication.getName(), rideId);

    return ResponseEntity.ok(response);
}
@GetMapping("/driver/my-rides")
public ResponseEntity<java.util.List<RideResponse>> getDriverRides(
        Authentication authentication) {

    return ResponseEntity.ok(
            rideService.getDriverRides(authentication.getName()));
}
@PatchMapping("/{rideId}/cancel")
public ResponseEntity<RideResponse> cancelRide(
        @PathVariable Long rideId,
        Authentication authentication) {

    RideResponse response = rideService.cancelRide(
            authentication.getName(), rideId);

    return ResponseEntity.ok(response);
}
}