package com.ridebooking.ride_booking_backend.dto;

import com.ridebooking.ride_booking_backend.enums.RideStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideResponse(
        Long id,
        Long passengerId,
        Long driverId,
        String pickupAddress,
        String destinationAddress,
        BigDecimal pickupLatitude,
        BigDecimal pickupLongitude,
        BigDecimal destinationLatitude,
        BigDecimal destinationLongitude,
        BigDecimal estimatedFare,
        BigDecimal actualFare,
        RideStatus status,
        LocalDateTime requestedAt,
        LocalDateTime completedAt
) {}