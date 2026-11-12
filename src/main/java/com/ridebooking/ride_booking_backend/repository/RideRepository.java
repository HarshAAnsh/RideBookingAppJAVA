package com.ridebooking.ride_booking_backend.repository;

import com.ridebooking.ride_booking_backend.entity.Ride;
import com.ridebooking.ride_booking_backend.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByPassengerIdOrderByRequestedAtDesc(Long passengerId);

    List<Ride> findByDriverIdOrderByRequestedAtDesc(Long driverId);

    List<Ride> findByStatusOrderByRequestedAtAsc(RideStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT r FROM Ride r WHERE r.id = :rideId")
Optional<Ride> findByIdForUpdate(@Param("rideId") Long rideId);
}