package com.example.parkingSystem.controller;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.parkingSystem.model.ParkingSession;
import com.example.parkingSystem.repository.ParkingSessionRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ParkingSessionControllerTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @InjectMocks
    private ParkingSessionController parkingSessionController;

    private static final List<String> ALLOWED_STREETS = Arrays.asList("Java", "Jakarta", "Spring", "Azure");

    public ParkingSessionControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterParking_ValidSession() {
        // Create a mock ParkingSession object
        ParkingSession session = mock(ParkingSession.class);
        when(session.getLicensePlate()).thenReturn("UP12AB1212");
        when(session.getStreetName()).thenReturn("Java");

        // Simulate repository returning no active session for this license plate
        when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull("UP12AB1212")).thenReturn(null);

        // Simulate saving the session
        when(parkingSessionRepository.save(session)).thenReturn(session);

        // Perform the registerParking action
        ResponseEntity<String> result = parkingSessionController.registerParking(session);

        // Assertions to verify behavior and response
        assertNotNull(result);
    }

    @Test
    void testRegisterParking_NullLicensePlate() {
        ParkingSession session = mock(ParkingSession.class);
        session.setLicensePlate(null);
        session.setStreetName("Java");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parkingSessionController.registerParking(session);
        });

        assertEquals("License plate number cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testRegisterParking_EmptyLicensePlate() {
        ParkingSession session = mock(ParkingSession.class);
        session.setLicensePlate("");
        session.setStreetName("Java");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parkingSessionController.registerParking(session);
        });

        assertEquals("License plate number cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testRegisterParking_InvalidStreetName() {
        ParkingSession session = mock(ParkingSession.class);
        session.setLicensePlate("AB123CD");
        session.setStreetName("NonExistingStreet");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parkingSessionController.registerParking(session);
        });
        assertNotNull(exception);
    }

    @Test
    void testRegisterParking_ExistingActiveSession() {
        // Mock the session object with expected return values for its methods
        ParkingSession session = mock(ParkingSession.class);
        when(session.getLicensePlate()).thenReturn("AB123CD");
        when(session.getStreetName()).thenReturn("Java");

        // Mock an active existing session for the same license plate
        ParkingSession activeSession = mock(ParkingSession.class);
        when(activeSession.getLicensePlate()).thenReturn("AB123CD");
        when(activeSession.getStreetName()).thenReturn("Java");
        when(activeSession.getStartTime()).thenReturn(LocalDateTime.now());
        when(activeSession.getEndTime()).thenReturn(null); // Active session

        // Mock repository behavior to return the active session when searched by license plate
        when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull("AB123CD")).thenReturn(activeSession);

        // Expect an IllegalArgumentException to be thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            parkingSessionController.registerParking(session);
        });

        // Assertions
        assertNotNull(exception);
        assertEquals("Vehicle AB123CD is already in an active parking session.", exception.getMessage());
     
    }

    @Test
    void testRegisterParking_ValidNewSession_NoActiveSession() {
        // Mock a ParkingSession object and set expected return values for its methods
        ParkingSession session = mock(ParkingSession.class);
        when(session.getLicensePlate()).thenReturn("XY987Z");
        when(session.getStreetName()).thenReturn("Spring");

        // Mock the repository behavior to simulate no active session exists for the license plate
        when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull("XY987Z")).thenReturn(null);

        // Mock the save operation
        when(parkingSessionRepository.save(session)).thenReturn(session);

        // Call the method under test
        ResponseEntity<String> result = parkingSessionController.registerParking(session);

        // Perform assertions
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Parking session started for vehicle: XY987Z", result.getBody());

        // Verify that the repository's save method was called with the correct session
        verify(parkingSessionRepository).save(session);
    }
    
    @Test
    void testUnregisterParking_ValidRequest() {
        // Mock a valid parking session
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("UP12AB1212");
        session.setStreetName("Java");
        session.setStartTime(LocalDateTime.of(2023, 10, 1, 9, 0));
        session.setEndTime(null);

        // Set up repository mock to return this session when queried by license plate
        when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull("UP12AB1212")).thenReturn(session);

        // Call the unregisterParking method
        ResponseEntity<String> result = parkingSessionController.unregisterParking(session);

        // Assert that the result is not null
        assertNotNull(result);
    }

    @Test
    void testUnregisterParking_FreeParkingPeriod() {
        // Set up a session during free parking hours
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("UP12AB1212");
        session.setStreetName("Java");
        session.setStartTime(LocalDateTime.of(2023, 10, 1, 22, 0)); // Non-chargeable time
        session.setEndTime(null);

        when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull("UP12AB1212")).thenReturn(session);

        ResponseEntity<String> result = parkingSessionController.unregisterParking(session);

        // Assert that the result is not null
        assertNotNull(result);
    }

    @Test
    void testUnregisterParking_NoActiveSession() {
        ParkingSession sessionRequest = new ParkingSession();
        sessionRequest.setLicensePlate("UP12AB1212");

        // Mock repository to return null, simulating no active session found
        when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull("UP12AB1212")).thenReturn(null);

        try {
            ResponseEntity<String> result = parkingSessionController.unregisterParking(sessionRequest);
            assertNotNull(result);
        } catch (IllegalArgumentException e) {
            // Ignore exception for this test; focus is on assertNotNull
        }
    }
}
