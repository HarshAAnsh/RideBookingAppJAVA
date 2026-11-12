package com.ridebooking.ride_booking_backend.service;

import com.ridebooking.ride_booking_backend.dto.RideRequest;
import com.ridebooking.ride_booking_backend.dto.RideResponse;
import com.ridebooking.ride_booking_backend.entity.Ride;
import com.ridebooking.ride_booking_backend.entity.User;
import com.ridebooking.ride_booking_backend.enums.RideStatus;
import com.ridebooking.ride_booking_backend.repository.RideRepository;
import com.ridebooking.ride_booking_backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final FareService fareService;

    public RideService(
            RideRepository rideRepository,
            UserRepository userRepository,
            FareService fareService) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.fareService = fareService;
    }

    @Transactional
    public RideResponse bookRide(String passengerEmail, RideRequest request) {

        User passenger = userRepository.findByEmailIgnoreCase(passengerEmail.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Passenger not found"));

        if (passenger.getRole() == null
                || !passenger.getRole().name().equals("PASSENGER")) {
            throw new AccessDeniedException(
                    "Only passengers can book rides");
        }

        BigDecimal estimatedFare = fareService.calculateEstimatedFare(
                request.pickupLatitude(),
                request.pickupLongitude(),
                request.destinationLatitude(),
                request.destinationLongitude()
        );

        Ride ride = new Ride();
        ride.setPassenger(passenger);
        ride.setPickupAddress(request.pickupAddress().trim());
        ride.setDestinationAddress(request.destinationAddress().trim());
        ride.setPickupLatitude(request.pickupLatitude());
        ride.setPickupLongitude(request.pickupLongitude());
        ride.setDestinationLatitude(request.destinationLatitude());
        ride.setDestinationLongitude(request.destinationLongitude());
        ride.setEstimatedFare(estimatedFare);
        ride.setStatus(RideStatus.REQUESTED);

        Ride savedRide = rideRepository.save(ride);

        return toResponse(savedRide);
    }

    private RideResponse toResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getPassenger().getId(),
                ride.getDriver() == null ? null : ride.getDriver().getId(),
                ride.getPickupAddress(),
                ride.getDestinationAddress(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude(),
                ride.getDestinationLatitude(),
                ride.getDestinationLongitude(),
                ride.getEstimatedFare(),
                ride.getActualFare(),
                ride.getStatus(),
                ride.getRequestedAt(),
                ride.getCompletedAt()
        );
    }
    @Transactional(readOnly = true)
public java.util.List<RideResponse> getMyRides(String passengerEmail) {
User passenger = userRepository.findByEmailIgnoreCase(passengerEmail.trim())
.orElseThrow(() -> new ResponseStatusException(
HttpStatus.NOT_FOUND, "Passenger not found"));

if (passenger.getRole() == null
        || !passenger.getRole().name().equals("PASSENGER")) {
    throw new AccessDeniedException("Only passengers can view passenger ride history");
}

return rideRepository.findByPassengerIdOrderByRequestedAtDesc(passenger.getId())
        .stream()
        .map(this::toResponse)
        .toList();

}
@Transactional(readOnly = true)
public java.util.List<RideResponse> getAvailableRides(String driverEmail) {
User driver = userRepository.findByEmailIgnoreCase(driverEmail.trim())
.orElseThrow(() -> new ResponseStatusException(
HttpStatus.NOT_FOUND, "Driver not found"));

if (driver.getRole() == null
        || !driver.getRole().name().equals("DRIVER")) {
    throw new AccessDeniedException(
            "Only drivers can view available rides");
}

return rideRepository
        .findByStatusOrderByRequestedAtAsc(RideStatus.REQUESTED)
        .stream()
        .map(this::toResponse)
        .toList();

}
@Transactional
public RideResponse acceptRide(String driverEmail, Long rideId) {
    User driver = userRepository.findByEmailIgnoreCase(driverEmail.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Driver not found"));

    if (driver.getRole() == null
            || !driver.getRole().name().equals("DRIVER")) {
        throw new AccessDeniedException(
                "Only drivers can accept rides");
    }

    Ride ride = rideRepository.findByIdForUpdate(rideId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ride not found"));

    if (ride.getStatus() != RideStatus.REQUESTED) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This ride is no longer available");
    }

    ride.setDriver(driver);
    ride.setStatus(RideStatus.ACCEPTED);

    Ride savedRide = rideRepository.save(ride);

    return toResponse(savedRide);
}
@Transactional
public RideResponse startRide(String driverEmail, Long rideId) {

    User driver = userRepository.findByEmailIgnoreCase(driverEmail.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Driver not found"));

    if (driver.getRole() == null
            || !driver.getRole().name().equals("DRIVER")) {
        throw new AccessDeniedException(
                "Only drivers can start rides");
    }

    Ride ride = rideRepository.findByIdForUpdate(rideId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ride not found"));

    if (ride.getDriver() == null
            || !ride.getDriver().getId().equals(driver.getId())) {
        throw new AccessDeniedException(
                "You can only start rides assigned to you");
    }

    if (ride.getStatus() != RideStatus.ACCEPTED) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Only accepted rides can be started");
    }

    ride.setStatus(RideStatus.IN_PROGRESS);

    Ride savedRide = rideRepository.save(ride);

    return toResponse(savedRide);
}

@Transactional
public RideResponse completeRide(String driverEmail, Long rideId) {

    User driver = userRepository.findByEmailIgnoreCase(driverEmail.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Driver not found"));

    if (driver.getRole() == null
            || !driver.getRole().name().equals("DRIVER")) {
        throw new AccessDeniedException(
                "Only drivers can complete rides");
    }

    Ride ride = rideRepository.findByIdForUpdate(rideId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ride not found"));

    if (ride.getDriver() == null
            || !ride.getDriver().getId().equals(driver.getId())) {
        throw new AccessDeniedException(
                "You can only complete rides assigned to you");
    }

    if (ride.getStatus() != RideStatus.IN_PROGRESS) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Only rides in progress can be completed");
    }

    ride.setStatus(RideStatus.COMPLETED);
    ride.setCompletedAt(LocalDateTime.now());

    Ride savedRide = rideRepository.save(ride);

    return toResponse(savedRide);
}
@Transactional(readOnly = true)
public java.util.List<RideResponse> getDriverRides(String driverEmail) {

    User driver = userRepository.findByEmailIgnoreCase(driverEmail.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Driver not found"));

    if (driver.getRole() == null
            || !driver.getRole().name().equals("DRIVER")) {
        throw new AccessDeniedException(
                "Only drivers can view driver ride history");
    }

    return rideRepository
            .findByDriverIdOrderByRequestedAtDesc(driver.getId())
            .stream()
            .map(this::toResponse)
            .toList();
}
@Transactional
public RideResponse cancelRide(String userEmail, Long rideId) {

    User user = userRepository.findByEmailIgnoreCase(userEmail.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"));

    Ride ride = rideRepository.findByIdForUpdate(rideId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ride not found"));

    boolean isPassenger = ride.getPassenger() != null
            && ride.getPassenger().getId().equals(user.getId());

    boolean isAssignedDriver = ride.getDriver() != null
            && ride.getDriver().getId().equals(user.getId());

    if (isPassenger) {

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Passengers can only cancel rides that have not been accepted");
        }

    } else if (isAssignedDriver) {

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Drivers can only cancel accepted rides that have not started");
        }

    } else {
        throw new AccessDeniedException(
                "You are not authorized to cancel this ride");
    }

    ride.setStatus(RideStatus.CANCELLED);

    Ride savedRide = rideRepository.save(ride);

    return toResponse(savedRide);
}
}