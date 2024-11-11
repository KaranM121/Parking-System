package com.example.parkingSystem.controller;

import com.example.parkingSystem.model.ParkingSession;
import com.example.parkingSystem.repository.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingSessionController {

    private static final List<String> ALLOWED_STREETS = Arrays.asList("Java", "Jakarta", "Spring", "Azure");

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerParking(@RequestBody ParkingSession session) {
        if (session.getLicensePlate() == null || session.getLicensePlate().isEmpty()) {
            throw new IllegalArgumentException("License plate number cannot be null or empty.");
        }

        String streetName = (session.getStreetName() != null) ? session.getStreetName().trim() : null;
        if (streetName == null || streetName.isEmpty() || 
            !ALLOWED_STREETS.stream().map(String::toLowerCase).toList().contains(streetName.toLowerCase())) {
            throw new IllegalArgumentException("Street name must be one of: Java, Jakarta, Spring, Azure.");
        }

        ParkingSession existingSession = parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(session.getLicensePlate());
        if (existingSession != null) {
            throw new IllegalArgumentException("Vehicle " + session.getLicensePlate() + 
                    " is already in an active parking session.");
        }

        session.setStartTime(LocalDateTime.now());
        session.setEndTime(null);
        parkingSessionRepository.save(session);

        return ResponseEntity.ok("Parking session started for vehicle: " + session.getLicensePlate());
    }

    @PutMapping("/unregister")
    public ResponseEntity<String> unregisterParking(@RequestBody ParkingSession sessionRequest) {
        if (sessionRequest.getLicensePlate() == null || sessionRequest.getLicensePlate().isEmpty()) {
            throw new IllegalArgumentException("License plate number cannot be null or empty.");
        }

        ParkingSession session = parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(sessionRequest.getLicensePlate());
        if (session == null) {
            throw new IllegalArgumentException("No active parking session found for vehicle: " + sessionRequest.getLicensePlate());
        }

        session.setEndTime(LocalDateTime.now());
        parkingSessionRepository.save(session);

        long durationInMinutes = java.time.Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();

        long chargeableMinutes = 0;
        LocalDateTime currentTime = session.getStartTime();
        while (currentTime.isBefore(session.getEndTime())) {
            if (!(currentTime.toLocalTime().isAfter(LocalTime.of(21, 0)) || currentTime.toLocalTime().isBefore(LocalTime.of(8, 0))) 
                && currentTime.getDayOfWeek() != DayOfWeek.SUNDAY) {
                chargeableMinutes++;
            }
            currentTime = currentTime.plusMinutes(1);
        }

        if (chargeableMinutes == 0) {
            return ResponseEntity.ok("Parking session ended for vehicle: " + sessionRequest.getLicensePlate() +
                    ". Total cost: 0 cents (Free parking period).");
        }

        int costPerMinute;
        switch (session.getStreetName().toLowerCase()) {
            case "java":
                costPerMinute = 15;
                break;
            case "jakarta":
                costPerMinute = 13;
                break;
            case "spring":
            case "azure":
                costPerMinute = 10;
                break;
            default:
                throw new IllegalArgumentException("Unknown street name for vehicle: " + sessionRequest.getLicensePlate());
        }

        long totalCost = chargeableMinutes * costPerMinute;
        parkingSessionRepository.save(session);

        return ResponseEntity.ok("Parking session ended for vehicle: " + sessionRequest.getLicensePlate() +
                ". Total cost: " + totalCost + " cents for " + chargeableMinutes + " chargeable minutes.");
    }

    public static List<String> getAllowedStreets() {
        return ALLOWED_STREETS;
    }
}
