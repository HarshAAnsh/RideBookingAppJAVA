package com.ridebooking.ride_booking_backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RideRequest(

        @NotBlank(message = "Pickup address is required")
        @Size(max = 255, message = "Pickup address must not exceed 255 characters")
        String pickupAddress,

        @NotBlank(message = "Destination address is required")
        @Size(max = 255, message = "Destination address must not exceed 255 characters")
        String destinationAddress,

        @NotNull(message = "Pickup latitude is required")
        @DecimalMin(value = "-90.0", message = "Pickup latitude must be at least -90")
        @DecimalMax(value = "90.0", message = "Pickup latitude must not exceed 90")
        BigDecimal pickupLatitude,

        @NotNull(message = "Pickup longitude is required")
        @DecimalMin(value = "-180.0", message = "Pickup longitude must be at least -180")
        @DecimalMax(value = "180.0", message = "Pickup longitude must not exceed 180")
        BigDecimal pickupLongitude,

        @NotNull(message = "Destination latitude is required")
        @DecimalMin(value = "-90.0", message = "Destination latitude must be at least -90")
        @DecimalMax(value = "90.0", message = "Destination latitude must not exceed 90")
        BigDecimal destinationLatitude,

        @NotNull(message = "Destination longitude is required")
        @DecimalMin(value = "-180.0", message = "Destination longitude must be at least -180")
        @DecimalMax(value = "180.0", message = "Destination longitude must not exceed 180")
        BigDecimal destinationLongitude

) {}