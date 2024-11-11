package com.example.parkingSystem.service;

import com.example.parkingSystem.model.LicensePlateObservation;
import com.example.parkingSystem.repository.LicensePlateObservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class ObservationServiceImpl implements ObservationService {

    @Autowired
    private LicensePlateObservationRepository observationRepository;

    @Autowired
    private DataSource dataSource; // database connection

    @Override
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void observeParkedCars() {
        try (Connection connection = dataSource.getConnection()) {
            // Query to get all cars currently parked on the street
            String streetQuery = "SELECT license_plate, street_name FROM street";
            try (PreparedStatement streetStmt = connection.prepareStatement(streetQuery);
                 ResultSet streetResultSet = streetStmt.executeQuery()) {

                while (streetResultSet.next()) {
                    String licensePlate = streetResultSet.getString("license_plate");
                    String streetName = streetResultSet.getString("street_name");

                    // Check if the car has an active parking session
                    String sessionQuery = "SELECT end_time FROM parking_session WHERE license_plate = ?";
                    try (PreparedStatement sessionStmt = connection.prepareStatement(sessionQuery)) {
                        sessionStmt.setString(1, licensePlate);
                        ResultSet sessionResultSet = sessionStmt.executeQuery();

                        boolean isActiveSession = false;
                        if (sessionResultSet.next()) {
                            // If the end_time is null, session is active
                            isActiveSession = (sessionResultSet.getTimestamp("end_time") == null);
                        }

                        if (!isActiveSession) {
                            // Car does not have an active session; proceed to add/update fine
                            handleFinedCars(connection, licensePlate, streetName);
                        }
                    }
                }
            }
            System.out.println("Observation of parked cars completed.");
        } catch (SQLException e) {
            throw new RuntimeException("Database operation failed", e); // SQLException will be handled by GlobalExceptionHandler
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred", e); // Other exceptions will also be handled by GlobalExceptionHandler
        }
    }

    private void handleFinedCars(Connection connection, String licensePlate, String streetName) throws SQLException {
        // Check if this car already exists in the finedCars table
        String finedCarQuery = "SELECT fine FROM finedCars WHERE license_plate = ? AND street_name = ?";
        try (PreparedStatement finedCarStmt = connection.prepareStatement(finedCarQuery)) {
            finedCarStmt.setString(1, licensePlate);
            finedCarStmt.setString(2, streetName);
            ResultSet finedCarResult = finedCarStmt.executeQuery();

            if (finedCarResult.next()) {
                // Car already fined; update fine amount by adding 5 cents
                double currentFine = finedCarResult.getDouble("fine");
                double newFine = currentFine + 5;

                String updateFineQuery = "UPDATE finedCars SET fine = ?, observation_date = ? WHERE license_plate = ? AND street_name = ?";
                try (PreparedStatement updateFineStmt = connection.prepareStatement(updateFineQuery)) {
                    updateFineStmt.setDouble(1, newFine);
                    updateFineStmt.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                    updateFineStmt.setString(3, licensePlate);
                    updateFineStmt.setString(4, streetName);
                    updateFineStmt.executeUpdate();
                }
            } else {
                // Car not yet fined; insert initial fine of 20 cents
                String insertFineQuery = "INSERT INTO finedCars (license_plate, street_name, observation_date, fine) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertFineStmt = connection.prepareStatement(insertFineQuery)) {
                    insertFineStmt.setString(1, licensePlate);
                    insertFineStmt.setString(2, streetName);
                    insertFineStmt.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                    insertFineStmt.setDouble(4, 20.0);
                    insertFineStmt.executeUpdate();
                }
            }
        }
    }
}
