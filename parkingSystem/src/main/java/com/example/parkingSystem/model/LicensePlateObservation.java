package com.example.parkingSystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "license_plate_observation")
public class LicensePlateObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(name = "street_name", nullable = false)
    private String streetName;

    @Column(name = "observation_date", nullable = false)
    private LocalDateTime observationDate;

   
    public LicensePlateObservation() {}

    public LicensePlateObservation(String licensePlate, String streetName, LocalDateTime observationDate) {
        this.licensePlate = licensePlate;
        this.streetName = streetName;
        this.observationDate = observationDate;
    }

 
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public LocalDateTime getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(LocalDateTime observationDate) {
        this.observationDate = observationDate;
    }
}
