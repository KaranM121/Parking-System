package com.example.parkingSystem.controller;

import com.example.parkingSystem.model.LicensePlateObservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObservationControllerTest {

    private ObservationController observationController;

    @BeforeEach
    void setUp() {
        observationController = new ObservationController();
    }

    @Test
    void testCreateObservation() {
        LicensePlateObservation observation = new LicensePlateObservation();
        observation.setLicensePlate("XYZ123");

        ResponseEntity<String> response = observationController.createObservation(observation);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Observation saved successfully", response.getBody());
        assertEquals(1, observationController.observations.size()); // Check that the observation is added
    }

    @Test
    void testGetAllObservations() {
        LicensePlateObservation observation1 = new LicensePlateObservation();
        observation1.setLicensePlate("ABC456");
        observationController.observations.add(observation1);

        LicensePlateObservation observation2 = new LicensePlateObservation();
        observation2.setLicensePlate("DEF789");
        observationController.observations.add(observation2);

        ResponseEntity<List<LicensePlateObservation>> response = observationController.getAllObservations();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllObservations_NoContent() {
        ResponseEntity<List<LicensePlateObservation>> response = observationController.getAllObservations();

        assertEquals(204, response.getStatusCodeValue()); // No content
    }
}
