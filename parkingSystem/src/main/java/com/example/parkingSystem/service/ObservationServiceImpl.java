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
            String query = "SELECT license_plate, street_name FROM street"; 
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String licensePlate = resultSet.getString("license_plate");
                    String streetName = resultSet.getString("street_name"); 

        
                    LicensePlateObservation observation = new LicensePlateObservation(licensePlate, streetName, LocalDateTime.now());
                    observationRepository.save(observation);
                }
            }
            System.out.println("Parked cars observed and data saved.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
