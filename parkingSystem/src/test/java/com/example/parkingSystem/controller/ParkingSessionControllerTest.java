package com.example.parkingSystem.controller;

import com.example.parkingSystem.model.ParkingSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParkingSessionControllerTest {

    private ParkingSessionController parkingSessionController;
    private List<ParkingSession> parkingSessions;

    @BeforeEach
    void setUp() {
        parkingSessionController = new ParkingSessionController() {
            @Override
            public ResponseEntity<String> registerParking(ParkingSession session) {
                if (session.getLicensePlate() == null || session.getLicensePlate().isEmpty()) {
                    return ResponseEntity.badRequest().body("License plate number cannot be null or empty. Please enter a valid license plate number.");
                }

                String streetName = (session.getStreetName() != null) ? session.getStreetName().trim().toLowerCase() : null;

                if (streetName == null || streetName.isEmpty() || 
                    ParkingSessionController.getAllowedStreets().stream().map(String::toLowerCase).toList().contains(streetName)) {
                    return ResponseEntity.badRequest().body("Street name must be one of the following: Java, Jakarta, Spring, Azure. Please enter a valid street name.");
                }

                for (ParkingSession existingSession : parkingSessions) {
                    if (existingSession.getLicensePlate().equals(session.getLicensePlate()) && existingSession.getEndTime() == null) {
                        return ResponseEntity.badRequest().body("Vehicle " + session.getLicensePlate() + " is already in an active parking session. Please deregister first.");
                    }
                }

                session.setStartTime(LocalDateTime.now());
                session.setEndTime(null);
                parkingSessions.add(session);
                
                return ResponseEntity.ok("Parking session started for vehicle: " + session.getLicensePlate());
            }

        };
        parkingSessions = new ArrayList<>();
    }

    @Test
    void testRegisterParking_NewSession() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("ABC123");
        session.setStreetName("Java");

        ResponseEntity<String> response = parkingSessionController.registerParking(session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Parking session started for vehicle: ABC123", response.getBody());
        assertEquals(1, parkingSessions.size());
    }

    @Test
    void testRegisterParking_ExistingSession() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("ABC123");
        session.setStreetName("Java");
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(null);
        parkingSessions.add(session);

        ParkingSession newSession = new ParkingSession();
        newSession.setLicensePlate("ABC123");
        newSession.setStreetName("Java");

        ResponseEntity<String> response = parkingSessionController.registerParking(newSession);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Vehicle ABC123 is already in an active parking session. Please deregister first.", response.getBody());
    }

    @Test
    void testRegisterParking_NullLicensePlate() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate(null);
        session.setStreetName("Java");

        ResponseEntity<String> response = parkingSessionController.registerParking(session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("License plate number cannot be null or empty. Please enter a valid license plate number.", response.getBody());
    }

    @Test
    void testRegisterParking_EmptyLicensePlate() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("");
        session.setStreetName("Java");

        ResponseEntity<String> response = parkingSessionController.registerParking(session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("License plate number cannot be null or empty. Please enter a valid license plate number.", response.getBody());
    }

    @Test
    void testRegisterParking_NullStreetName() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("ABC123");
        session.setStreetName(null);

        ResponseEntity<String> response = parkingSessionController.registerParking(session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Street name must be one of the following: Java, Jakarta, Spring, Azure. Please enter a valid street name.", response.getBody());
    }

    @Test
    void testRegisterParking_EmptyStreetName() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("ABC123");
        session.setStreetName("");

        ResponseEntity<String> response = parkingSessionController.registerParking(session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Street name must be one of the following: Java, Jakarta, Spring, Azure. Please enter a valid street name.", response.getBody());
    }

    @Test
    void testRegisterParking_InvalidStreetName() {
        ParkingSession session = new ParkingSession();
        session.setLicensePlate("ABC123");
        session.setStreetName("InvalidStreet");

        ResponseEntity<String> response = parkingSessionController.registerParking(session);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Street name must be one of the following: Java, Jakarta, Spring, Azure. Please enter a valid street name.", response.getBody());
    }

}
