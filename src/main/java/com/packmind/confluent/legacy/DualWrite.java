package com.packmind.confluent.legacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerConfig;

public class DualWrite {

    public static void main(String[] args) {
        // Database URL, username, and password
        String jdbcUrl = "jdbc:mysql://localhost:3306/mydatabase";
        String dbUser = "username";
        String dbPassword = "password";

        // Kafka properties
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            // Start a database transaction
            connection.setAutoCommit(false);

            // 1. Write operation to the SQL database
            String sqlInsert = "INSERT INTO mytable (id, data) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
                statement.setInt(1, 1);
                statement.setString(2, "some data");
                statement.executeUpdate();

                // Simulating a possible error during the database write
                // throw new SQLException("Database write failed");

                // 2. Publish an event to Kafka
                ProducerRecord<String, String> record = new ProducerRecord<>("my-topic", "key", "event data");
                RecordMetadata metadata = producer.send(record).get();

                // Simulating a possible error during the Kafka publish
                // throw new ExecutionException(new Throwable("Kafka publish failed"));

                // If both operations succeed, commit the transaction
                connection.commit();
                System.out.println("Both operations succeeded.");
            } catch (SQLException | ExecutionException e) {
                // If any operation fails, roll back the transaction
                connection.rollback();
                System.err.println("An error occurred, transaction rolled back: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }

}
