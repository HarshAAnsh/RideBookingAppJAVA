package com.ridebooking.ride_booking_backend.entity;

import com.ridebooking.ride_booking_backend.enums.RideStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
public class Ride {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "passenger_id", nullable = false)
private User passenger;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "driver_id")
private User driver;

@Column(name = "pickup_address", nullable = false, length = 255)
private String pickupAddress;

@Column(name = "destination_address", nullable = false, length = 255)
private String destinationAddress;

@Column(name = "pickup_latitude", nullable = false, precision = 10, scale = 7)
private BigDecimal pickupLatitude;

@Column(name = "pickup_longitude", nullable = false, precision = 10, scale = 7)
private BigDecimal pickupLongitude;

@Column(name = "destination_latitude", nullable = false, precision = 10, scale = 7)
private BigDecimal destinationLatitude;

@Column(name = "destination_longitude", nullable = false, precision = 10, scale = 7)
private BigDecimal destinationLongitude;

@Column(name = "estimated_fare", nullable = false, precision = 10, scale = 2)
private BigDecimal estimatedFare;

@Column(name = "actual_fare", precision = 10, scale = 2)
private BigDecimal actualFare;

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private RideStatus status = RideStatus.REQUESTED;

@Column(name = "requested_at", nullable = false, updatable = false)
private LocalDateTime requestedAt;

@Column(name = "completed_at")
private LocalDateTime completedAt;

public Ride() {
    // Required by JPA.
}

@PrePersist
protected void onCreate() {
    if (requestedAt == null) {
        requestedAt = LocalDateTime.now();
    }

    if (status == null) {
        status = RideStatus.REQUESTED;
    }
}

public Long getId() {
    return id;
}

public User getPassenger() {
    return passenger;
}

public void setPassenger(User passenger) {
    this.passenger = passenger;
}

public User getDriver() {
    return driver;
}

public void setDriver(User driver) {
    this.driver = driver;
}

public String getPickupAddress() {
    return pickupAddress;
}

public void setPickupAddress(String pickupAddress) {
    this.pickupAddress = pickupAddress;
}

public String getDestinationAddress() {
    return destinationAddress;
}

public void setDestinationAddress(String destinationAddress) {
    this.destinationAddress = destinationAddress;
}

public BigDecimal getPickupLatitude() {
    return pickupLatitude;
}

public void setPickupLatitude(BigDecimal pickupLatitude) {
    this.pickupLatitude = pickupLatitude;
}

public BigDecimal getPickupLongitude() {
    return pickupLongitude;
}

public void setPickupLongitude(BigDecimal pickupLongitude) {
    this.pickupLongitude = pickupLongitude;
}

public BigDecimal getDestinationLatitude() {
    return destinationLatitude;
}

public void setDestinationLatitude(BigDecimal destinationLatitude) {
    this.destinationLatitude = destinationLatitude;
}

public BigDecimal getDestinationLongitude() {
    return destinationLongitude;
}

public void setDestinationLongitude(BigDecimal destinationLongitude) {
    this.destinationLongitude = destinationLongitude;
}

public BigDecimal getEstimatedFare() {
    return estimatedFare;
}

public void setEstimatedFare(BigDecimal estimatedFare) {
    this.estimatedFare = estimatedFare;
}

public BigDecimal getActualFare() {
    return actualFare;
}

public void setActualFare(BigDecimal actualFare) {
    this.actualFare = actualFare;
}

public RideStatus getStatus() {
    return status;
}

public void setStatus(RideStatus status) {
    this.status = status;
}

public LocalDateTime getRequestedAt() {
    return requestedAt;
}

public LocalDateTime getCompletedAt() {
    return completedAt;
}

public void setCompletedAt(LocalDateTime completedAt) {
    this.completedAt = completedAt;
}

}