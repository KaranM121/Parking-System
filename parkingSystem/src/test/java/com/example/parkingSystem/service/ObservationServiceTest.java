package com.example.parkingSystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback
public class ObservationServiceTest {

    @Autowired
    private ObservationServiceImpl observationService;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            // Initialize data in street and parking_session tables
            String initStreet = "INSERT INTO street (license_plate, street_name) VALUES ('ABC123', 'Java Street'), ('XYZ456', 'Spring Street')";
            String initSession = "INSERT INTO parking_session (license_plate, end_time) VALUES ('ABC123', NULL), ('XYZ456', '2023-10-10 10:10:10')";
            connection.prepareStatement(initStreet).executeUpdate();
            connection.prepareStatement(initSession).executeUpdate();
        }
    }

    @Test
    void testObserveParkedCars() throws SQLException {
        observationService.observeParkedCars();

        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT fine FROM finedCars WHERE license_plate = 'XYZ456' AND street_name = 'Spring Street'";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    double fine = resultSet.getDouble("fine");
                    assertEquals(20.0, fine, "Initial fine should be 20.0 for non-compliant car.");
                }
            }
        }
    }

    @Test
    void testObserveParkedCars_UpdateFine() throws SQLException {
        // Insert initial fine for car 'XYZ456' on 'Spring Street'
        try (Connection connection = dataSource.getConnection()) {
            String insertFine = "INSERT INTO finedCars (license_plate, street_name, observation_date, fine) VALUES ('XYZ456', 'Spring Street', NOW(), 20.0)";
            connection.prepareStatement(insertFine).executeUpdate();
        }

        // Run observation
        observationService.observeParkedCars();

        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT fine FROM finedCars WHERE license_plate = 'XYZ456' AND street_name = 'Spring Street'";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    double updatedFine = resultSet.getDouble("fine");
                    assertEquals(25.0, updatedFine, "Fine should be updated by 5 to 25.0 for already fined car.");
                }
            }
        }
    }
}
