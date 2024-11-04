package com.example.parkingSystem.repository;

import com.example.parkingSystem.model.LicensePlateObservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicensePlateObservationRepository extends JpaRepository<LicensePlateObservation, Long> {
}
