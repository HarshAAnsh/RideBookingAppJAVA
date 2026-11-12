package com.ridebooking.ride_booking_backend.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FareService {

    private static final BigDecimal BASE_FARE = new BigDecimal("40.00");
    private static final BigDecimal FARE_PER_KM = new BigDecimal("12.00");
    private static final BigDecimal EARTH_RADIUS_KM = new BigDecimal("6371.0");

    public BigDecimal calculateEstimatedFare(
            BigDecimal pickupLatitude,
            BigDecimal pickupLongitude,
            BigDecimal destinationLatitude,
            BigDecimal destinationLongitude) {

        double lat1 = Math.toRadians(pickupLatitude.doubleValue());
        double lon1 = Math.toRadians(pickupLongitude.doubleValue());
        double lat2 = Math.toRadians(destinationLatitude.doubleValue());
        double lon2 = Math.toRadians(destinationLongitude.doubleValue());

        double latitudeDifference = lat2 - lat1;
        double longitudeDifference = lon2 - lon1;

        double a = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);

        a = Math.max(0.0, Math.min(1.0, a));

        double distanceKm = 2 * EARTH_RADIUS_KM.doubleValue()
                * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        BigDecimal distance = BigDecimal.valueOf(distanceKm);

        BigDecimal fare = BASE_FARE.add(
                distance.multiply(FARE_PER_KM)
        );

        return fare.setScale(2, RoundingMode.HALF_UP);
    }
}