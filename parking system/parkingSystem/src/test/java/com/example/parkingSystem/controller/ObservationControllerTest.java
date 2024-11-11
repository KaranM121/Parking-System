package com.example.parkingSystem.controller;

import com.example.parkingSystem.model.LicensePlateObservation;
import com.example.parkingSystem.service.ObservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ObservationControllerTest {

    @Mock
    private ObservationService observationService;

    @InjectMocks
    private ObservationController observationController;

    private List<LicensePlateObservation> observations;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        observations = new ArrayList<>();
    }

    @Test
    void testCreateObservation_ValidObservation() {
        LicensePlateObservation observation = mock(LicensePlateObservation.class);
        when(observation.getLicensePlate()).thenReturn("UP12AB1212");
        when(observation.getStreetName()).thenReturn("Java");

        doNothing().when(observationService).observeParkedCars();

        ResponseEntity<String> response = observationController.createObservation(observation);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Observation saved successfully", response.getBody());

        observations.add(observation);
        assertFalse(observations.isEmpty());
        assertEquals(1, observations.size());
    }

    @Test
    void testCreateObservation_NullLicensePlate() {
        LicensePlateObservation observation = new LicensePlateObservation();
        observation.setLicensePlate(null);
        observation.setStreetName("Java");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            observationController.createObservation(observation);
        });

        assertEquals("License plate number cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCreateObservation_EmptyLicensePlate() {
        LicensePlateObservation observation = new LicensePlateObservation();
        observation.setLicensePlate("");
        observation.setStreetName("Java");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            observationController.createObservation(observation);
        });

        assertEquals("License plate number cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCreateObservation_NullStreetName() {
        LicensePlateObservation observation = new LicensePlateObservation();
        observation.setLicensePlate("UP12AB1212");
        observation.setStreetName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            observationController.createObservation(observation);
        });

        assertEquals("Street name cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCreateObservation_EmptyStreetName() {
        LicensePlateObservation observation = new LicensePlateObservation();
        observation.setLicensePlate("UP12AB1212");
        observation.setStreetName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            observationController.createObservation(observation);
        });

        assertEquals("Street name cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testGetAllObservations_NoContent() {
        ResponseEntity<List<LicensePlateObservation>> response = observationController.getAllObservations();

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().isEmpty());
    }

    @Test
    void testGetAllObservations_WithObservations() {
        LicensePlateObservation observation = new LicensePlateObservation();
        observation.setLicensePlate("UP12AB1212");
        observation.setStreetName("Java");

        observations.add(observation);

        ResponseEntity<List<LicensePlateObservation>> response = observationController.getAllObservations();

        assertNotNull(response);
        
    }
}
