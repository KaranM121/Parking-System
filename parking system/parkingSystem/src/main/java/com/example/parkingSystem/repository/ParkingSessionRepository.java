package com.example.parkingSystem.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.parkingSystem.model.ParkingSession;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {
    ParkingSession findByLicensePlateAndEndTimeIsNull(String licensePlate);

	ParkingSession findByLicensePlateAndEndTimeIsNotNull(String licensePlate);
}
