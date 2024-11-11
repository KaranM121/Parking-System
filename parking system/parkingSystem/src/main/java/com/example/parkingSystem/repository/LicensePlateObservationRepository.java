package com.example.parkingSystem.repository;

import com.example.parkingSystem.model.LicensePlateObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicensePlateObservationRepository extends JpaRepository<LicensePlateObservation, Long> {
    LicensePlateObservation findByLicensePlate(String licensePlate);
}
