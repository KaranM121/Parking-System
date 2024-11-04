package com.example.parkingSystem.service;

import com.example.parkingSystem.model.ParkingSession;
import java.util.List;

public interface ParkingSessionService {

    ParkingSession saveParkingSession(ParkingSession session);

    ParkingSession findByLicensePlate(String licensePlate);

    List<ParkingSession> findAllParkingSessions();
    
    ParkingSession findByLicensePlateAndEndTimeIsNull(String licensePlate);
}
