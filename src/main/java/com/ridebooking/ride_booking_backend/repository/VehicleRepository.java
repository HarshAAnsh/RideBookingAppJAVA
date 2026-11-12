package com.ridebooking.ride_booking_backend.repository;

import com.ridebooking.ride_booking_backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
}
