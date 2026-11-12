package com.ridebooking.ride_booking_backend.repository;

import com.ridebooking.ride_booking_backend.entity.Driver;
import com.ridebooking.ride_booking_backend.entity.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long>{
    List<Driver> findByStatus(DriverStatus status);
}
