package com.example.parkingSystem.controller;

import com.example.parkingSystem.model.ParkingSession;
import com.example.parkingSystem.repository.ParkingSessionRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            return ResponseEntity.badRequest().body("License plate number cannot be null or empty. Please enter a valid license plate number.");
        }

        // Trim and convert the street name to lowercase
        String streetName = (session.getStreetName() != null) ? session.getStreetName().trim() : null;

        // Validate street name
        if (streetName == null || streetName.isEmpty() || 
            !ALLOWED_STREETS.stream().map(String::toLowerCase).toList().contains(streetName.toLowerCase())) {
            return ResponseEntity.badRequest().body("Street name must be one of the following: Java, Jakarta, Spring, Azure. Please enter a valid street name.");
        }

        ParkingSession existingSession = parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(session.getLicensePlate());
        if (existingSession != null) {
            return ResponseEntity.badRequest().body("Vehicle " + session.getLicensePlate() + " is already in an active parking session. Please deregister first.");
        }

        session.setStartTime(LocalDateTime.now());
        session.setEndTime(null);
        parkingSessionRepository.save(session); 

        return ResponseEntity.ok("Parking session started for vehicle: " + session.getLicensePlate());
    }

    @PutMapping("/unregister")
    public ResponseEntity<String> unregisterParking(@RequestBody ParkingSession sessionRequest) {
        if (sessionRequest.getLicensePlate() == null || sessionRequest.getLicensePlate().isEmpty()) {
            return ResponseEntity.badRequest().body("License plate number cannot be null or empty. Please enter a valid license plate number.");
        }

        ParkingSession session = parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(sessionRequest.getLicensePlate());

        if (session == null) {
            return ResponseEntity.badRequest().body("No active parking session found for vehicle: " + sessionRequest.getLicensePlate());
        }

        session.setEndTime(LocalDateTime.now());

        long durationInMinutes = java.time.Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();

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
                return ResponseEntity.badRequest().body("Unknown street name for vehicle: " + sessionRequest.getLicensePlate());
        }

        long totalCost = durationInMinutes * costPerMinute;

        parkingSessionRepository.save(session);

        return ResponseEntity.ok("Parking session ended for vehicle: " + sessionRequest.getLicensePlate() +
                ". Total cost: " + totalCost + " cents for " + durationInMinutes + " minutes.");
    }


    public static List<String> getAllowedStreets() {
        return ALLOWED_STREETS;
    }
}
