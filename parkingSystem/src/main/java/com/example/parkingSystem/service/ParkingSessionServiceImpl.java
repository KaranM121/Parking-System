package com.example.parkingSystem.service;

import com.example.parkingSystem.model.ParkingSession;
import com.example.parkingSystem.repository.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSessionServiceImpl implements ParkingSessionService {

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Override
    public ParkingSession saveParkingSession(ParkingSession session) {
        return parkingSessionRepository.save(session);
    }

    @Override
    public ParkingSession findByLicensePlate(String licensePlate) {
        return parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(licensePlate);
    }

    @Override
    public List<ParkingSession> findAllParkingSessions() {
        return parkingSessionRepository.findAll();
    }

    @Override
    public ParkingSession findByLicensePlateAndEndTimeIsNull(String licensePlate) {
        return parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(licensePlate);
    }
}
