package com.example.parkingSystem.controller;

import com.example.parkingSystem.model.LicensePlateObservation;
import com.example.parkingSystem.service.ObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/observations")
public class ObservationController {

    @Autowired
    private ObservationService observationService;
    
    
    //Junit testing
    List<LicensePlateObservation> observations = new ArrayList<>();

    @PostMapping
    public ResponseEntity<String> createObservation(@RequestBody LicensePlateObservation observation) {
        // Validate license plate and street name; throws IllegalArgumentException if invalid
        if (observation.getLicensePlate() == null || observation.getLicensePlate().isEmpty()) {
            throw new IllegalArgumentException("License plate number cannot be null or empty.");
        }
        if (observation.getStreetName() == null || observation.getStreetName().isEmpty()) {
            throw new IllegalArgumentException("Street name cannot be null or empty.");
        }

        observationService.observeParkedCars();
        
        observations.add(observation);

        return ResponseEntity.ok("Observation saved successfully");
    }

    
    //Junit testing
    @GetMapping
    public ResponseEntity<List<LicensePlateObservation>> getAllObservations() {
        if (observations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(observations);
    }
}
